package com.rltx.truck.broker.port.credit;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the com.behosoft.edi.logink.credit.wsClient
 * package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _Security_QNAME = new QName(
			"http://www.logink.org/", "Security");
	private final static QName _GenericResult_QNAME = new QName(
			"http://www.logink.org/", "GenericResult");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package:
	 * com.behosoft.edi.logink.credit.wsClient
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link GenericResult }
	 * 
	 */
	public GenericResult createGenericResult() {
		return new GenericResult();
	}

	/**
	 * Create an instance of {@link PublicInformation }
	 * 
	 */
	public PublicInformation createPublicInformation() {
		return new PublicInformation();
	}

	/**
	 * Create an instance of {@link PublicInformation.Providerlist }
	 * 
	 */
	public PublicInformation.Providerlist createPublicInformationProviderlist() {
		return new PublicInformation.Providerlist();
	}

	/**
	 * Create an instance of {@link Authentication }
	 * 
	 */
	public Authentication createAuthentication() {
		return new Authentication();
	}

	/**
	 * Create an instance of {@link Security }
	 * 
	 */
	public Security createSecurity() {
		return new Security();
	}

	/**
	 * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link Security }
	 * {@code >}
	 *
	 */
	@XmlElementDecl(namespace = "http://www.logink.org/", name = "Security")
	public JAXBElement<Security> createSecurity(Security value) {
		return new JAXBElement<Security>(_Security_QNAME, Security.class, null,
				value);
	}

	/**
	 * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link GenericResult }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://www.logink.org/", name = "GenericResult")
	public JAXBElement<GenericResult> createGenericResult(GenericResult value) {
		return new JAXBElement<GenericResult>(_GenericResult_QNAME,
				GenericResult.class, null, value);
	}

}
