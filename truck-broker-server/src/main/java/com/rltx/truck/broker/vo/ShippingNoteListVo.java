package com.rltx.truck.broker.vo;

public class ShippingNoteListVo {

    /*
    name: 托运单号
     */
    private String shippingNoteNumber;

    /*
    name: 运单备注
     */
    private String remark;

    public String getShippingNoteNumber() {
        return shippingNoteNumber;
    }

    public void setShippingNoteNumber(String shippingNoteNumber) {
        this.shippingNoteNumber = shippingNoteNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
