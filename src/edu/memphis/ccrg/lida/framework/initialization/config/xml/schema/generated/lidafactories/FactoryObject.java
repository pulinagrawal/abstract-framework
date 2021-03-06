//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.09.22 at 08:38:08 AM CDT 
//

package edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.generated.lidafactories;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.generated.lida.Param;
import edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.helper.StringTrimAdapter;

/**
 * <p>
 * Java class for factoryObject complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="factoryObject">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="impl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="params" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="param" type="{http://ccrg.cs.memphis.edu/LidaXMLSchema}param" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "factoryObject", propOrder = {

})
public class FactoryObject {

    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    @XmlElement(required = true)
    protected String type;
    @XmlJavaTypeAdapter(StringTrimAdapter.class)
    @XmlElement(required = true)
    protected String impl;

    @XmlElement(name = "param")
    @XmlElementWrapper(name = "params")
    protected List<Param> params;

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "context")
    protected FactoryObjectContextType context;
    @XmlAttribute(name = "default")
    protected Boolean _default;

    /**
     * Gets the value of the params property.
     * 
     * @return a {@link List}
     */
    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the impl property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getImpl() {
        return impl;
    }

    /**
     * Sets the value of the impl property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setImpl(String value) {
        this.impl = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setName(String value) {
        this.name = value;
    }
    
    /**
     * Gets the value of the context property.
     * 
     * @return
     *     possible object is
     *     {@link FactoryObjectContextType }
     *     
     */
    public FactoryObjectContextType getContext() {
        return context;
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *     allowed object is
     *     {@link FactoryObjectContextType }
     *     
     */
    public void setContext(FactoryObjectContextType value) {
        this.context = value;
    }

    /**
     * Gets the value of the default property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDefault() {
        if (_default == null) {
            return Boolean.FALSE;
        }
        return _default;
    }

    /**
     * Sets the value of the default property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDefault(Boolean value) {
        this._default = value;
    }
}