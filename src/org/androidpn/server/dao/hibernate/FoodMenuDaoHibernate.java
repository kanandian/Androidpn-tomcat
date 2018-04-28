package org.androidpn.server.dao.hibernate;

import org.androidpn.server.dao.FoodMenuDao;
import org.androidpn.server.model.FoodMenu;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class FoodMenuDaoHibernate extends HibernateDaoSupport implements FoodMenuDao {
    @Override
    public List<FoodMenu> getFoodMenusByBussinessId(long bussinessId) {
        return getHibernateTemplate().find("from FoodMenu f where f.bussinessId = ?", bussinessId);
    }

    @Override
    public void addFoodMenu(List<FoodMenu> foodMenuList) {
        for (FoodMenu foodMenu : foodMenuList) {
            getHibernateTemplate().saveOrUpdate(foodMenu);
        }
        getHibernateTemplate().flush();
    }

    @Override
    public void deleteFoodMenuByMenuId(long menuId) {
        FoodMenu foodMenu = new FoodMenu();
        foodMenu.setMenuId(menuId);
        getHibernateTemplate().delete(foodMenu);
    }
}
