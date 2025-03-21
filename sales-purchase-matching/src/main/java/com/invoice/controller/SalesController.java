//package com.invoice.controller;
//
//import com.invoice.model.Sales;
//import com.invoice.service.SalesService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/sales")
//public class SalesController {
//
//    @Autowired
//    private SalesService salesService;
//
//    @GetMapping
//    public ResponseEntity<List<Sales>> getAllSales() {
//        List<Sales> sales = salesService.getAllSales();
//        return new ResponseEntity<>(sales, HttpStatus.OK);
//    }
//    
//    @GetMapping("/{id}")
//    public ResponseEntity<Sales> getSalesById(@PathVariable Long id) {
//        Sales sales = salesService.getSalesById(id);
//        if (sales != null) {
//            return new ResponseEntity<>(sales, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//    
//    @GetMapping("/domain/{domainName}")
//    public ResponseEntity<List<Sales>> getSalesByDomain(@PathVariable String domainName) {
//        List<Sales> sales = salesService.getSalesByDomain(domainName);
//        return new ResponseEntity<>(sales, HttpStatus.OK);
//    }
//    
//    @GetMapping("/item/{itemName}")
//    public ResponseEntity<List<Sales>> getSalesByItem(@PathVariable String itemName) {
//        List<Sales> sales = salesService.getSalesByItem(itemName);
//        return new ResponseEntity<>(sales, HttpStatus.OK);
//    }
//    
//    @GetMapping("/renewals/{billingFrequency}")
//    public ResponseEntity<List<Sales>> getUpcomingRenewals(@PathVariable String billingFrequency) {
//        List<Sales> renewals = salesService.getUpcomingRenewals(billingFrequency);
//        return new ResponseEntity<>(renewals, HttpStatus.OK);
//    }
//    
//}
package com.invoice.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.model.Notification;
import com.invoice.model.Purchase;
import com.invoice.model.Sales;
import com.invoice.repository.NotificationRepository;
import com.invoice.repository.PurchaseRepository;
import com.invoice.repository.SalesRepository;
import com.invoice.service.SalesService;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    @Autowired
    private SalesService salesService;
    
    @Autowired
    private SalesRepository salesRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @GetMapping
    public ResponseEntity<List<Sales>> getAllSales() {
        List<Sales> sales = salesService.getAllSales();
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Sales> getSalesById(@PathVariable Long id) {
        Sales sales = salesService.getSalesById(id);
        if (sales != null) {
            return new ResponseEntity<>(sales, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/domain/{domainName}")
    public ResponseEntity<List<Sales>> getSalesByDomain(@PathVariable String domainName) {
        List<Sales> sales = salesService.getSalesByDomain(domainName);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }
    
    @GetMapping("/item/{itemName}")
    public ResponseEntity<List<Sales>> getSalesByItem(@PathVariable String itemName) {
        List<Sales> sales = salesService.getSalesByItem(itemName);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }
    
    @GetMapping("/renewals/{billingFrequency}")
    public ResponseEntity<List<Sales>> getUpcomingRenewals(@PathVariable String billingFrequency) {
        List<Sales> renewals = salesService.getUpcomingRenewals(billingFrequency);
        return new ResponseEntity<>(renewals, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable Long id) {
        try {
            salesRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @PostMapping
//    public ResponseEntity<Map<String, Object>> addSale(@RequestBody Sales sale) {
//        Map<String, Object> response = new HashMap<>();
//        
//        try {
//            // Calculate normalized monthly price
//            sale.setNormalizedMonthlyPrice(
//                calculateNormalizedPrice(sale.getPrice(), sale.getBillingFrequency()));
//            
//            // Save to database
//            Sales savedSale = salesRepository.save(sale);
//            
//            response.put("success", true);
//            response.put("message", "Sale added successfully");
//            response.put("data", savedSale);
//            return new ResponseEntity<>(response, HttpStatus.CREATED);
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.put("success", false);
//            response.put("message", "Failed to add sale: " + e.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
    // Helper method for calculating normalized price
    private Double calculateNormalizedPrice(Double price, String billingFrequency) {
        if (price == null || billingFrequency == null) {
            return price;
        }
        
        switch (billingFrequency.toLowerCase()) {
            case "monthly":
                return price;
            case "quarterly":
                return price / 3;
            case "semi annual":
            case "semiannual":
                return price / 6;
            case "annual":
                return price / 12;
            default:
                return price;
        }
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> addSale(@RequestBody Sales sale) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Calculate normalized monthly price
            sale.setNormalizedMonthlyPrice(
                calculateNormalizedPrice(sale.getPrice(), sale.getBillingFrequency()));
            
            // Save to database
            Sales savedSale = salesRepository.save(sale);
            
            // Generate notifications for the new sale
            checkForRenewalNotification(savedSale);
            checkForUnmatchedSaleNotification(savedSale);
            
            response.put("success", true);
            response.put("message", "Sale added successfully");
            response.put("data", savedSale);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to add sale: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to check for renewal notifications
    private void checkForRenewalNotification(Sales sale) {
        LocalDate today = LocalDate.now();
        
        // Determine threshold based on billing frequency
        int daysBefore;
        switch (sale.getBillingFrequency().toLowerCase()) {
            case "monthly":
                daysBefore = 4;
                break;
            case "quarterly":
                daysBefore = 7;
                break;
            case "semi annual":
                daysBefore = 14;
                break;
            case "annual":
                daysBefore = 30;
                break;
            default:
                daysBefore = 7;
                break;
        }
        
        LocalDate thresholdDate = today.plusDays(daysBefore);
        
        // Check if renewal date is within threshold
        if (sale.getEndDate() != null && 
            !sale.getEndDate().isBefore(today) && 
            !sale.getEndDate().isAfter(thresholdDate)) {
            
            Notification notification = new Notification();
            notification.setType("RENEWAL");
            notification.setDomainName(sale.getDomainName());
            notification.setItemName(sale.getItemName());
            notification.setMessage(String.format("Upcoming renewal for %s on %s. Renewal date: %s", 
                                sale.getItemName(), sale.getDomainName(), sale.getEndDate()));
            notification.setCreatedDate(today);
            notification.setDueDate(sale.getEndDate());
            notification.setStatus("NEW");
            
            notificationRepository.save(notification);
        }
    }

    // Helper method to create notification for unmatched sale
    private void checkForUnmatchedSaleNotification(Sales sale) {
        // Find potential matching purchases
        List<Purchase> potentialMatches = purchaseRepository.findByDomainNameContainingIgnoreCase(sale.getDomainName());
        
        // If no match found, create a notification
        if (potentialMatches.isEmpty()) {
            Notification notification = new Notification();
            notification.setType("UNMATCHED_SALE");
            notification.setDomainName(sale.getDomainName());
            notification.setItemName(sale.getItemName());
            notification.setMessage("No matching purchase found for: " + sale.getItemName() + 
                                  " on domain " + sale.getDomainName());
            notification.setCreatedDate(LocalDate.now());
            notification.setStatus("NEW");
            
            notificationRepository.save(notification);
        }
    }
    @DeleteMapping("/clear")
    public ResponseEntity<?> deleteAllSales() {
        try {
            salesRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}