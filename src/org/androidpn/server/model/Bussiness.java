package org.androidpn.server.model;

import org.androidpn.server.util.Location;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.theme.SessionThemeResolver;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "apn_bussiness")
public class Bussiness implements Serializable {
    private Long bussinessId;
    private String imageURL;
    private String businessName;
    private String classification;
    private String tag;
    private Location location;
    private String mobile;
    private double price;
    private int level;
    private String des;

    private List<Comment> commentList = new ArrayList<Comment>();


    private String userName;

    public Bussiness() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(Long bussinessId) {
        this.bussinessId = bussinessId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLocation() {
        return location.toString();
    }

    public void setLocation(String location) {
        this.location = new Location(location);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @OneToMany(mappedBy = "bussiness", fetch = FetchType.EAGER)
    @OrderBy
    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    //    public String toXMLInummary(String NAMESPACE) {
//        //需要发送imageURL、bussinessame、location、tag
//        Element probeResponse = DocumentHelper.createElement(QName.get("query",
//                NAMESPACE));
//        probeResponse.addElement("");
//        StringBuilder itemContent = new StringBuilder();
//
//        itemContent.append("<bussinessitem>");
//        itemContent.append("");
//        itemContent.append("</bussinessitem>");
//
//        return itemContent.toString();
//    }
}
