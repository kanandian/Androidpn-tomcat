package org.androidpn.server.dao;

import org.androidpn.server.model.TakeoutOrder;

import java.util.List;

public interface TakeoutOrderDao {

    public void saveTakeoutOrder(TakeoutOrder takeoutOrder);
    public List<TakeoutOrder> getTakeoutOrderListByBussinessId(long bussinessId);


}
