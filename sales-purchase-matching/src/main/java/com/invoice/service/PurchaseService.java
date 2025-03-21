// PurchaseService.java
package com.invoice.service;

import com.invoice.model.Purchase;
import com.invoice.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    public List<Purchase> getPurchasesByDomain(String domainName) {
        return purchaseRepository.findByDomainNameContainingIgnoreCase(domainName);
    }

    public List<Purchase> getPurchasesBySubscription(String subscription) {
        return purchaseRepository.findBySubscriptionContainingIgnoreCase(subscription);
    }
}
