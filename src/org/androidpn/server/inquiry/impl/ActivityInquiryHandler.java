package org.androidpn.server.inquiry.impl;

import org.androidpn.server.inquiry.InquiryHandler;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.Location;
import org.androidpn.server.util.MahoutUtil;
import org.androidpn.server.util.SortUtil;
import org.apache.mahout.cf.taste.common.TasteException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

import java.io.IOException;
import java.util.*;

public class ActivityInquiryHandler implements InquiryHandler {

    private static final String NAMESPACE = "androidpn:iq:inquiry";

    private UserService userService;
    private BussinessService bussinessService;
    private Element probeResponse;

    private Location location;

    public ActivityInquiryHandler(String location) {
        userService = ServiceLocator.getUserService();
        bussinessService = ServiceLocator.getBussinessService();
        if (location == null || "".equals(location)) {
            this.location = null;
        } else  {
            this.location = new Location(location);
        }
    }

    @Override
    public IQ handle(IQ reply, String title) {
        probeResponse = DocumentHelper.createElement(QName.get("activity",
                NAMESPACE));
        if("main".equals(title)) {
            List<Bussiness> bussinessList = null;
            if (this.location == null) {
                bussinessList = bussinessService.getBussinesses();
            } else {
                bussinessList = bussinessService.getBussinessOrderByDistance(location);
            }
            for(Bussiness bussiness : bussinessList) {
                addItem(bussiness);
            }
        } else if ("shoplist".equals(title)) {
            List<Bussiness> bussinessList = null;
            if (this.location == null) {
                bussinessList = bussinessService.getBussinesses();
            } else {
                bussinessList = bussinessService.getBussinessOrderByDistance(location);
            }
            for(Bussiness bussiness : bussinessList) {
                addItem(bussiness);
            }
        } else if (title.contains("perference")) {
            String userName = title.split(":")[1];

            if (userName == null || "null".equals(userName) || "".equals(userName)) {
                List<Bussiness> bussinessList = bussinessService.getBussinesses();
                for (Bussiness bussiness : bussinessList) {
                    addItem(bussiness);
                }
            } else {
                List<Bussiness> bussinessList = MahoutUtil.getInstance().getPerferencesBussinesses(userName, 30);
                if (bussinessList == null || bussinessList.isEmpty()) {
                    bussinessList = bussinessService.getBussinesses();

                }

                List<Bussiness> removeList = new ArrayList<Bussiness>();
                for (Bussiness bussiness : bussinessList) {
                    if (location.getDistance(bussiness) > 15000) {
                        removeList.add(bussiness);
                    }
                }
                bussinessList.removeAll(removeList);


                Set<Bussiness> bussinesses = listToSet(bussinessList);
                for (Bussiness bussiness : bussinessService.getBussinessOrderByDistance(location)) {
                    bussinesses.add(bussiness);
                    if (bussinesses.size() >= 30) {
                        break;
                    }
                }

                bussinessList = setToList(bussinesses);

                SortUtil.sortBussinessesByDistance(bussinessList, location);
                    for (Bussiness bussiness : bussinessList) {
                        addItem(bussiness);
                    }
//                List<String> tagList = userService.getPerferences(userName);
//
//                if (tagList != null && !tagList.isEmpty()) {
//                    List<Bussiness> bussinessList = bussinessService.getBussinessesByTag(tagList);
//
//                    if (bussinessList != null && !bussinessList.isEmpty()) {
//                        for (Bussiness bussiness : bussinessList) {
//                            addItem(bussiness);
//                        }
//                    } else {
//                        bussinessList = bussinessService.getBussinesses();
//                        for (Bussiness bussiness : bussinessList) {
//                            addItem(bussiness);
//                        }
//                    }
//                } else {
//                    List<Bussiness> bussinessList = new ArrayList<Bussiness>();
//                    bussinessList = bussinessService.getBussinesses();
//                    for (Bussiness bussiness : bussinessList) {
//                        addItem(bussiness);
//                    }
//                }
            }
        } else {
            List<Bussiness> bussinessList = bussinessService.getBussinessesByClassification(title);
            SortUtil.sortBussinessesByDistance(bussinessList, location);
            for (Bussiness bussiness : bussinessList) {
                addItem(bussiness);
            }
        }

        reply.setChildElement(probeResponse);

        return reply;
    }

    public Set<Bussiness> listToSet(List<Bussiness> bussinessList) {
        Set<Bussiness> bussinesses = new HashSet<Bussiness>();

        for (Bussiness bussiness : bussinessList) {
            bussinesses.add(bussiness);
        }

        return bussinesses;

    }

    public List<Bussiness> setToList(Set<Bussiness> bussinesses) {
        List<Bussiness> bussinessList = new ArrayList<Bussiness>();

        for (Bussiness bussiness : bussinesses) {
            bussinessList.add(bussiness);
        }

        return bussinessList;
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
