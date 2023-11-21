package com.wsj.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.takeout.entity.Employee;
import com.wsj.takeout.mapper.EmployeeMapper;
import com.wsj.takeout.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
        implements EmployeeService {
}
