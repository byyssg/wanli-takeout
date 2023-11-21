package com.wsj.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsj.takeout.common.R;
import com.wsj.takeout.dto.DishDto;
import com.wsj.takeout.dto.SetmealDto;
import com.wsj.takeout.entity.Category;
import com.wsj.takeout.entity.Dish;
import com.wsj.takeout.entity.Setmeal;
import com.wsj.takeout.entity.SetmealDish;
import com.wsj.takeout.service.CategoryService;
import com.wsj.takeout.service.DishService;
import com.wsj.takeout.service.SetmealDishService;
import com.wsj.takeout.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //条件查询
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //排序查询
        queryWrapper.orderByDesc(Setmeal ::getUpdateTime);
        //调用分页查询
        setmealService.page(pageInfo,queryWrapper);
        //拷贝并忽略records
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            //新建个对象
            SetmealDto setmealDto = new SetmealDto();
            //拷贝进新对象
            BeanUtils.copyProperties(item,setmealDto);
            //得到菜品分类id
            Long categoryId = item.getCategoryId();
            //根据菜品分类id查出category数据表
            Category category = categoryService.getById(categoryId);
            if(category !=null){
                //得到分类名称
                String categoryName = category.getName();
                //在setmealDto中设置分类名称
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());//收集
        //存入分类名称
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 对套餐批量或者是单个 进行停售或者是起售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(queryWrapper);
        list.stream().map((item)->{
            if(item!=null){
                item.setStatus(status);
                setmealService.updateById(item);
            }
            return item;
        }).collect(Collectors.toList());
        return R.success("售卖状态修改成功");
    }

    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐数据
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("套餐修改成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    private R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,
                Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,
                Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 移动端点击套餐图片查看套餐具体内容
     * 这里返回的是dto 对象，因为前端需要copies这个属性
     * 前端主要要展示的信息是:套餐中菜品的基本信息，图片，菜品描述，以及菜品的份数
     * @param SetmealId
     * @return
     */
    //这里前端是使用路径来传值的，要注意，不然你前端的请求都接收不到，就有点尴尬哈
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dish(@PathVariable("id") Long SetmealId){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,SetmealId);
        //获取套餐里面的所有菜品  这个就是SetmealDish表里面的数据
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        List<DishDto> dishDtos = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //其实这个BeanUtils的拷贝是浅拷贝，这里要注意一下
            BeanUtils.copyProperties(item, dishDto);
            //这里是为了把套餐中的菜品的基本信息填充到dto中，比如菜品描述，菜品图片等菜品的基本信息
            Long dishId = item.getDishId();
            Dish dish = dishService.getById(dishId);
            BeanUtils.copyProperties(dish, dishDto);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtos);
    }
}
