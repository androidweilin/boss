package com.android.app.buystoreapp.bean;

import java.io.Serializable;

/**
 * $desc
 * Created by likaihang on 16/09/21.
 */
public class RelaseGroupBean implements Serializable{

    private static final long serialVersionUID = 366944324825746624L;
    /**
     * "moreGroPrice":43.0,       //价格                        （double类型）
     "moreGroName": "红色上衣",    //组合名称
     "proSurplus":223             //剩余量                     （int类型）
     * */
    public String moreGroName;
    public double moreGroPrice;
    public int moreGroSurplus;
    public String unit; //单位

    public int getMoreGroSurplus() {
        return moreGroSurplus;
    }

    public void setMoreGroSurplus(int moreGroSurplus) {
        this.moreGroSurplus = moreGroSurplus;
    }

    public String getMoreGroName() {
        return moreGroName;
    }

    public void setMoreGroName(String moreGroName) {
        this.moreGroName = moreGroName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getMoreGroPrice() {
        return moreGroPrice;
    }

    public void setMoreGroPrice(double moreGroPrice) {
        this.moreGroPrice = moreGroPrice;
    }


    @Override
    public String toString() {
        return "RelaseGroupBean{" +
                "moreGroName='" + moreGroName + '\'' +
                ", moreGroPrice=" + moreGroPrice +
                ", moreGroSurplus=" + moreGroSurplus +
                ", unit='" + unit + '\'' +
                '}';
    }
}
