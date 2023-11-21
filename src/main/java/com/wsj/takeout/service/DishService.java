package com.wsj.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsj.takeout.dto.DishDto;
import com.wsj.takeout.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味信息，需要操作两张表dish、dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新对应的口味信息
    void updateWithFlavor(DishDto dishDto);

    //根据传过来的id批量或者是单个的删除菜品
    void deleteByIds(List<Long> ids);
}
