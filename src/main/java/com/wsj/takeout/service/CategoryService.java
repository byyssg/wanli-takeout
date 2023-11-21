package com.wsj.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsj.takeout.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
