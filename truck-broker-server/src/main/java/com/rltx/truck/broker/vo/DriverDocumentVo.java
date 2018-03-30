package com.rltx.truck.broker.vo;

/**
 * Created by Leo_Chan on 2017/3/20.
 */
public class DriverDocumentVo {

    /*
    name: 姓名
    desc: 选填。驾驶员姓名
     */
    private String nameOfPerson;

    /*
    name: 从业资格证号
    desc: 选填。驾驶员从业资格证号
     */
    private String qualificationCertificateNumber;

    /*
    name: 电话号码
    desc: 选填。
     */
    private String telephoneNumber;


    public String getNameOfPerson() {
        return nameOfPerson;
    }

    public void setNameOfPerson(String nameOfPerson) {
        this.nameOfPerson = nameOfPerson;
    }

    public String getQualificationCertificateNumber() {
        return qualificationCertificateNumber;
    }

    public void setQualificationCertificateNumber(String qualificationCertificateNumber) {
        this.qualificationCertificateNumber = qualificationCertificateNumber;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}
