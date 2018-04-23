package org.androidpn.server.service;

import org.androidpn.server.model.FoodMenu;

import java.util.List;

public interface FoodMenuService {

    public List<FoodMenu> getFoodMenusByBussinessId(String bussinessId);
    public void addFoodMenu(List<FoodMenu> foodMenuList);
}
