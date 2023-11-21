package com.wsj.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.takeout.entity.User;
import com.wsj.takeout.mapper.UserMapper;
import com.wsj.takeout.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
