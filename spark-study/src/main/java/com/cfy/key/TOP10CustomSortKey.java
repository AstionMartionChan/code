package com.cfy.key;

import scala.math.Ordered;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/4
 * Time: 15:09
 * Work contact: Astion_Leo@163.com
 */


public class TOP10CustomSortKey implements Ordered<TOP10CustomSortKey>, Serializable {


    private long clickCount;
    private long orderCount;
    private long payCount;


    @Override
    public int compare(TOP10CustomSortKey that) {
        if (this.clickCount - that.getClickCount() != 0){
            return (int) (this.clickCount - that.getClickCount());
        } else if (this.orderCount - that.getOrderCount() != 0){
            return (int) (this.orderCount - that.getOrderCount());
        } else if (this.payCount - that.getPayCount() != 0){
            return (int) (this.payCount - that.getPayCount());
        }
        return 0;
    }

    @Override
    public boolean $less(TOP10CustomSortKey that) {
        if (this.clickCount < that.getClickCount()){
            return true;
        } else if (this.clickCount == that.getClickCount() &&
                this.orderCount < that.getOrderCount()){
            return true;
        } else if (this.clickCount == that.getClickCount() &&
                this.orderCount == that.getOrderCount() &&
                this.payCount < that.getPayCount()){
            return true;
        }

        return false;
    }

    @Override
    public boolean $greater(TOP10CustomSortKey that) {
        if (this.clickCount > that.getClickCount()){
            return true;
        } else if (this.clickCount == that.getClickCount() &&
                this.orderCount > that.getOrderCount()){
            return true;
        } else if (this.clickCount == that.getClickCount() &&
                this.orderCount == that.getOrderCount() &&
                this.payCount > that.getPayCount()){
            return true;
        }

        return false;
    }

    @Override
    public boolean $less$eq(TOP10CustomSortKey that) {
        if ($less(that)){
            return true;
        } else if (this.clickCount == that.getClickCount() &&
                this.orderCount == that.getOrderCount() &&
                this.payCount == that.getPayCount()){
            return true;
        }
        return false;
    }

    @Override
    public boolean $greater$eq(TOP10CustomSortKey that) {
        if ($greater(that)){
            return true;
        } else if (this.clickCount == that.getClickCount() &&
                this.orderCount == that.getOrderCount() &&
                this.payCount == that.getPayCount()){
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(TOP10CustomSortKey that) {
        if (this.clickCount - that.getClickCount() != 0){
            return (int) (this.clickCount - that.getClickCount());
        } else if (this.orderCount - that.getOrderCount() != 0){
            return (int) (this.orderCount - that.getOrderCount());
        } else if (this.payCount - that.getPayCount() != 0){
            return (int) (this.payCount - that.getPayCount());
        }
        return 0;
    }


    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getPayCount() {
        return payCount;
    }

    public void setPayCount(Long payCount) {
        this.payCount = payCount;
    }
}
