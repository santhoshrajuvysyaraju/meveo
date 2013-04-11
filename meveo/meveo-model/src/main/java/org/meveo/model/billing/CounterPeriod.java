package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name="BILLING_COUNTER_PERIOD")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_COUNTER_PERIOD_SEQ")
public class CounterPeriod extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4924601467998738157L;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "COUNTER_INSTANCE_ID")
	private CounterInstance counterInstance;
	

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_START_DATE")
    private Date periodStartDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_END_DATE")
    private Date periodEndDate;
    
	@Column(name = "VALUE", precision = 23, scale = 12)
	@Digits(integer = 23, fraction = 12)
	private BigDecimal value;


	public CounterInstance getCounterInstance() {
		return counterInstance;
	}

	public void setCounterInstance(CounterInstance counterInstance) {
		this.counterInstance = counterInstance;
	}

	public Date getPeriodStartDate() {
		return periodStartDate;
	}

	public void setPeriodStartDate(Date periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	public Date getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(Date periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	
}
