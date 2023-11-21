package com.wsj.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsj.takeout.entity.Orders;

public interface OrderService extends IService<Orders> {
    //用户下单
    void submit(Orders orders);

}
