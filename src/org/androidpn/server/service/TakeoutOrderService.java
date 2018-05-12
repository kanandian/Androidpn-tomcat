package org.androidpn.server.service;

import org.androidpn.server.dao.TakeoutOrderDao;
import org.androidpn.server.model.TakeoutOrder;
import org.androidpn.server.util.ResultModel;

import java.util.List;

public interface TakeoutOrderService {

    public void saveTakeoutOrder(TakeoutOrder takeoutOrder);
    public TakeoutOrder getTakeoutOrderById(String orderId);
    public List<TakeoutOrder> getTakeoutOrderListByBussinessId(String bussinessId);
    public List<TakeoutOrder> getTakeourOrderListByUserName(String userName);

    public ResultModel updateTakeoutOrderStatus(String orderId, int code);


}
