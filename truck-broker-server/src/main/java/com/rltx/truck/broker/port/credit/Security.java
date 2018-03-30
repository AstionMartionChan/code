package com.rltx.truck.broker.port.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for security complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="security">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LogisticsExchangeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserTokenID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "security", propOrder = { "logisticsExchangeCode",
		"userTokenID" })
public class Security {

	@XmlElement(name = "LogisticsExchangeCode")
	protected String logisticsExchangeCode;
	@XmlElement(name = "UserTokenID")
	protected String userTokenID;

	/**
	 * Gets the value of the logisticsExchangeCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLogisticsExchangeCode() {
		return logisticsExchangeCode;
	}

	/**
	 * Sets the value of the logisticsExchangeCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLogisticsExchangeCode(String value) {
		this.logisticsExchangeCode = value;
	}

	/**
	 * Gets the value of the userTokenID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUserTokenID() {
		return userTokenID;
	}

	/**
	 * Sets the value of the userTokenID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUserTokenID(String value) {
		this.userTokenID = value;
	}

}
