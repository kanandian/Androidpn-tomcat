/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.service;

import org.androidpn.server.model.ChatMessage;
import org.androidpn.server.model.FoodMenu;
import org.androidpn.server.model.Payment;
import org.androidpn.server.xmpp.XmppServer;

/** 
 * This is a helper class to look up service objects.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ServiceLocator {

    public static String USER_SERVICE = "userService";
    public static String BUSSINESS_SERVICE = "bussinessService";
    public static String MENU_FOOD_SERVICE = "foodMenuService";
    public static String TAKEOUT_ORDER_SERVICE = "takeoutOrderService";
    public static String PAYMENT_SERVICE = "paymentService";
    public static String CHAT_MESSAGE_SERVICE = "chatMessageService";

    /**
     * Generic method to obtain a service object for a given name. 
     * 
     * @param name the service bean name
     * @return
     */
    public static Object getService(String name) {
        return XmppServer.getInstance().getBean(name);
    }

    /**
     * Obtains the user service.
     * 
     * @return the user service
     */
    public static UserService getUserService() {
        return (UserService) XmppServer.getInstance().getBean(USER_SERVICE);
    }

    public static BussinessService getBussinessService() {
        return (BussinessService) XmppServer.getInstance().getBean(BUSSINESS_SERVICE);
    }

    public static FoodMenuService getFoodMenuService() {
        return (FoodMenuService) XmppServer.getInstance().getBean(MENU_FOOD_SERVICE);
    }

    public static TakeoutOrderService getTakeoutOrderService() {
        return (TakeoutOrderService) XmppServer.getInstance().getBean(TAKEOUT_ORDER_SERVICE);
    }

    public static PaymentService getPaymentService() {
        return (PaymentService) XmppServer.getInstance().getBean(PAYMENT_SERVICE);
    }

    public static ChatMessageService getChatMessageService() {
        return (ChatMessageService) XmppServer.getInstance().getBean(CHAT_MESSAGE_SERVICE);
    }


}
