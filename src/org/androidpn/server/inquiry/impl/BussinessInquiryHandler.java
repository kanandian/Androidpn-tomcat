package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

public class BussinessInquiryHandler implements InquiryHandler {

    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private BussinessService bussinessService;
    private Element probeResponse;

    public BussinessInquiryHandler() {
        bussinessService = ServiceLocator.getBussinessService();
    }


    @Override
    public IQ handle(IQ reply, String title) {
        probeResponse = DocumentHelper.createElement(QName.get("bussiness",
                NAMESPACE));
        Bussiness bussiness = bussinessService.getBussiness(title);

        createResponse(bussiness);

        reply.setChildElement(probeResponse);

        return reply;
    }

    public void createResponse(Bussiness bussiness) {
        //创建元素
        Element id = DocumentHelper.createElement("id");
        Element name = DocumentHelper.createElement("name");
        Element imageURL = DocumentHelper.createElement("imageURL");
        Element location = DocumentHelper.createElement("location");
        Element classification = DocumentHelper.createElement("classification");
        Element tag = DocumentHelper.createElement("tag");
        Element mobile = DocumentHelper.createElement("mobile");
        Element des = DocumentHelper.createElement("des");

        //设置内容
        id.setText(String.valueOf(bussiness.getBussinessId()));
        name.setText(bussiness.getBusinessName());
        imageURL.setText(bussiness.getImageURL());
        location.setText(bussiness.getLocation());
        classification.setText(bussiness.getClassification());
        tag.setText(bussiness.getTag());
        mobile.setText(bussiness.getMobile());
        des.setText(bussiness.getDes());

        //添加到probeResponse中
        probeResponse.add(id);
        probeResponse.add(name);
        probeResponse.add(imageURL);
        probeResponse.add(location);
        probeResponse.add(classification);
        probeResponse.add(tag);
        probeResponse.add(mobile);
        probeResponse.add(des);
    }

}
