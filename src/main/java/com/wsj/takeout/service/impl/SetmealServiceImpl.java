package com.wsj.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.takeout.common.CustomException;
import com.wsj.takeout.dto.SetmealDto;
import com.wsj.takeout.entity.Setmeal;
import com.wsj.takeout.entity.SetmealDish;
import com.wsj.takeout.mapper.SetmealMapper;
import com.wsj.takeout.service.SetmealDishService;
import com.wsj.takeout.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //Select count(*) form setmeal where id in(1,2,3) and status = 1
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        //serviceImpl框架里的this.count方法
        long count = this.count(queryWrapper);
        if(count > 0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除！");
        }
        //如果可以删除，先删除套餐表中的数据--setmeal
        this.removeByIds(ids);
        //delete from setmeal_dish where setmeal_id in (1,2,3）
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据----setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);
    }

    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //查询套餐基本信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        //查询当前套餐对应的关联关系
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     * 修改套餐数据
     * @param setmealDto
     */
    //为了不把问题复杂化，我是先把相关的setmealDish内容移除然后再重新添加，
    //这样就可以不用考虑dish重复的问题和哪些修改哪些没修改；
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(queryWrapper);
        //为setmeal_dish表填充相关的属性
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        //批量把setmealDish保存到setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);
        setmealService.updateById(setmealDto);
    }
}
