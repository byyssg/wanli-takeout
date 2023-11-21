package com.wsj.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.takeout.entity.AddressBook;
import com.wsj.takeout.mapper.AddressBookMapper;
import com.wsj.takeout.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
        implements AddressBookService {
}
