//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.09.15 at 08:16:45 PM CDT 
//


package edu.memphis.ccrg.lida.framework.xml.schema.generated.lida;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for lida complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="lida">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="globalparams" type="{http://ccrg.cs.memphis.edu/LidaXMLSchema}params" minOccurs="0"/>
 *         &lt;element name="taskmanager" type="{http://ccrg.cs.memphis.edu/LidaXMLSchema}taskmanager"/>
 *         &lt;element name="taskspawners" type="{http://ccrg.cs.memphis.edu/LidaXMLSchema}taskspawners"/>
 *         &lt;element name="submodules" type="{http://ccrg.cs.memphis.edu/LidaXMLSchema}modules"/>
 *         &lt;element name="listeners" type="{http://ccrg.cs.memphis.edu/LidaXMLSchema}listeners" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lida", propOrder = {
    "globalparams",
    "taskmanager",
    "taskspawners",
    "submodules",
    "listeners"
})
public class Lida {

    @XmlElementWrapper(name = "globalparams") 
    @XmlElement(name = "param") 
    protected List<Param> globalparams;
    
    @XmlElement(required = true)
    protected Taskmanager taskmanager;
    
    @XmlElementWrapper(name = "taskspawners") 
    @XmlElement(name = "taskspawner", required = true) 
    protected List<Taskspawner> taskspawners;
    
    @XmlElementWrapper(name = "submodules") 
    @XmlElement(name = "module", required = true) 
    protected List<Module> submodules;
    
    @XmlElementWrapper(name = "listeners") 
    @XmlElement(name = "listener", required = true) 
    protected List<Listener> listeners;

    /**
     * Gets the value of the globalparams property.
     * 
     */
    public List<Param> getGlobalparams() {
        return globalparams;
    }

    /**
     * Sets the value of the globalparams property.
     *     
     */
    public void setGlobalparams(List<Param> params) {
        this.globalparams = params;
    }

    /**
     * Gets the value of the taskmanager property.
     * 
     * @return
     *     possible object is
     *     {@link Taskmanager }
     *     
     */
    public Taskmanager getTaskmanager() {
        return taskmanager;
    }

    /**
     * Sets the value of the taskmanager property.
     * 
     * @param value
     *     allowed object is
     *     {@link Taskmanager }
     *     
     */
    public void setTaskmanager(Taskmanager value) {
        this.taskmanager = value;
    }

    public List<Taskspawner> getTaskspawners() {
        return taskspawners;
    }

    public void setTaskspawners(List<Taskspawner> taskspawners) {
        this.taskspawners = taskspawners;
    }

    public List<Module> getSubmodules() {
        return submodules;
    }

    public void setSubmodules(List<Module> submodules) {
        this.submodules = submodules;
    }

    public List<Listener> getListeners() {
        return listeners;
    }
    
    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

}
