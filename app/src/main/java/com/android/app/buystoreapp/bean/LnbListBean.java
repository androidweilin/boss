package com.android.app.buystoreapp.bean;

import java.io.Serializable;

public  class LnbListBean implements Serializable{
    private String bindingMark1;
    private String bindingMark2;
    private String bindingMark3;
    private String bindingMark4;
    private String newsIcon;
    private String newsId;
    private String newsTitle;
    private int subscribeIsOff;
    private int userSubscribeNum;

    public String getBindingMark1() {
        return bindingMark1;
    }

    public void setBindingMark1(String bindingMark1) {
        this.bindingMark1 = bindingMark1;
    }

    public String getBindingMark2() {
        return bindingMark2;
    }

    public void setBindingMark2(String bindingMark2) {
        this.bindingMark2 = bindingMark2;
    }

    public String getBindingMark3() {
        return bindingMark3;
    }

    public void setBindingMark3(String bindingMark3) {
        this.bindingMark3 = bindingMark3;
    }

    public String getBindingMark4() {
        return bindingMark4;
    }

    public void setBindingMark4(String bindingMark4) {
        this.bindingMark4 = bindingMark4;
    }

    public String getNewsIcon() {
        return newsIcon;
    }

    public void setNewsIcon(String newsIcon) {
        this.newsIcon = newsIcon;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public int getSubscribeIsOff() {
        return subscribeIsOff;
    }

    public void setSubscribeIsOff(int subscribeIsOff) {
        this.subscribeIsOff = subscribeIsOff;
    }

    public int getUserSubscribeNum() {
        return userSubscribeNum;
    }

    public void setUserSubscribeNum(int userSubscribeNum) {
        this.userSubscribeNum = userSubscribeNum;
    }
}