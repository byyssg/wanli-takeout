package com.wsj.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.takeout.entity.SetmealDish;
import com.wsj.takeout.mapper.SetmealDishMapper;
import com.wsj.takeout.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish>
        implements SetmealDishService {
}
