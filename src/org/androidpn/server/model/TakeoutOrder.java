package org.androidpn.server.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "takeout_order")
public class TakeoutOrder {
    private long orderId;

    private List<TakeoutOrderItem> takeoutOrderItemList;

    private String address;
    private String note;
    private String mobile;

    private double totalPrice;

    private long bussinessId;

    private String fromUserName;
    private String toUserName;

    public TakeoutOrder() {

    }

    @Id
    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "orderId")
    @OrderBy
    public List<TakeoutOrderItem> getTakeoutOrderItemList() {
        return takeoutOrderItemList;
    }

    public void setTakeoutOrderItemList(List<TakeoutOrderItem> takeoutOrderItemList) {
        this.takeoutOrderItemList = takeoutOrderItemList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(long bussinessId) {
        this.bussinessId = bussinessId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }
}
