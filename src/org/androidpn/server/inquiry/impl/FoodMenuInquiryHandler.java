package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.FoodMenu;
import org.androidpn.server.service.FoodMenuService;
import org.androidpn.server.service.ServiceLocator;
import org.dom4j.DocumentHelper;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

import javax.xml.bind.Element;
import java.util.List;

public class FoodMenuInquiryHandler implements InquiryHandler {

    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private FoodMenuService foodMenuService;
    private org.dom4j.Element probeResponse;


    public FoodMenuInquiryHandler() {
        foodMenuService = ServiceLocator.getFoodMenuService();
        probeResponse = DocumentHelper.createElement(QName.get("foodmeniu",
                NAMESPACE));
    }

    @Override
    public IQ handle(IQ reply, String title) {
        List<FoodMenu> foodMenuList = foodMenuService.getFoodMenusByBussinessId(title);

        for (FoodMenu foodMenu : foodMenuList) {
            addItem(foodMenu);
        }
        reply.setChildElement(probeResponse);
        return reply;
    }

    public void addItem(FoodMenu foodMenu) {
        org.dom4j.Element item = DocumentHelper.createElement(QName.get("item",
                NAMESPACE));
        item.addAttribute("menuId", String.valueOf(foodMenu.getMenuId()));
        item.addAttribute("foodName", foodMenu.getFoodName());
        item.addAttribute("price", String.valueOf(foodMenu.getPrice()));
        item.addAttribute("bussinessId", String.valueOf(foodMenu.getBussinessId()));
        probeResponse.add(item);
    }
}
