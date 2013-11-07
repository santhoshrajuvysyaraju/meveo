//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.30 at 08:09:14 AM CST 
//


package org.meveo.asg.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QuantityRangeChargeData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuantityRangeChargeData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ApplyPerOrganization" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Min" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Max" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Prices" type="{}ArrayOfChargePriceData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuantityRangeChargeData", propOrder = {
    "applyPerOrganization",
    "min",
    "max",
    "prices"
})
public class QuantityRangeChargeData {

    @XmlElement(name = "ApplyPerOrganization")
    protected boolean applyPerOrganization;
    @XmlElement(name = "Min")
    protected int min;
    @XmlElement(name = "Max")
    protected int max;
    @XmlElement(name = "Prices")
    protected ArrayOfChargePriceData prices;

    /**
     * Gets the value of the applyPerOrganization property.
     * 
     */
    public boolean isApplyPerOrganization() {
        return applyPerOrganization;
    }

    /**
     * Sets the value of the applyPerOrganization property.
     * 
     */
    public void setApplyPerOrganization(boolean value) {
        this.applyPerOrganization = value;
    }

    /**
     * Gets the value of the min property.
     * 
     */
    public int getMin() {
        return min;
    }

    /**
     * Sets the value of the min property.
     * 
     */
    public void setMin(int value) {
        this.min = value;
    }

    /**
     * Gets the value of the max property.
     * 
     */
    public int getMax() {
        return max;
    }

    /**
     * Sets the value of the max property.
     * 
     */
    public void setMax(int value) {
        this.max = value;
    }

    /**
     * Gets the value of the prices property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfChargePriceData }
     *     
     */
    public ArrayOfChargePriceData getPrices() {
        return prices;
    }

    /**
     * Sets the value of the prices property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfChargePriceData }
     *     
     */
    public void setPrices(ArrayOfChargePriceData value) {
        this.prices = value;
    }

}
