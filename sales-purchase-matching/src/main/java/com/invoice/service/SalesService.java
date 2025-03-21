// SalesService.java
package com.invoice.service;

import com.invoice.model.Sales;
import com.invoice.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalesService {

    @Autowired
    private SalesRepository salesRepository;

    public List<Sales> getAllSales() {
        return salesRepository.findAll();
    }

    public Sales getSalesById(Long id) {
        return salesRepository.findById(id).orElse(null);
    }

    public List<Sales> getSalesByDomain(String domainName) {
        return salesRepository.findByDomainNameContainingIgnoreCase(domainName);
    }

    public List<Sales> getSalesByItem(String itemName) {
        return salesRepository.findByItemNameContainingIgnoreCase(itemName);
    }

    public List<Sales> getUpcomingRenewals(String billingFrequency) {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate;
        
        // Set threshold date based on billing frequency
        switch (billingFrequency.toLowerCase()) {
            case "monthly":
                thresholdDate = today.plusDays(4);
                break;
            case "quarterly":
                thresholdDate = today.plusDays(7);
                break;
            case "semi annual":
            case "semiannual":
                thresholdDate = today.plusDays(14);
                break;
            case "annual":
                thresholdDate = today.plusDays(30);
                break;
            default:
                thresholdDate = today.plusDays(7);
                break;
        }
        
        return salesRepository.findUpcomingRenewals(thresholdDate);
    }
}
