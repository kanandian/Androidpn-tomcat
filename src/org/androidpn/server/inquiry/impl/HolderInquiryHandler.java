package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.User;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

import javax.xml.ws.Service;
import java.util.List;

public class HolderInquiryHandler implements InquiryHandler {

    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private BussinessService bussinessService;
    private Element probeResponse;

    public HolderInquiryHandler() {
        this.bussinessService = ServiceLocator.getBussinessService();
    }

    @Override
    public IQ handle(IQ reply, String title) {
        probeResponse = DocumentHelper.createElement(QName.get("activity",
                NAMESPACE));

        List<Bussiness> bussinessList = bussinessService.getBussinessesByUserName(title);

        if (bussinessList == null || bussinessList.isEmpty()) {

        } else {
            for (Bussiness bussiness : bussinessList) {
                addItem(bussiness);
            }
        }

        reply.setChildElement(probeResponse);

        return reply;
    }

    public void addItem(Bussiness bussiness) {
        Element item = DocumentHelper.createElement(QName.get("item",
                NAMESPACE));
        item.addAttribute("id", String.valueOf(bussiness.getBussinessId()));
        item.addAttribute("name", bussiness.getBusinessName());
        item.addAttribute("imageURL", bussiness.getImageURL());
        item.addAttribute("tag", bussiness.getTag());
        item.addAttribute("location", bussiness.getLocation());
        item.addAttribute("mobile", bussiness.getMobile());
        item.addAttribute("price", String.valueOf(bussiness.getPrice()));
        item.addAttribute("level", String.valueOf(bussiness.getLevel()));
        item.addAttribute("des", bussiness.getDes());
        probeResponse.add(item);
    }
}
