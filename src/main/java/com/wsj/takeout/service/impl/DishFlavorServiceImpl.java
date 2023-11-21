package com.wsj.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.takeout.entity.DishFlavor;
import com.wsj.takeout.mapper.DishFlavorMapper;
import com.wsj.takeout.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}
