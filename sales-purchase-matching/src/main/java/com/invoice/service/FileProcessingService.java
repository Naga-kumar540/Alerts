//// STEP 4: Services
//// FileProcessingService.java
//package com.invoice.service;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.invoice.model.Purchase;
//import com.invoice.model.Sales;
//import com.invoice.repository.NotificationRepository;
//import com.invoice.repository.PurchaseRepository;
//import com.invoice.repository.SalesRepository;
//import com.invoice.util.ExcelHelper;
//
//@Service
//public class FileProcessingService {
//
//	@Autowired
//	private NotificationRepository notificationRepository;
//	
//    @Autowired
//    private SalesRepository salesRepository;
//
//    @Autowired
//    private PurchaseRepository purchaseRepository;
//
//    @Autowired
//    private ExcelHelper excelHelper;
//
//    public void processSalesFile(MultipartFile file) throws IOException {
//        List<Sales> salesList = excelHelper.parseSalesExcel(file.getInputStream());
//        
//        // Process and save each sales record
//        for (Sales sale : salesList) {
//            // Calculate normalized monthly price based on billing frequency
//            sale.setNormalizedMonthlyPrice(calculateNormalizedPrice(sale.getPrice(), sale.getBillingFrequency()));
//            
//            // Set plan type if not already set
//            if (sale.getPlanType() == null || sale.getPlanType().isEmpty()) {
//                // Default to "Flexible" if not specified
//                sale.setPlanType("Flexible");
//            }
//            
//            // Save to database
//            salesRepository.save(sale);
//        }
//    }
//
//    public void processPurchaseFile(MultipartFile file) throws IOException {
//        List<Purchase> purchaseList = excelHelper.parsePurchaseExcel(file.getInputStream());
//        
//        // Process and save each purchase record
//        for (Purchase purchase : purchaseList) {
//            // Determine plan type from description
//            if (purchase.getDescription() != null && purchase.getDescription().toLowerCase().contains("commitment")) {
//                purchase.setPlanType("Commitment");
//            } else {
//                purchase.setPlanType("Flexible");
//            }
//            
//            // Calculate normalized monthly price (as purchases are typically monthly)
//            purchase.setNormalizedMonthlyPrice(purchase.getRatePu());
//            
//            // Save to database
//            purchaseRepository.save(purchase);
//        }
//    }
//
//    private Double calculateNormalizedPrice(Double price, String billingFrequency) {
//        if (price == null || billingFrequency == null) {
//            return price;
//        }
//        
//        // Convert price to monthly based on billing frequency
//        switch (billingFrequency.toLowerCase()) {
//            case "monthly":
//                return price;
//            case "quarterly":
//                return price / 3;
//            case "semi annual":
//            case "semiannual":
//                return price / 6;
//            case "annual":
//                return price / 12;
//            default:
//                return price;
//        }
//    }
//
// // Add this method to your FileProcessingService class
//    public void clearAllData() {
//        salesRepository.deleteAll();
//        purchaseRepository.deleteAll();
//        notificationRepository.deleteAll();
//    }
//}
package com.invoice.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.invoice.model.Purchase;
import com.invoice.model.Sales;
import com.invoice.repository.NotificationRepository;
import com.invoice.repository.PurchaseRepository;
import com.invoice.repository.SalesRepository;
import com.invoice.util.ExcelHelper;

@Service
public class FileProcessingService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ExcelHelper excelHelper;

    public int processSalesFile(MultipartFile file) throws IOException {
        List<Sales> salesList = excelHelper.parseSalesExcel(file.getInputStream());
        
        // Process and save each sales record
        for (Sales sale : salesList) {
            // Calculate normalized monthly price based on billing frequency
            sale.setNormalizedMonthlyPrice(calculateNormalizedPrice(sale.getPrice(), sale.getBillingFrequency()));
            
            // Set plan type if not already set
            if (sale.getPlanType() == null || sale.getPlanType().isEmpty()) {
                // Default to "Flexible" if not specified
                sale.setPlanType("Flexible");
            }
            
            // Save to database
            salesRepository.save(sale);
        }
        
        return salesList.size();
    }

    public int processPurchaseFile(MultipartFile file) throws IOException {
        List<Purchase> purchaseList = excelHelper.parsePurchaseExcel(file.getInputStream());
        
        // Process and save each purchase record
        for (Purchase purchase : purchaseList) {
            // Determine plan type from description
            if (purchase.getDescription() != null && purchase.getDescription().toLowerCase().contains("commitment")) {
                purchase.setPlanType("Commitment");
            } else {
                purchase.setPlanType("Flexible");
            }
            
            // Calculate normalized monthly price (as purchases are typically monthly)
            purchase.setNormalizedMonthlyPrice(purchase.getRatePu());
            
            // Save to database
            purchaseRepository.save(purchase);
        }
        
        return purchaseList.size();
    }

    private Double calculateNormalizedPrice(Double price, String billingFrequency) {
        if (price == null || billingFrequency == null) {
            return price;
        }
        
        // Convert price to monthly based on billing frequency
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
    
    public void clearAllData() {
        salesRepository.deleteAll();
        purchaseRepository.deleteAll();
        notificationRepository.deleteAll();
    }
}