package com.ronda.saleassist.bean;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/29
 * Version: v1.0
 */

public class BuyerOrderVipInfo {

    /**
     * money : 0
     * cost1 : {"commission":"1","min":"10","status":1}
     * cost2 : {"status":0, "score"}
     * cost3 : {"cost":"0.95","status":1}
     * status : 1
     * name : 四星会员
     */

    private String money;
    private String status;
    private String name;
    /**
     * commission : 1
     * min : 10
     * status : 1
     */

    private Cost1Bean cost1;
    /**
     * status : 0
     * score : 1
     */

    private Cost2Bean cost2;
    /**
     * cost : 0.95
     * status : 1
     */

    private Cost3Bean cost3;

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public Cost1Bean getCost1() {
        return cost1;
    }

    public void setCost1(Cost1Bean cost1) {
        this.cost1 = cost1;
    }

    public Cost2Bean getCost2() {
        return cost2;
    }

    public void setCost2(Cost2Bean cost2) {
        this.cost2 = cost2;
    }

    public Cost3Bean getCost3() {
        return cost3;
    }

    public void setCost3(Cost3Bean cost3) {
        this.cost3 = cost3;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Cost1Bean {
        private String commission;
        private String min;
        private int status;

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public static class Cost2Bean {
        private int status;
        private String score;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
    }

    public static class Cost3Bean {
        private String cost;
        private int status;

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
