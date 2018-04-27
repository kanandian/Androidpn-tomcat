package org.androidpn.server.service.impl;

import org.androidpn.server.dao.TakeoutOrderDao;
import org.androidpn.server.model.TakeoutOrder;
import org.androidpn.server.service.TakeoutOrderService;

import java.util.List;

public class TakeoutOrderServiceImpl implements TakeoutOrderService {
    private TakeoutOrderDao takeoutOrderDao;

    @Override
    public void saveTakeoutOrder(TakeoutOrder takeoutOrder) {
        takeoutOrderDao.saveTakeoutOrder(takeoutOrder);
    }

    @Override
    public TakeoutOrder getTakeoutOrderById(String orderId) {
        return takeoutOrderDao.getTakeoutOrderById(Long.parseLong(orderId));
    }

    @Override
    public List<TakeoutOrder> getTakeoutOrderListByBussinessId(String bussinessId) {
        return takeoutOrderDao.getTakeoutOrderListByBussinessId(Long.parseLong(bussinessId));
    }

    @Override
    public List<TakeoutOrder> getTakeourOrderListByUserName(String userName) {
        return takeoutOrderDao.getTakeourOrderListByUserName(userName);
    }

    public TakeoutOrderDao getTakeoutOrderDao() {
        return takeoutOrderDao;
    }

    public void setTakeoutOrderDao(TakeoutOrderDao takeoutOrderDao) {
        this.takeoutOrderDao = takeoutOrderDao;
    }
}
