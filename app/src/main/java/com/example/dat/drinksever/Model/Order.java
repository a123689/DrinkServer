package com.example.dat.drinksever.Model;

public class Order {
    private long OrderId;
    private int OderStatus;
    private float OderPrice;
    private String OderDetail,OderComment,OderAddress,UserPhone;

    public Order() {
    }

    public long getOrderId() {
        return OrderId;
    }

    public void setOrderId(long orderId) {
        OrderId = orderId;
    }

    public int getOrderStatus() {
        return OderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        OderStatus = orderStatus;
    }

    public float getOrderPrice() {
        return OderPrice;
    }

    public void setOrderPrice(float orderPrice) {
        OderPrice = orderPrice;
    }

    public String getOrderDetail() {
        return OderDetail;
    }

    public void setOrderDetail(String orderDetail) {
        OderDetail = orderDetail;
    }

    public String getOrderComment() {
        return OderComment;
    }

    public void setOrderComment(String orderComment) {
        OderComment = orderComment;
    }

    public String getOrderAddress() {
        return OderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        OderAddress = orderAddress;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }
}
