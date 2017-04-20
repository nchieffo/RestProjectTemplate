package it.tecla.examples.model;

import java.util.Date;

public class Model {

	private Object nullValue;
	private Boolean BooleanValue;
	private Integer intValue;
	private Float floatValue;
	private Double doubleValue;
	private String stringValue;
	private Date dateValue;
	
	public Object getNullValue() {
		return nullValue;
	}
	public void setNullValue(Object nullValue) {
		this.nullValue = nullValue;
	}
	public Boolean isBooleanValue() {
		return BooleanValue;
	}
	public void setBooleanValue(Boolean BooleanValue) {
		this.BooleanValue = BooleanValue;
	}
	public Integer getIntValue() {
		return intValue;
	}
	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}
	public Float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}
	public Double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public Date getDateValue() {
		return dateValue;
	}
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	@Override
	public String toString() {
		return "Model [nullValue=" + nullValue + ", BooleanValue=" + BooleanValue + ", intValue=" + intValue
				+ ", floatValue=" + floatValue + ", doubleValue=" + doubleValue + ", stringValue=" + stringValue
				+ ", dateValue=" + dateValue + "]";
	}
	
}
