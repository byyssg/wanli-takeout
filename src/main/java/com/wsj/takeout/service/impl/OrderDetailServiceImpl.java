package com.wsj.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.takeout.entity.OrderDetail;
import com.wsj.takeout.mapper.OrderDetailMapper;
import com.wsj.takeout.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
