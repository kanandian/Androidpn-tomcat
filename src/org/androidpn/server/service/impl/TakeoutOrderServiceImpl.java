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
    public List<TakeoutOrder> getTakeoutOrderListByBussinessId(String bussinessId) {
        return takeoutOrderDao.getTakeoutOrderListByBussinessId(Long.parseLong(bussinessId));
    }

    public TakeoutOrderDao getTakeoutOrderDao() {
        return takeoutOrderDao;
    }

    public void setTakeoutOrderDao(TakeoutOrderDao takeoutOrderDao) {
        this.takeoutOrderDao = takeoutOrderDao;
    }
}
