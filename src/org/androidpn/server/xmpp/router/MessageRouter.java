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
package org.androidpn.server.xmpp.router;

import org.androidpn.server.xmpp.message.MessageHandler;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

/** 
 * This class is to route Message packets to their corresponding handler.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class MessageRouter {

    private SessionManager sessionManager;
    private MessageHandler messageHandler;

    /**
     * Constucts a packet router.
     */
    public MessageRouter() {
        sessionManager = SessionManager.getInstance();
        messageHandler = new MessageHandler();
    }

    /**
     * Routes the Message packet.
     * 
     * @param packet the packet to route
     */
    public void route(Message packet) {
        if (packet == null) {
            throw new NullPointerException();
        }

        JID sender = packet.getFrom();
        ClientSession session = sessionManager.getSession(sender);


        if (session == null
                || session.getStatus() == Session.STATUS_AUTHENTICATED) {
            handle(packet);
        }
    }

    public void handle(Packet packet) {
        MessageHandler handler = getMessageHandler();

        handler.process(packet);
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
