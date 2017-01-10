package com.android.app.buystoreapp.managementservice;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 */
public class CollectionBean implements Serializable{

    private static final long serialVersionUID = 7935608392415138795L;
    /**
     * result : 0
     * resultNote : 成功
     * ucpList : [{"bindingMark1":10,"bindingMark2":1,"bindingMark3":1,"bindingMark4":1,"createDate":"2016-10-09","mgList":[{"moreGroId":"99","moreGroName":"黑色的男士上衣","moreGroPrice":134,"moreGroSurplus":3,"moreGroUnit":"元","proId":"S3is87jdf69Mid82"}],"nickname":"boss_李四","piList":[{"isCover":0,"proId":"S3is87jdf69Mid82","proImageId":"2147","proImageName":"2147.png","proImageUrl":"/bossgroupimage/shop/477/product/431/2147.png"}],"proDes":"最新款的男士上衣，便宜处理了，给钱就卖，给钱就卖","proDistance":"0.0","proId":"S3is87jdf69Mid82","proName":"男款上衣","proSale":0,"proSeeNum":0,"proSurplus":0,"serveLabel":0,"serveLabelName":"","userHeadicon":"","userLevelMark":2,"userPosition":"老师","userTreasure":"4332"}]
     */

    private String result;
    private String resultNote;
    /**
     * bindingMark1 : 10
     * bindingMark2 : 1
     * bindingMark3 : 1
     * bindingMark4 : 1
     * createDate : 2016-10-09
     * mgList : [{"moreGroId":"99","moreGroName":"黑色的男士上衣","moreGroPrice":134,"moreGroSurplus":3,"moreGroUnit":"元","proId":"S3is87jdf69Mid82"}]
     * nickname : boss_李四
     * piList : [{"isCover":0,"proId":"S3is87jdf69Mid82","proImageId":"2147","proImageName":"2147.png","proImageUrl":"/bossgroupimage/shop/477/product/431/2147.png"}]
     * proDes : 最新款的男士上衣，便宜处理了，给钱就卖，给钱就卖
     * proDistance : 0.0
     * proId : S3is87jdf69Mid82
     * proName : 男款上衣
     * proSale : 0
     * proSeeNum : 0
     * proSurplus : 0
     * serveLabel : 0
     * serveLabelName :
     * userHeadicon :
     * userLevelMark : 2
     * userPosition : 老师
     * userTreasure : 4332
     */

