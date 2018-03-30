package com.rltx.truck.broker.port.credit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java class for publicInformation complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="publicInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ActionType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Providerlist" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="LogisticsExchangeCode" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "publicInformation", propOrder = { "serviceType", "actionType",
		"providerlist", "remark" })
public class PublicInformation {

	@XmlElement(name = "ServiceType")
	protected String serviceType;
	@XmlElement(name = "ActionType")
	protected String actionType;
	@XmlElement(name = "Providerlist")
	protected PublicInformation.Providerlist providerlist;
	@XmlElement(name = "Remark")
	protected String remark;

	/**
	 * Gets the value of the serviceType property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * Sets the value of the serviceType property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setServiceType(String value) {
		this.serviceType = value;
	}

	/**
	 * Gets the value of the actionType property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getActionType() {
		return actionType;
	}

	/**
	 * Sets the value of the actionType property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setActionType(String value) {
		this.actionType = value;
	}

	/**
	 * Gets the value of the providerlist property.
	 *
	 * @return possible object is {@link PublicInformation.Providerlist }
	 *
	 */
	public PublicInformation.Providerlist getProviderlist() {
		return providerlist;
	}

	/**
	 * Sets the value of the providerlist property.
	 *
	 * @param value
	 *            allowed object is {@link PublicInformation.Providerlist }
	 *
	 */
	public void setProviderlist(PublicInformation.Providerlist value) {
		this.providerlist = value;
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
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="LogisticsExchangeCode" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "logisticsExchangeCode" })
	public static class Providerlist {

		@XmlElement(name = "LogisticsExchangeCode")
		protected List<String> logisticsExchangeCode;

		/**
		 * Gets the value of the logisticsExchangeCode property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the logisticsExchangeCode property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getLogisticsExchangeCode().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link String }
		 * 
		 * 
		 */
		public List<String> getLogisticsExchangeCode() {
			if (logisticsExchangeCode == null) {
				logisticsExchangeCode = new ArrayList<String>();
			}
			return this.logisticsExchangeCode;
		}

	}

}
