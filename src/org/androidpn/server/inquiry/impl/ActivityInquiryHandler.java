package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

import java.util.List;

public class ActivityInquiryHandler implements InquiryHandler {

    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private BussinessService bussinessService;
    private Element probeResponse;

    public ActivityInquiryHandler() {
        bussinessService = ServiceLocator.getBussinessService();
    }

    @Override
    public IQ handle(IQ reply, String title) {
        probeResponse = DocumentHelper.createElement(QName.get("activity",
                NAMESPACE));

        if("main".equals(title)) {
            List<Bussiness> bussinessList = bussinessService.getBussinesses();
            for(Bussiness bussiness : bussinessList) {
                addItem(bussiness);
            }
        } else if ("shoplist".equals(title)) {
            List<Bussiness> bussinessList = bussinessService.getBussinesses();
            for(Bussiness bussiness : bussinessList) {
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
        item.addAttribute("des", bussiness.getDes());
        probeResponse.add(item);
    }

}
