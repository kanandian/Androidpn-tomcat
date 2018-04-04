package org.androidpn.server.model;

import javax.persistence.*;

@Entity
@Table(name = "apn_comment")
public class Comment {
    private long commentId;
    private String userName;
    private String content;

    private Bussiness bussiness;

    public Comment() {
    }

    @Id
    @GeneratedValue
    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToOne
    public Bussiness getBussiness() {
        return bussiness;
    }

    public void setBussiness(Bussiness bussiness) {
        this.bussiness = bussiness;
    }
}
