package org.androidpn.server.service.impl;

import org.androidpn.server.dao.FoodMenuDao;
import org.androidpn.server.model.FoodMenu;
import org.androidpn.server.service.FoodMenuService;

import java.util.List;

public class FoodMenuServiceImpl implements FoodMenuService {

    private FoodMenuDao foodMenuDao;

    @Override
    public List<FoodMenu> getFoodMenusByBussinessId(String bussinessId) {
        return foodMenuDao.getFoodMenusByBussinessId(Long.parseLong(bussinessId));
    }

    @Override
    public void addFoodMenu(List<FoodMenu> foodMenuList) {
        foodMenuDao.addFoodMenu(foodMenuList);
    }

    public FoodMenuDao getFoodMenuDao() {
        return foodMenuDao;
    }

    public void setFoodMenuDao(FoodMenuDao foodMenuDao) {
        this.foodMenuDao = foodMenuDao;
    }
}
