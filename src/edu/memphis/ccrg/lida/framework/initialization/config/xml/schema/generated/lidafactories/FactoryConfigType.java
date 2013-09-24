//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.09.15 at 08:16:45 PM CDT 
//


package edu.memphis.ccrg.lida.framework.initialization.config.xml.schema.generated.lidafactories;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for factoryConfigType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="factoryConfigType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="local"/>
 *     &lt;enumeration value="external"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "factoryConfigType")
@XmlEnum
public enum FactoryConfigType {

    @XmlEnumValue("local")
    LOCAL("local"),
    @XmlEnumValue("external")
    EXTERNAL("external");
    private final String value;

    FactoryConfigType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FactoryConfigType fromValue(String v) {
        for (FactoryConfigType c: FactoryConfigType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}