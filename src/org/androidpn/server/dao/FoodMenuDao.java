package org.androidpn.server.dao;

import org.androidpn.server.model.FoodMenu;

import java.util.List;

public interface FoodMenuDao {
    public List<FoodMenu> getFoodMenusByBussinessId(long bussinessId);
    public void addFoodMenu(List<FoodMenu> foodMenuList);

}
