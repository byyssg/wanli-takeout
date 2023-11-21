package com.wsj.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wsj.takeout.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
