//// PurchaseController.java
//package com.invoice.controller;
//
//import com.invoice.model.Purchase;
//import com.invoice.service.PurchaseService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController	
//@RequestMapping("/api/purchases")
//public class PurchaseController {
//
//    @Autowired
//    private PurchaseService purchaseService;
//
//    @GetMapping
//    public ResponseEntity<List<Purchase>> getAllPurchases() {
//        List<Purchase> purchases = purchaseService.getAllPurchases();
//        return new ResponseEntity<>(purchases, HttpStatus.OK);
//    }
//    
//    @GetMapping("/{id}")
//    public ResponseEntity<Purchase> getPurchaseById(@PathVariable Long id) {
//        Purchase purchase = purchaseService.getPurchaseById(id);
//        if (purchase != null) {
//            return new ResponseEntity<>(purchase, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//    
//    @GetMapping("/domain/{domainName}")
//    public ResponseEntity<List<Purchase>> getPurchasesByDomain(@PathVariable String domainName) {
//        List<Purchase> purchases = purchaseService.getPurchasesByDomain(domainName);
//        return new ResponseEntity<>(purchases, HttpStatus.OK);
//    }
//    
//    @GetMapping("/subscription/{subscription}")
//    public ResponseEntity<List<Purchase>> getPurchasesBySubscription(@PathVariable String subscription) {
//        List<Purchase> purchases = purchaseService.getPurchasesBySubscription(subscription);
//        return new ResponseEntity<>(purchases, HttpStatus.OK);
//    }
//}
// PurchaseController.java
package com.invoice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoice.model.Purchase;
import com.invoice.repository.PurchaseRepository;
import com.invoice.service.PurchaseService;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;
    
    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @GetMapping
    public ResponseEntity<List<Purchase>> getAllPurchases() {
        List<Purchase> purchases = purchaseService.getAllPurchases();
        return new ResponseEntity<>(purchases, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchaseById(@PathVariable Long id) {
        Purchase purchase = purchaseService.getPurchaseById(id);
        if (purchase != null) {
            return new ResponseEntity<>(purchase, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/domain/{domainName}")
    public ResponseEntity<List<Purchase>> getPurchasesByDomain(@PathVariable String domainName) {
        List<Purchase> purchases = purchaseService.getPurchasesByDomain(domainName);
        return new ResponseEntity<>(purchases, HttpStatus.OK);
    }
    
    @GetMapping("/subscription/{subscription}")
    public ResponseEntity<List<Purchase>> getPurchasesBySubscription(@PathVariable String subscription) {
        List<Purchase> purchases = purchaseService.getPurchasesBySubscription(subscription);
        return new ResponseEntity<>(purchases, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> addPurchase(@RequestBody Purchase purchase) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Determine plan type from description
            if (purchase.getDescription() != null && 
                purchase.getDescription().toLowerCase().contains("commitment")) {
                purchase.setPlanType("Commitment");
            } else {
                purchase.setPlanType("Flexible");
            }
            
            // Calculate normalized monthly price
            purchase.setNormalizedMonthlyPrice(purchase.getRatePu());
            
            // Save to database
            Purchase savedPurchase = purchaseRepository.save(purchase);
            
            response.put("success", true);
            response.put("message", "Purchase added successfully");
            response.put("data", savedPurchase);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to add purchase: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
