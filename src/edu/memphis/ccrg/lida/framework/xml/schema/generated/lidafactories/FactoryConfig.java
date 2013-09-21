//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.09.15 at 08:16:45 PM CDT 
//


package edu.memphis.ccrg.lida.framework.xml.schema.generated.lidafactories;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import edu.memphis.ccrg.lida.framework.xml.schema.generated.lida.Param;

/**
 * <p>Java class for factoryConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="factoryConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="params" type="{http://ccrg.cs.memphis.edu/LidaXMLSchema}params" minOccurs="0"/>
 *         &lt;element name="factoryObjects" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="factoryObject" type="{http://ccrg.cs.memphis.edu/LidaFactoriesXMLSchema}factoryObject" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{http://ccrg.cs.memphis.edu/LidaFactoriesXMLSchema}factoryConfigType" />
 *       &lt;attribute name="filename" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "factoryConfig", propOrder = {
    "params",
    "factoryObjects"
})
public class FactoryConfig {

    @XmlElementWrapper(name = "params") 
    @XmlElement(name = "param") 
    protected List<Param> params;
   
    @XmlElementWrapper(name = "factoryObjects") 
    @XmlElement(name = "factoryObject") 
    protected List<FactoryObject> factoryObjects;
    
    @XmlAttribute(name = "type", required = true)
    protected FactoryConfigType type;
    
    @XmlAttribute(name = "filename")
    protected String filename;

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

    public List<FactoryObject> getFactoryObjects() {
        return factoryObjects;
    }

    public void setFactoryObjects(List<FactoryObject> factoryObjects) {
        this.factoryObjects = factoryObjects;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link FactoryConfigType }
     *     
     */
    public FactoryConfigType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link FactoryConfigType }
     *     
     */
    public void setType(FactoryConfigType value) {
        this.type = value;
    }

    /**
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilename(String value) {
        this.filename = value;
    }

}
