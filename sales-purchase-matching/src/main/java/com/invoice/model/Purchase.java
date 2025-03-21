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
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "console")
    private String console;
    
    @Column(name = "domain_name")
    private String domainName;
    
    @Column(name = "subscription")
    private String subscription;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "order_name")
    private String orderName;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "po_number")
    private String poNumber;
    
    @Column(name = "amount")
    private Double amount;
    
    @Column(name = "customer_id")
    private String customerId;
    
    @Column(name = "sku_id")
    private String skuId;
    
    @Column(name = "rate_pu")
    private Double ratePu;
    
    // Additional derived fields
    @Column(name = "plan_type")
    private String planType;  // Derived from description
    
    // Calculated field for normalized monthly price (for comparison)
    @Transient
    private Double normalizedMonthlyPrice;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConsole() {
		return console;
	}

	public void setConsole(String console) {
		this.console = console;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public Double getRatePu() {
		return ratePu;
	}

	public void setRatePu(Double ratePu) {
		this.ratePu = ratePu;
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
