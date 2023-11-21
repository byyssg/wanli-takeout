package com.wsj.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsj.takeout.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    void clean();
}
