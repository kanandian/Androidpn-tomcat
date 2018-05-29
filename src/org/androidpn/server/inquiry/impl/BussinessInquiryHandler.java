package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

public class BussinessInquiryHandler implements InquiryHandler {

    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private BussinessService bussinessService;
    private UserService userService;
    private Element probeResponse;

    private String userName;

    public BussinessInquiryHandler(String userName) {
        userService = ServiceLocator.getUserService();
        bussinessService = ServiceLocator.getBussinessService();
        this.userName = userName;
    }


    @Override
    public IQ handle(IQ reply, String title) {
        probeResponse = DocumentHelper.createElement(QName.get("bussiness",
                NAMESPACE));

        createResponse(title);

        reply.setChildElement(probeResponse);

        return reply;
    }

    public void createResponse(String bussinessId) {
        Bussiness bussiness = bussinessService.getBussiness(bussinessId);

        boolean exist = false;
        if (userName != null && !"".equals(userName)) {
            exist = userService.existColldection(userName, bussinessId);
        }
        //创建元素
        Element id = DocumentHelper.createElement("id");
        Element name = DocumentHelper.createElement("name");
        Element imageURL = DocumentHelper.createElement("imageURL");
        Element location = DocumentHelper.createElement("location");
        Element classification = DocumentHelper.createElement("classification");
        Element tag = DocumentHelper.createElement("tag");
        Element mobile = DocumentHelper.createElement("mobile");
        Element des = DocumentHelper.createElement("des");
        Element holder = DocumentHelper.createElement("holder");
        Element collected = DocumentHelper.createElement("collected");
        Element startTime = DocumentHelper.createElement("starttime");
        Element endtime = DocumentHelper.createElement("endtime");
        Element feature = DocumentHelper.createElement("feature");

        //设置内容
        id.setText(String.valueOf(bussiness.getBussinessId()));
        name.setText(bussiness.getBusinessName());
        imageURL.setText(bussiness.getImageURL());
        location.setText(bussiness.getLocation());
        classification.setText(bussiness.getClassification());
        tag.setText(bussiness.getTag());
        mobile.setText(bussiness.getMobile());
        des.setText(bussiness.getDes());
        holder.setText(bussiness.getHolder());
        startTime.setText(bussiness.getStartTime() != null ? bussiness.getStartTime() : "");
        endtime.setText(bussiness.getEndTime() != null ? bussiness.getEndTime() : "");
        feature.setText(bussiness.getFeature() != null ? bussiness.getFeature() : "");

        if (exist) {
            collected.setText("1");
        } else {
            collected.setText("0");
        }

        //添加到probeResponse中
        probeResponse.add(id);
        probeResponse.add(name);
        probeResponse.add(imageURL);
        probeResponse.add(location);
        probeResponse.add(classification);
        probeResponse.add(tag);
        probeResponse.add(mobile);
        probeResponse.add(des);
        probeResponse.add(holder);
        probeResponse.add(collected);
        probeResponse.add(startTime);
        probeResponse.add(endtime);
        probeResponse.add(feature);
    }

}
