package org.androidpn.server.service.impl;

import org.androidpn.server.dao.FoodMenuDao;
import org.androidpn.server.model.FoodMenu;
import org.androidpn.server.service.FoodMenuService;

import java.util.ArrayList;
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

    @Override
    public void deleteFoodMenuByMenuId(String menuId) {
        foodMenuDao.deleteFoodMenuByMenuId(Long.parseLong(menuId));
    }

    @Override
    public void addFoodMenu(FoodMenu foodMenu) {
        List<FoodMenu> foodMenuList = new ArrayList<FoodMenu>();
        foodMenuList.add(foodMenu);

        foodMenuDao.addFoodMenu(foodMenuList);
    }

    public FoodMenuDao getFoodMenuDao() {
        return foodMenuDao;
    }

    public void setFoodMenuDao(FoodMenuDao foodMenuDao) {
        this.foodMenuDao = foodMenuDao;
    }
}
