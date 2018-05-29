package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

import java.util.List;

public class CollectedBussinessesInquiryHandler implements InquiryHandler {
    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private UserService userService;
    private Element probeResponse;

    public CollectedBussinessesInquiryHandler() {
        userService = ServiceLocator.getUserService();
        probeResponse = DocumentHelper.createElement(QName.get("activity",
                NAMESPACE));
    }

    @Override
    public IQ handle(IQ reply, String title) {

        List<Bussiness> bussinessList = userService.getCollectedBussinesses(title);

        for (Bussiness bussiness : bussinessList) {
            addItem(bussiness);
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
        item.addAttribute("classification", bussiness.getClassification());
        item.addAttribute("location", bussiness.getLocation());
        item.addAttribute("mobile", bussiness.getMobile());
        item.addAttribute("price", String.valueOf(bussiness.getAvgPrice()));
        item.addAttribute("level", String.valueOf(bussiness.getAvgLevel()));
        item.addAttribute("des", bussiness.getDes());
        item.addAttribute("holder", bussiness.getHolder());
        item.addAttribute("starttime", (bussiness.getStartTime() != null) ? bussiness.getStartTime() : "");
        item.addAttribute("endtime", (bussiness.getEndTime() != null) ? bussiness.getEndTime() : "");
        item.addAttribute("feature", (bussiness.getFeature() != null) ? bussiness.getFeature() : "");
        probeResponse.add(item);
    }
}
