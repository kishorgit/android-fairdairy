package com.example.user.sunaitestingapp.model;

public class admin_listing_model {
    String cust_name,addrs, deliveryinstruction, specialinstruction, leave_status,qty,customerid,routeName;
    int productid ,createdBy;
    Double actualDelivery, scheduleDelivery ;

    public admin_listing_model(){}

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getQty() {
        return qty;
    }

    public void setAddrs(String addrs) {
        this.addrs = addrs;
    }

    public String getAddrs() {
        return addrs;
    }


    public String getLeave_status() {
        return leave_status;
    }

    public void setLeave_status(String leave_status) {
        this.leave_status = leave_status;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setProductid(int productid) {
        this.productid = productid;
    }

    public int getProductid() {
        return productid;
    }

    public void setActualDelivery(Double actualDelivery) {
        this.actualDelivery = actualDelivery;
    }

    public Double getActualDelivery() {
        return actualDelivery;
    }

    public void setScheduleDelivery(Double scheduleDelivery) {
        this.scheduleDelivery = scheduleDelivery;
    }

    public Double getScheduleDelivery() {
        return scheduleDelivery;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public void setDeliveryinstruction(String deliveryinstruction) {
        this.deliveryinstruction = deliveryinstruction;
    }

    public String getDeliveryinstruction() {
        return deliveryinstruction;
    }

    public void setSpecialinstruction(String specialinstruction) {
        this.specialinstruction = specialinstruction;
    }

    public String getSpecialinstruction() {
        return specialinstruction;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteName() {
        return routeName;
    }
}
