package com.example.user.sunaitestingapp.model;

public class StringWithTag {

    String val;

    public Object tag;

    public StringWithTag(String val, Object tag) {
        this.val = val;
        this.tag = tag;
    }


    @Override
    public String toString() {
        return val;
    }
}
