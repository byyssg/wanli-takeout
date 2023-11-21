package com.wsj.takeout.dto;

import com.wsj.takeout.entity.Setmeal;
import com.wsj.takeout.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
