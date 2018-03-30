package com.rltx.truck.broker.port.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for genericResult complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="genericResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResultCode" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ExceptionInformationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExceptionInformation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BusinessInformation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "genericResult", propOrder = { "resultCode",
		"exceptionInformationCode", "exceptionInformation", "remark",
		"businessInformation" })
public class GenericResult {

	@XmlElement(name = "ResultCode")
	protected boolean resultCode;
	@XmlElement(name = "ExceptionInformationCode")
	protected String exceptionInformationCode;
	@XmlElement(name = "ExceptionInformation")
	protected String exceptionInformation;
	@XmlElement(name = "Remark")
	protected String remark;
	@XmlElement(name = "BusinessInformation")
	protected String businessInformation;

	/**
	 * Gets the value of the resultCode property.
	 * 
	 */
	public boolean isResultCode() {
		return resultCode;
	}

	/**
	 * Sets the value of the resultCode property.
	 * 
	 */
	public void setResultCode(boolean value) {
		this.resultCode = value;
	}

	/**
	 * Gets the value of the exceptionInformationCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExceptionInformationCode() {
		return exceptionInformationCode;
	}

	/**
	 * Sets the value of the exceptionInformationCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExceptionInformationCode(String value) {
		this.exceptionInformationCode = value;
	}

	/**
	 * Gets the value of the exceptionInformation property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExceptionInformation() {
		return exceptionInformation;
	}

	/**
	 * Sets the value of the exceptionInformation property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExceptionInformation(String value) {
		this.exceptionInformation = value;
	}

	/**
	 * Gets the value of the remark property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * Sets the value of the remark property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRemark(String value) {
		this.remark = value;
	}

	/**
	 * Gets the value of the businessInformation property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBusinessInformation() {
		return businessInformation;
	}

	/**
	 * Sets the value of the businessInformation property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBusinessInformation(String value) {
		this.businessInformation = value;
	}

}
