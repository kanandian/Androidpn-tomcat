package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.TakeoutOrder;
import org.androidpn.server.model.TakeoutOrderItem;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.TakeoutOrderService;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

import javax.print.DocFlavor;
import java.util.List;

public class TakeoutOrderInquiryHandler implements InquiryHandler {

    private static final String NAMESPACE = "androidpn:order:takeout";

    private TakeoutOrderService takeoutOrderService;

    private Element probeResponse;

    public TakeoutOrderInquiryHandler() {
        takeoutOrderService = ServiceLocator.getTakeoutOrderService();
        probeResponse = DocumentHelper.createElement(QName.get("order",
                NAMESPACE));
    }


    @Override
    public IQ handle(IQ reply, String title) {
        if (title.contains("all")) {
            String userName = title.split("[:]")[1];
            List<TakeoutOrder> takeoutOrderList = takeoutOrderService.getTakeourOrderListByUserName(userName);

            for (TakeoutOrder takeoutOrder : takeoutOrderList) {
                addTakeoutOrder(takeoutOrder);
            }
        } else if (title.contains("bussiness")) {
            String bussinessId = title.split("[:]")[1];

            List<TakeoutOrder> takeoutOrderList = takeoutOrderService.getTakeoutOrderListByBussinessId(bussinessId);

            for (TakeoutOrder takeoutOrder : takeoutOrderList) {
                addTakeoutOrder(takeoutOrder);
            }

        } else {
            TakeoutOrder takeoutOrder = takeoutOrderService.getTakeoutOrderById(title);
            for (TakeoutOrderItem takeoutOrderItem : takeoutOrder.getTakeoutOrderItemList()) {
                addTakeoutOrderItem(takeoutOrderItem);
            }
        }

        reply.setChildElement(probeResponse);
        return reply;
    }

    public void addTakeoutOrder(TakeoutOrder takeoutOrder) {
        Element item = DocumentHelper.createElement(QName.get("item",
                NAMESPACE));
        item.addAttribute("orderid", String.valueOf(takeoutOrder.getOrderId()));
        item.addAttribute("bussinessname", takeoutOrder.getBussinessName());
        item.addAttribute("address", takeoutOrder.getAddress());
        item.addAttribute("bussinessid", String.valueOf(takeoutOrder.getBussinessId()));
        item.addAttribute("mobile", takeoutOrder.getMobile());
        item.addAttribute("note", takeoutOrder.getNote());
        item.addAttribute("totalprice", String.valueOf(takeoutOrder.getTotalPrice()));
        item.addAttribute("itemcount", String.valueOf(takeoutOrder.getTakeoutOrderItemList().size()));
        item.addAttribute("orderstatus", String.valueOf(takeoutOrder.getOrderStatus()));
        item.addAttribute("createtime", takeoutOrder.getCreateTime());

        if (takeoutOrder.getTakeoutOrderItemList().size() >= 1) {
            item.addAttribute("firstfoodname", takeoutOrder.getTakeoutOrderItemList().get(0).getFoodName());
        }

        probeResponse.add(item);
    }

    public void addTakeoutOrderItem(TakeoutOrderItem takeoutOrderItem) {
        Element item = DocumentHelper.createElement(QName.get("item",
                NAMESPACE));

        item.addAttribute("itemid", String.valueOf(takeoutOrderItem.getItemId()));
        item.addAttribute("foodname", takeoutOrderItem.getFoodName());
        item.addAttribute("price", String.valueOf(takeoutOrderItem.getPrice()));
        item.addAttribute("count", String.valueOf(takeoutOrderItem.getCount()));
        item.addAttribute("bussinessid", String.valueOf(takeoutOrderItem.getBussinessId()));
        item.addAttribute("orderid", String.valueOf(takeoutOrderItem.getOrderId()));

        probeResponse.add(item);
    }
}
