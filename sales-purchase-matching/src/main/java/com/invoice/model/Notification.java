package com.invoice.model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "type")
    private String type;  // RENEWAL, MISMATCH_PRICE, MISMATCH_DATE, MISMATCH_QUANTITY
    
    @Column(name = "domain_name")
    private String domainName;
    
    @Column(name = "item_name")
    private String itemName;
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "created_date")
    private LocalDate createdDate;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "status")
    private String status;  // NEW, READ, RESOLVED
    
	public Notification() {
	}
	
	public Notification(Long id, String type, String domainName, String itemName, String message, LocalDate createdDate,
			LocalDate dueDate, String status) {
		super();
		this.id = id;
		this.type = type;
		this.domainName = domainName;
		this.itemName = itemName;
		this.message = message;
		this.createdDate = createdDate;
		this.dueDate = dueDate;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, domainName, dueDate, id, itemName, message, status, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		return Objects.equals(createdDate, other.createdDate) && Objects.equals(domainName, other.domainName)
				&& Objects.equals(dueDate, other.dueDate) && Objects.equals(id, other.id)
				&& Objects.equals(itemName, other.itemName) && Objects.equals(message, other.message)
				&& Objects.equals(status, other.status) && Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		return "Notification [id=" + id + ", type=" + type + ", domainName=" + domainName + ", itemName=" + itemName
				+ ", message=" + message + ", createdDate=" + createdDate + ", dueDate=" + dueDate + ", status="
				+ status + "]";
	}
    
    
}
