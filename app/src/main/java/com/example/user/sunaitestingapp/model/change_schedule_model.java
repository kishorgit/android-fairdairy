package com.example.user.sunaitestingapp.model;

public class change_schedule_model {
    String product_name,imageurl;
    int product_id ;
    Double pricePerUnit, productQty ;
    Boolean subscriptable;

    public change_schedule_model(){}

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setProductQty(Double productQty) {
        this.productQty = productQty;
    }

    public Double getProductQty() {
        return productQty;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setSubscriptable(Boolean subscriptable) {
        this.subscriptable = subscriptable;
    }

    public Boolean getSubscriptable() {
        return subscriptable;
    }
}
