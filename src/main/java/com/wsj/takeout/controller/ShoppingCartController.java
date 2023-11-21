package com.wsj.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wsj.takeout.common.BaseContext;
import com.wsj.takeout.common.R;
import com.wsj.takeout.entity.ShoppingCart;
import com.wsj.takeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车管理
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if (dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart ::getDishId,dishId);
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart ::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询当前菜品或者套餐是否在购物车中
        //select*from shopping_cart where user_id=? and dish_id/setmeal_id =?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne != null) {
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne=shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //SQL:delete from shopping_cart where user_id=?
        shoppingCartService.clean();
        return R.success("清空购物车成功");
    }

    /**
     * 购物车的套餐或者是菜品数量减少设置
     * @param shoppingCart
     * @return
     */
    @Transactional
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //查出当前用户id
        queryWrapper.eq(ShoppingCart ::getUserId,BaseContext.getCurrentId());
        if (shoppingCart.getDishId() != null) {
            //代表数量减少的是菜品数量
            queryWrapper.eq(ShoppingCart ::getDishId,shoppingCart.getDishId());
        }else {
            //代表数量减少的是套餐数量
            queryWrapper.eq(ShoppingCart ::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number-1);
            if(cartServiceOne.getNumber()>0){
                //对数据进行更新操作
                shoppingCartService.updateById(cartServiceOne);
            }else if (cartServiceOne.getNumber()==0){
                //如果购物车的菜品数量减为0，那么就把菜品从购物车删除
                shoppingCartService.removeById(cartServiceOne);
            }else if(cartServiceOne.getNumber()<0){
                return R.error("操作异常");
            }
        }
        return R.success(cartServiceOne);
    }
}
