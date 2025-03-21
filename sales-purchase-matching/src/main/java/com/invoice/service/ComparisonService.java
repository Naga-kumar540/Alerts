package com.invoice.service;

import com.invoice.model.*;
import com.invoice.repository.NotificationRepository;
import com.invoice.repository.PurchaseRepository;
import com.invoice.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComparisonService {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;

    private static final double PRICE_TOLERANCE_PERCENTAGE = 10.0; // Increased tolerance to 10%

    public ComparisonResult compareAllSalesAndPurchases() {
        List<Sales> allSales = salesRepository.findAll();
        List<Purchase> allPurchases = purchaseRepository.findAll();
        
        System.out.println("Running comparison with " + allSales.size() + " sales and " + allPurchases.size() + " purchases");
        
        ComparisonResult result = new ComparisonResult();
        result.setTotalSales(allSales.size());
        result.setTotalPurchases(allPurchases.size());
        
        List<ComparisonResult.SalesPurchaseMatch> matches = new ArrayList<>();
        List<Notification> notifications = new ArrayList<>();
        
        // Clear previous notifications to avoid duplicates
        notificationRepository.deleteAll();
        
        // Map to track matched purchases
        Map<Long, Boolean> purchaseMatched = new HashMap<>();
        for (Purchase purchase : allPurchases) {
            purchaseMatched.put(purchase.getId(), false);
        }
        
        // For each sale, try to find matching purchases
        for (Sales sale : allSales) {
            System.out.println("Checking matches for sale: " + sale.getItemName() + " for domain " + sale.getDomainName());
            
            // Try to find matching purchases by domain name and item/subscription
            List<Purchase> potentialMatches = findPotentialMatches(sale);
            
            boolean foundMatch = false;
            for (Purchase purchase : potentialMatches) {
                ComparisonResult.SalesPurchaseMatch match = compareItems(sale, purchase);
                
                // Record this as a match (even if some details don't match)
                matches.add(match);
                foundMatch = true;
                purchaseMatched.put(purchase.getId(), true);
                
                System.out.println("Found match between sale [" + sale.getItemName() + 
                                 "] and purchase [" + purchase.getSubscription() + "]");
                
                // Create notifications for mismatches
                createMismatchNotifications(match, notifications);
            }
            
            if (!foundMatch) {
                // Sale has no matching purchase
                result.setUnmatchedSales(result.getUnmatchedSales() + 1);
                
                System.out.println("No match found for sale: " + sale.getItemName() + " for " + sale.getDomainName());
                
                // Create a notification for unmatched sale
                Notification notification = new Notification();
                notification.setType("UNMATCHED_SALE");
                notification.setDomainName(sale.getDomainName());
                notification.setItemName(sale.getItemName());
                notification.setMessage("Sale has no matching purchase: " + sale.getItemName() + " for " + sale.getDomainName());
                notification.setCreatedDate(LocalDate.now());
                notification.setStatus("NEW");
                notifications.add(notification);
            }
            
            // Always check for upcoming renewals
            checkForRenewalNotification(sale, notifications);
        }
        
        // Count unmatched purchases
        for (Map.Entry<Long, Boolean> entry : purchaseMatched.entrySet()) {
            if (!entry.getValue()) {
                result.setUnmatchedPurchases(result.getUnmatchedPurchases() + 1);
                
                // Create notification for unmatched purchase
                Purchase purchase = purchaseRepository.findById(entry.getKey()).orElse(null);
                if (purchase != null) {
                    System.out.println("Unmatched purchase found: " + purchase.getSubscription() + 
                                     " for " + purchase.getDomainName());
                    
                    Notification notification = new Notification();
                    notification.setType("UNMATCHED_PURCHASE");
                    notification.setDomainName(purchase.getDomainName());
                    notification.setItemName(purchase.getSubscription());
                    notification.setMessage("Purchase has no matching sale: " + purchase.getSubscription() + 
                                          " for " + purchase.getDomainName());
                    notification.setCreatedDate(LocalDate.now());
                    notification.setStatus("NEW");
                    notifications.add(notification);
                }
            }
        }
        
        result.setMatchedItems(matches.size());
        result.setMatches(matches);
        result.setNotifications(notifications);
        
        // Save all notifications to database
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
            System.out.println("Saved " + notifications.size() + " notifications");
        } else {
            System.out.println("No notifications generated");
        }
        
        return result;
    }
    
    private List<Purchase> findPotentialMatches(Sales sale) {
        // Clean domain name (remove "for domain" text if present)
        String originalDomainName = sale.getDomainName().trim();
        
        // Process domain name - create cleaned version
        String processedDomainName;
        if (originalDomainName.contains("for domain")) {
            processedDomainName = originalDomainName.substring(originalDomainName.indexOf("for domain") + 11).trim();
        } else {
            processedDomainName = originalDomainName;
        }
        
        // Create final version with newlines and spaces removed
        final String domainName = processedDomainName.replaceAll("[\\n\\r]", "").trim();
        
        System.out.println("Looking for matches for domain: '" + domainName + "'");
        
        // Try a more flexible matching approach with the final domainName
        List<Purchase> allPurchases = purchaseRepository.findAll();
        List<Purchase> matches = allPurchases.stream()
            .filter(p -> p.getDomainName() != null &&
                      (p.getDomainName().toLowerCase().contains(domainName.toLowerCase()) ||
                       domainName.toLowerCase().contains(p.getDomainName().toLowerCase())))
            .collect(Collectors.toList());
        
        System.out.println("Found " + matches.size() + " domain matches");
        
        // Filter further by subscription name similarity
        List<Purchase> filteredMatches = new ArrayList<>();
        for (Purchase purchase : matches) {
            // Simple string matching for now - could be enhanced with fuzzy matching
            if (isServiceNameSimilar(sale.getItemName(), purchase.getSubscription())) {
                System.out.println("Service name match found: " + 
                                 sale.getItemName() + " ~ " + purchase.getSubscription());
                filteredMatches.add(purchase);
            }
        }
        
        System.out.println("After filtering by service name: " + filteredMatches.size() + " matches");
        
        return filteredMatches;
    }
    
    private boolean isServiceNameSimilar(String salesItemName, String purchaseSubscription) {
        if (salesItemName == null || purchaseSubscription == null) {
            return false;
        }
        
        // Clean up the strings first - remove newlines, extra spaces
        String item1 = salesItemName.replaceAll("[\\n\\r]", "").trim().toLowerCase();
        String item2 = purchaseSubscription.replaceAll("[\\n\\r]", "").trim().toLowerCase();
        
        // Check if they are the same or if one contains the other
        boolean match = item1.equals(item2) || 
                     item1.contains(item2) || 
                     item2.contains(item1);
        
        // Try more aggressive matching with keywords if standard matching fails
        if (!match) {
            // Check for common keywords
            String[] keywords = {"google", "workspace", "microsoft", "office", "365", "starter", 
                               "standard", "business", "enterprise", "basic", "premium"};
            
            int matchCount = 0;
            for (String keyword : keywords) {
                if (item1.contains(keyword) && item2.contains(keyword)) {
                    matchCount++;
                }
            }
            
            // If they share at least 2 keywords, consider it a match
            match = matchCount >= 2;
        }
        
        return match;
    }
    
    private ComparisonResult.SalesPurchaseMatch compareItems(Sales sale, Purchase purchase) {
        ComparisonResult.SalesPurchaseMatch match = new ComparisonResult.SalesPurchaseMatch();
        match.setSale(sale);
        match.setPurchase(purchase);
        
        // Check customer name based on domain (basic matching, can be enhanced)
        match.setCustomerNameMatch(true);  // Assuming domain match implies customer match
        
        // Check renewal date match
        boolean datesMatch = checkDatesMatch(sale.getEndDate(), purchase.getEndDate());
        match.setRenewalDateMatch(datesMatch);
        
        // Check price match
        boolean priceMatch = checkPriceMatch(sale.getNormalizedMonthlyPrice(), purchase.getNormalizedMonthlyPrice());
        match.setPriceMatch(priceMatch);
        
        // Check quantity match
        boolean quantityMatch = (sale.getQuantity() != null && purchase.getQuantity() != null && 
                                sale.getQuantity().equals(purchase.getQuantity()));
        match.setQuantityMatch(quantityMatch);
        
        // Build mismatch reason
        StringBuilder mismatchReason = new StringBuilder();
        if (!datesMatch) {
            mismatchReason.append("Renewal dates do not match. ");
        }
        if (!priceMatch) {
            mismatchReason.append("Prices do not match. ");
        }
        if (!quantityMatch) {
            mismatchReason.append("Quantities do not match. ");
        }
        
        match.setMismatchReason(mismatchReason.toString());
        
        return match;
    }
    
    private boolean checkDatesMatch(LocalDate saleEndDate, LocalDate purchaseEndDate) {
        if (saleEndDate == null || purchaseEndDate == null) {
            return false;
        }
        
        // Consider dates to match if they are within 2 days of each other
        long daysDifference = Math.abs(ChronoUnit.DAYS.between(saleEndDate, purchaseEndDate));
        return daysDifference <= 2;
    }
    
    private boolean checkPriceMatch(Double salePrice, Double purchasePrice) {
        if (salePrice == null || purchasePrice == null) {
            return false;
        }
        
        // Allow for GST and other small variations with a tolerance percentage
        double difference = Math.abs(salePrice - purchasePrice);
        double percentageDifference = (difference / Math.max(0.01, purchasePrice)) * 100;
        
        System.out.println("Price comparison: Sale=" + salePrice + ", Purchase=" + purchasePrice + 
                         ", Difference=" + percentageDifference + "%");
        
        return percentageDifference <= PRICE_TOLERANCE_PERCENTAGE;
    }
    
    private void createMismatchNotifications(ComparisonResult.SalesPurchaseMatch match, List<Notification> notifications) {
        Sales sale = match.getSale();
        Purchase purchase = match.getPurchase();
        
        // Check for date mismatch
        if (!match.isRenewalDateMatch()) {
            System.out.println("Date mismatch detected: Sale=" + sale.getEndDate() + 
                             ", Purchase=" + purchase.getEndDate());
            
            Notification notification = new Notification();
            notification.setType("MISMATCH_DATE");
            notification.setDomainName(sale.getDomainName());
            notification.setItemName(sale.getItemName());
            notification.setMessage(String.format("Renewal date mismatch for %s on %s. Sale: %s, Purchase: %s", 
                                  sale.getItemName(), sale.getDomainName(), 
                                  sale.getEndDate(), purchase.getEndDate()));
            notification.setCreatedDate(LocalDate.now());
            notification.setDueDate(sale.getEndDate());
            notification.setStatus("NEW");
            notifications.add(notification);
        }
        
        // Check for price mismatch
        if (!match.isPriceMatch()) {
            System.out.println("Price mismatch detected: Sale=" + sale.getNormalizedMonthlyPrice() + 
                             ", Purchase=" + purchase.getNormalizedMonthlyPrice());
            
            Notification notification = new Notification();
            notification.setType("MISMATCH_PRICE");
            notification.setDomainName(sale.getDomainName());
            notification.setItemName(sale.getItemName());
            notification.setMessage(String.format("Price mismatch for %s on %s. Sale: %.2f, Purchase: %.2f", 
                                  sale.getItemName(), sale.getDomainName(), 
                                  sale.getNormalizedMonthlyPrice(), purchase.getNormalizedMonthlyPrice()));
            notification.setCreatedDate(LocalDate.now());
            notification.setDueDate(sale.getEndDate());
            notification.setStatus("NEW");
            notifications.add(notification);
        }
        
        // Check for quantity mismatch
        if (!match.isQuantityMatch()) {
            System.out.println("Quantity mismatch detected: Sale=" + sale.getQuantity() + 
                             ", Purchase=" + purchase.getQuantity());
            
            Notification notification = new Notification();
            notification.setType("MISMATCH_QUANTITY");
            notification.setDomainName(sale.getDomainName());
            notification.setItemName(sale.getItemName());
            notification.setMessage(String.format("Quantity mismatch for %s on %s. Sale: %d, Purchase: %d", 
                                  sale.getItemName(), sale.getDomainName(), 
                                  sale.getQuantity(), purchase.getQuantity()));
            notification.setCreatedDate(LocalDate.now());
            notification.setDueDate(sale.getEndDate());
            notification.setStatus("NEW");
            notifications.add(notification);
        }
    }
    
    // New method to check for renewal notifications
    private void checkForRenewalNotification(Sales sale, List<Notification> notifications) {
        LocalDate today = LocalDate.now();
        
        // Skip if end date is missing or in the past
        if (sale.getEndDate() == null || sale.getEndDate().isBefore(today)) {
            return;
        }
        
        // Determine notification threshold based on billing frequency
        int daysBefore;
        String frequency = sale.getBillingFrequency();
        if (frequency == null) {
            // Default to 7 days if frequency is not set
            daysBefore = 7;
        } else {
            frequency = frequency.toLowerCase().trim();
            if (frequency.contains("month")) {
                daysBefore = 4;
            } else if (frequency.contains("quarter")) {
                daysBefore = 7;
            } else if (frequency.contains("semi") || frequency.contains("half")) {
                daysBefore = 14;
            } else if (frequency.contains("annual") || frequency.contains("year")) {
                daysBefore = 30;
            } else {
                daysBefore = 7; // Default
            }
        }
        
        // Calculate threshold date
        LocalDate thresholdDate = today.plusDays(daysBefore);
        
        // Check if renewal is coming up within the threshold
        if (!sale.getEndDate().isAfter(thresholdDate)) {
            System.out.println("Renewal notification triggered for: " + sale.getItemName() + 
                             " due on " + sale.getEndDate());
            
            Notification notification = new Notification();
            notification.setType("RENEWAL");
            notification.setDomainName(sale.getDomainName());
            notification.setItemName(sale.getItemName());
            notification.setMessage(String.format("Upcoming renewal for %s on %s. Renewal date: %s", 
                                  sale.getItemName(), sale.getDomainName(), sale.getEndDate()));
            notification.setCreatedDate(LocalDate.now());
            notification.setDueDate(sale.getEndDate());
            notification.setStatus("NEW");
            notifications.add(notification);
        }
    }
    
    // Method to manually trigger renewal checks
    public List<Notification> checkAllRenewals() {
        List<Sales> allSales = salesRepository.findAll();
        List<Notification> notifications = new ArrayList<>();
        
        System.out.println("Checking renewals for " + allSales.size() + " sales");
        
        for (Sales sale : allSales) {
            checkForRenewalNotification(sale, notifications);
        }
        
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
            System.out.println("Saved " + notifications.size() + " renewal notifications");
        } else {
            System.out.println("No renewal notifications generated");
        }
        
        return notifications;
    }
}