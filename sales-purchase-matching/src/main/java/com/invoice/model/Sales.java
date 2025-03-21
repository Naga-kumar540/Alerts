package com.invoice.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "sales")
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;
    
    @Column(name = "invoice_number")
    private String invoiceNumber;
    
    @Column(name = "invoice_status")
    private String invoiceStatus;
    
    @Column(name = "customer_name")
    private String customerName;
    
    @Column(name = "gst_treatment")
    private String gstTreatment;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "invoice_value")
    private Double invoiceValue;
    
    @Column(name = "due_amount")
    private Double dueAmount;
    
    @Column(name = "item_name")
    private String itemName;
    
    @Column(name = "domain_name")
    private String domainName;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "price")
    private Double price;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "billing_frequency")
    private String billingFrequency;
    
    // Additional fields as per requirement
    @Column(name = "plan_type")
    private String planType;  // Flexible or Commitment
    
    // Calculated field for normalized monthly price (for comparison)
    @Transient
    private Double normalizedMonthlyPrice;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(LocalDate invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getGstTreatment() {
		return gstTreatment;
	}

	public void setGstTreatment(String gstTreatment) {
		this.gstTreatment = gstTreatment;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public Double getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(Double invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public Double getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(Double dueAmount) {
		this.dueAmount = dueAmount;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public Double getNormalizedMonthlyPrice() {
		return normalizedMonthlyPrice;
	}

	public void setNormalizedMonthlyPrice(Double normalizedMonthlyPrice) {
		this.normalizedMonthlyPrice = normalizedMonthlyPrice;
	}
    
    
}