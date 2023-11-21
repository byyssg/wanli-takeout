package com.wsj.takeout.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.takeout.common.R;
import com.wsj.takeout.entity.Dish;
import com.wsj.takeout.service.DishService;
import com.wsj.takeout.service.GetDataFromExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.*;

@Controller
@RequestMapping(value = "/backend/page/eChart")
public class EChartsController {

    @Autowired
    private GetDataFromExcel getDataFromExcel;
    @Autowired
    private DishService dishService;
    public static final String path= System.getProperty("user.home") + "/Desktop/菜品销量.xlsx";


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getList() throws Exception {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = getDataFromExcel.getData();
        map.put("msg", "ok");
        map.put("data", list);
        list.forEach(System.out::println);

        return map;
    }

    @RequestMapping(value = "excel", method = RequestMethod.GET)
    public R<String> getExcel(){
        R<String> r = new R<>();

        File file = new File(path);
        if(file.exists()) {
            r.setCode(0);
            r.setMsg("文件已导出");
            return r;
        }
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.select("name", "count");
        List<Dish> list = dishService.list(wrapper);
        ArrayList<Map<String, Object>> rows = new ArrayList<>();
        for (Dish dish : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("菜品", dish.getName());
            row.put("销量", dish.getCount());
            rows.add(row);
        }

        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter(path);
        // 合并单元格后的标题行，使用默认标题样式
        writer.merge(1 ,"菜品销量统计");
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(rows,true);
        // 关闭writer，释放内存
        writer.close();
        r.setCode(1);
        r.setData("文件导出成功");
        return r;
    }
}
