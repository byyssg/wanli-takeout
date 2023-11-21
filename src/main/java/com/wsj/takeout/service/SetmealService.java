package com.wsj.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsj.takeout.dto.SetmealDto;
import com.wsj.takeout.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //新增套餐，同时需要保存套餐和菜品的关联关系
    void saveWithDish(SetmealDto setmealDto);

    //删除套餐，同时需要删除套餐和菜品的关联数据
    void removeWithDish(List<Long> ids);

    //更新套餐信息，同时更新对应的关联关系
    void updateWithDish(SetmealDto setmealDto);

    //回显套餐数据：根据套餐id查询套餐
    SetmealDto getByIdWithDish(Long id);
}