    private List<UcpListBean> ucpList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultNote() {
        return resultNote;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

    public List<UcpListBean> getUcpList() {
        return ucpList;
    }

    public void setUcpList(List<UcpListBean> ucpList) {
        this.ucpList = ucpList;
    }

    public static class UcpListBean implements Serializable{
        private static final long serialVersionUID = 798535265658050815L;
        private int bindingMark1;
        private int bindingMark2;
        private int bindingMark3;
        private int bindingMark4;
        private String createDate;
        private String nickname;
        private String proDes;
        private String proDistance;
        private String proId;
        private String proName;
        private int proSale;
        private int proSeeNum;
        private int proSurplus;
        private String serveLabel;
        private String serveLabelName;
        private String userHeadicon;
        private int userLevelMark;
        private String userPosition;
        private String userTreasure;
        private String createDateFamt;
        private String userId;
        /**
         * moreGroId : 99
         * moreGroName : 黑色的男士上衣
         * moreGroPrice : 134
         * moreGroSurplus : 3
         * moreGroUnit : 元
         * proId : S3is87jdf69Mid82
         */

        private List<MgListBean> mgList;
        /**
         * isCover : 0
         * proId : S3is87jdf69Mid82
         * proImageId : 2147
         * proImageName : 2147.png
         * proImageUrl : /bossgroupimage/shop/477/product/431/2147.png
         */

        private List<PiListBean> piList;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCreateDateFamt() {
            return createDateFamt;
        }

        public void setCreateDateFamt(String createDateFamt) {
            this.createDateFamt = createDateFamt;
        }

        public int getBindingMark1() {
            return bindingMark1;
        }

        public void setBindingMark1(int bindingMark1) {
            this.bindingMark1 = bindingMark1;
        }

        public int getBindingMark2() {
            return bindingMark2;
        }

        public void setBindingMark2(int bindingMark2) {
            this.bindingMark2 = bindingMark2;
        }

        public int getBindingMark3() {
            return bindingMark3;
        }

        public void setBindingMark3(int bindingMark3) {
            this.bindingMark3 = bindingMark3;
        }

        public int getBindingMark4() {
            return bindingMark4;
        }

        public void setBindingMark4(int bindingMark4) {
            this.bindingMark4 = bindingMark4;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getProDes() {
            return proDes;
        }

        public void setProDes(String proDes) {
            this.proDes = proDes;
        }

        public String getProDistance() {
            return proDistance;
        }

        public void setProDistance(String proDistance) {
            this.proDistance = proDistance;
        }

        public String getProId() {
            return proId;
        }

        public void setProId(String proId) {
            this.proId = proId;
        }

        public String getProName() {
            return proName;
        }

        public void setProName(String proName) {
            this.proName = proName;
        }

        public int getProSale() {
            return proSale;
        }

        public void setProSale(int proSale) {
            this.proSale = proSale;
        }

        public int getProSeeNum() {
            return proSeeNum;
        }

        public void setProSeeNum(int proSeeNum) {
            this.proSeeNum = proSeeNum;
        }

        public int getProSurplus() {
            return proSurplus;
        }

        public void setProSurplus(int proSurplus) {
            this.proSurplus = proSurplus;
        }

        public String getServeLabel() {
            return serveLabel;
        }

        public void setServeLabel(String serveLabel) {
            this.serveLabel = serveLabel;
        }

        public String getServeLabelName() {
            return serveLabelName;
        }

        public void setServeLabelName(String serveLabelName) {
            this.serveLabelName = serveLabelName;
        }

        public String getUserHeadicon() {
            return userHeadicon;
        }

        public void setUserHeadicon(String userHeadicon) {
            this.userHeadicon = userHeadicon;
        }

        public int getUserLevelMark() {
            return userLevelMark;
        }

        public void setUserLevelMark(int userLevelMark) {
            this.userLevelMark = userLevelMark;
        }

        public String getUserPosition() {
            return userPosition;
        }

        public void setUserPosition(String userPosition) {
            this.userPosition = userPosition;
        }

        public String getUserTreasure() {
            return userTreasure;
        }

        public void setUserTreasure(String userTreasure) {
            this.userTreasure = userTreasure;
        }

        public List<MgListBean> getMgList() {
            return mgList;
        }

        public void setMgList(List<MgListBean> mgList) {
            this.mgList = mgList;
        }

        public List<PiListBean> getPiList() {
            return piList;
        }

        public void setPiList(List<PiListBean> piList) {
            this.piList = piList;
        }

        public static class MgListBean implements Serializable{
            private static final long serialVersionUID = -6968348312044703777L;
            private String moreGroId;
            private String moreGroName;
            private double moreGroPrice;
            private int moreGroSurplus;
            private String moreGroUnit;
            private String proId;

            public String getMoreGroId() {
                return moreGroId;
            }

            public void setMoreGroId(String moreGroId) {
                this.moreGroId = moreGroId;
            }

            public String getMoreGroName() {
                return moreGroName;
            }

            public void setMoreGroName(String moreGroName) {
                this.moreGroName = moreGroName;
            }

            public double getMoreGroPrice() {
                return moreGroPrice;
            }

            public void setMoreGroPrice(double moreGroPrice) {
                this.moreGroPrice = moreGroPrice;
            }

            public int getMoreGroSurplus() {
                return moreGroSurplus;
            }

            public void setMoreGroSurplus(int moreGroSurplus) {
                this.moreGroSurplus = moreGroSurplus;
            }

            public String getMoreGroUnit() {
                return moreGroUnit;
            }

            public void setMoreGroUnit(String moreGroUnit) {
                this.moreGroUnit = moreGroUnit;
            }

            public String getProId() {
                return proId;
            }

            public void setProId(String proId) {
                this.proId = proId;
            }
        }

        public static class PiListBean implements Serializable{
            private static final long serialVersionUID = -3597731899162444788L;
            private int isCover;
            private String proId;
            private String proImageId;
            private String proImageName;
            private String proImageUrl;
            private String proImageMin;

            public String getProImageMin() {
                return proImageMin;
            }

            public void setProImageMin(String proImageMin) {
                this.proImageMin = proImageMin;
            }

            public int getIsCover() {
                return isCover;
            }

            public void setIsCover(int isCover) {
                this.isCover = isCover;
            }

            public String getProId() {
                return proId;
            }

            public void setProId(String proId) {
                this.proId = proId;
            }

            public String getProImageId() {
                return proImageId;
            }

            public void setProImageId(String proImageId) {
                this.proImageId = proImageId;
            }

            public String getProImageName() {
                return proImageName;
            }

            public void setProImageName(String proImageName) {
                this.proImageName = proImageName;
            }

            public String getProImageUrl() {
                return proImageUrl;
            }

            public void setProImageUrl(String proImageUrl) {
                this.proImageUrl = proImageUrl;
            }
        }
    }
}
