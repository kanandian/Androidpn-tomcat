package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.AdminHandler;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.TakeoutOrderService;
import org.androidpn.server.util.ResultModel;

public class OrderStatusHandler implements AdminHandler {

    private TakeoutOrderService takeoutOrderService;

    public OrderStatusHandler() {
        takeoutOrderService = ServiceLocator.getTakeoutOrderService();
    }

    @Override
    public ResultModel handle(String title, String content) {
        int code = Integer.parseInt(content);
        return takeoutOrderService.updateTakeoutOrderStatus(title, code);
    }
}
