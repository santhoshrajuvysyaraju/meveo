package org.meveocrm.model.dwh;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "DWH_MEASURED_VALUE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "DWH_MEASURED_VALUE_SEQ")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasuredValue extends BaseEntity {

	private static final long serialVersionUID = -3343485468990186936L;

	@ManyToOne
	@JoinColumn(name = "MEASURABLE_QUANTITY", nullable = true, unique = false, updatable = true)
	private MeasurableQuantity measurableQuantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "MEASUREMENT_PERIOD")
	private MeasurementPeriodEnum measurementPeriod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE")
	@XmlTransient
	private Date date;

	@Column(name = "DIMENSION_1", length = 255)
	private String dimension1;

	@Column(name = "DIMENSION_2", length = 255)
	private String dimension2;

	@Column(name = "DIMENSION_3", length = 255)
	private String dimension3;

	@Column(name = "DIMENSION_4", length = 255)
	private String dimension4;

	@Column(name = "VALUE", precision = NB_PRECISION, scale = NB_DECIMALS)
	@XmlTransient
	private BigDecimal value;

	public MeasurableQuantity getMeasurableQuantity() {
		return measurableQuantity;
	}

	public void setMeasurableQuantity(MeasurableQuantity measurableQuantity) {
		this.measurableQuantity = measurableQuantity;
	}

	public MeasurementPeriodEnum getMeasurementPeriod() {
		return measurementPeriod;
	}

	public void setMeasurementPeriod(MeasurementPeriodEnum measurementPeriod) {
		this.measurementPeriod = measurementPeriod;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDimension1() {
		return dimension1;
	}

	public void setDimension1(String dimension1) {
		this.dimension1 = dimension1;
	}

	public String getDimension2() {
		return dimension2;
	}

	public void setDimension2(String dimension2) {
		this.dimension2 = dimension2;
	}

	public String getDimension3() {
		return dimension3;
	}

	public void setDimension3(String dimension3) {
		this.dimension3 = dimension3;
	}

	public String getDimension4() {
		return dimension4;
	}

	public void setDimension4(String dimension4) {
		this.dimension4 = dimension4;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
