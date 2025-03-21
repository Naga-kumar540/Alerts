// ComparisonResult.java
package com.invoice.model;

import lombok.Data;
import java.util.List;

@Data
public class ComparisonResult {
    private int totalSales;
    private int totalPurchases;
    private int matchedItems;
    private int unmatchedSales;
    private int unmatchedPurchases;
    private List<Notification> notifications;
    private List<SalesPurchaseMatch> matches;
    
    @Data
    public static class SalesPurchaseMatch {
        private Sales sale;
        private Purchase purchase;
        private boolean customerNameMatch;
        private boolean renewalDateMatch;
        private boolean priceMatch;
        private boolean quantityMatch;
        private String mismatchReason;
		public Sales getSale() {
			return sale;
		}
		public void setSale(Sales sale) {
			this.sale = sale;
		}
		public Purchase getPurchase() {
			return purchase;
		}
		public void setPurchase(Purchase purchase) {
			this.purchase = purchase;
		}
		public boolean isCustomerNameMatch() {
			return customerNameMatch;
		}
		public void setCustomerNameMatch(boolean customerNameMatch) {
			this.customerNameMatch = customerNameMatch;
		}
		public boolean isRenewalDateMatch() {
			return renewalDateMatch;
		}
		public void setRenewalDateMatch(boolean renewalDateMatch) {
			this.renewalDateMatch = renewalDateMatch;
		}
		public boolean isPriceMatch() {
			return priceMatch;
		}
		public void setPriceMatch(boolean priceMatch) {
			this.priceMatch = priceMatch;
		}
		public boolean isQuantityMatch() {
			return quantityMatch;
		}
		public void setQuantityMatch(boolean quantityMatch) {
			this.quantityMatch = quantityMatch;
		}
		public String getMismatchReason() {
			return mismatchReason;
		}
		public void setMismatchReason(String mismatchReason) {
			this.mismatchReason = mismatchReason;
		}
        
    }

	public int getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(int totalSales) {
		this.totalSales = totalSales;
	}

	public int getTotalPurchases() {
		return totalPurchases;
	}

	public void setTotalPurchases(int totalPurchases) {
		this.totalPurchases = totalPurchases;
	}

	public int getMatchedItems() {
		return matchedItems;
	}

	public void setMatchedItems(int matchedItems) {
		this.matchedItems = matchedItems;
	}

	public int getUnmatchedSales() {
		return unmatchedSales;
	}

	public void setUnmatchedSales(int unmatchedSales) {
		this.unmatchedSales = unmatchedSales;
	}

	public int getUnmatchedPurchases() {
		return unmatchedPurchases;
	}

	public void setUnmatchedPurchases(int unmatchedPurchases) {
		this.unmatchedPurchases = unmatchedPurchases;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public List<SalesPurchaseMatch> getMatches() {
		return matches;
	}

	public void setMatches(List<SalesPurchaseMatch> matches) {
		this.matches = matches;
	}
    
    
}