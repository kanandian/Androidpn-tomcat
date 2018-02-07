package org.androidpn.server.inquiry;

import org.xmpp.packet.IQ;

public interface InquiryHandler {

    public IQ handle(IQ reply, String title);

}
