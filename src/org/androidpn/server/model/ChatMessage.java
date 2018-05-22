package org.androidpn.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "apn_message")
public class ChatMessage {
    private long messageId;
    private String fromUserName;
    private String fromUserJID;
    private String toUserName;
    private String content;
    private long createTime;

    public ChatMessage() {
    }

    @Id
    @GeneratedValue
    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserJID() {
        return fromUserJID;
    }

    public void setFromUserJID(String fromUserJID) {
        this.fromUserJID = fromUserJID;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
