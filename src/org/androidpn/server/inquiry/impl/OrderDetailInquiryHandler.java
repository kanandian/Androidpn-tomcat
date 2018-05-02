package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.FoodMenu;
import org.androidpn.server.model.TakeoutOrder;
import org.androidpn.server.model.TakeoutOrderItem;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.TakeoutOrderService;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

import java.util.List;
import java.util.SplittableRandom;

public class OrderDetailInquiryHandler implements InquiryHandler {


    private final String NAMESPACE = "androidpn:iq:orderdetail";
    private Element probeResponse;

    private TakeoutOrderService takeoutOrderService;

    public OrderDetailInquiryHandler() {
        takeoutOrderService = ServiceLocator.getTakeoutOrderService();
        probeResponse = DocumentHelper.createElement(QName.get("order",
                NAMESPACE));
    }


    @Override
    public IQ handle(IQ reply, String title) {
        TakeoutOrder takeoutOrder = takeoutOrderService.getTakeoutOrderById(title);

        for (TakeoutOrderItem takeoutOrderItem : takeoutOrder.getTakeoutOrderItemList()) {
            addItem(takeoutOrderItem);
        }

        probeResponse.addElement("bussinessname").setText(takeoutOrder.getBussinessName());
        probeResponse.addElement("address").setText(takeoutOrder.getAddress());
        probeResponse.addElement("mobile").setText(takeoutOrder.getMobile());
        probeResponse.addElement("note").setText(takeoutOrder.getNote());
        probeResponse.addElement("totalprice").setText(String.valueOf(takeoutOrder.getTotalPrice()));
        probeResponse.addElement("orderid").setText(String.valueOf(takeoutOrder.getOrderId()));
        probeResponse.addElement("bussinessid").setText(String.valueOf(takeoutOrder.getBussinessId()));


        reply.setChildElement(probeResponse);
        return reply;
    }

    public void addItem(TakeoutOrderItem takeoutOrderItem) {
        org.dom4j.Element item = DocumentHelper.createElement(QName.get("item",
                NAMESPACE));
        item.addAttribute("itemid", String.valueOf(takeoutOrderItem.getItemId()));
        item.addAttribute("foodname", takeoutOrderItem.getFoodName());
        item.addAttribute("count", String.valueOf(takeoutOrderItem.getCount()));
        item.addAttribute("bussinessId", String.valueOf(takeoutOrderItem.getBussinessId()));
        item.addAttribute("price", String.valueOf(takeoutOrderItem.getPrice()));
        item.addAttribute("orderid", String.valueOf(takeoutOrderItem.getOrderId()));
        probeResponse.add(item);
    }
}
