package org.androidpn.server.dao;

import org.androidpn.server.model.TakeoutOrder;

import java.util.List;

public interface TakeoutOrderDao {

    public void saveTakeoutOrder(TakeoutOrder takeoutOrder);
    public TakeoutOrder getTakeoutOrderById(long orderId);
    public List<TakeoutOrder> getTakeoutOrderListByBussinessId(long bussinessId);
    public List<TakeoutOrder> getTakeourOrderListByUserName(String userName);


}
