package com.wsj.takeout.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.takeout.entity.Dish;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class GetDataFromExcel {
    @Autowired
    private DishService dishService;
    public static final String path = "D:\\java\\wanli_take_out-master\\excel\\test.xlsx";

    public List<Map<String, Object>> getData() throws Exception {
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.select("name", "count");
        List<Dish> list = dishService.list(wrapper);
        List<Map<String, Object>> maps = new ArrayList<>();
        for (Dish dish : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", dish.getName());
            map.put("count", dish.getCount());
            maps.add(map);
        }
        System.out.println(maps);
        return maps;

//        File file = new File(path);
//
//        FileInputStream input = new FileInputStream(file);
//
//        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
//        // 1.获取上传文件输入流
//        InputStream inputStream = null;
//
//        inputStream = multipartFile.getInputStream();
//
//        // 2.应用HUtool ExcelUtil获取ExcelReader指定输入流和sheet
//        ExcelReader excelReader = ExcelUtil.getReader(inputStream, "sheet1");
//        // 可以加上表头验证
//        // 3.读取第二行到最后一行数据
//        //List<List<Object>> read = excelReader.read(1, excelReader.getRowCount());
//        return excelReader.readAll();
    }
}
