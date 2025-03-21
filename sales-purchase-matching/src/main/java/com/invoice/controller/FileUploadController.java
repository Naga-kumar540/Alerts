package com.invoice.controller;

import com.invoice.service.ComparisonService;
import com.invoice.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileProcessingService fileProcessingService;
    
    @Autowired
    private ComparisonService comparisonService;

    @PostMapping("/upload/sales")
    public ResponseEntity<Map<String, Object>> uploadSalesFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // Validate file is Excel
            if (!isExcelFile(file)) {
                response.put("success", false);
                response.put("message", "Please upload an Excel file");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // Process the file
            int recordsUploaded = fileProcessingService.processSalesFile(file);
            
            // Run comparison after processing sales file
            System.out.println("Running comparison after sales file upload");
            comparisonService.compareAllSalesAndPurchases();
            
            response.put("success", true);
            response.put("message", "Sales file uploaded and processed successfully");
            response.put("recordsUploaded", recordsUploaded);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Add this to print detailed error in logs
            response.put("success", false);
            response.put("message", "Failed to upload sales file: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/upload/purchase")
    public ResponseEntity<Map<String, Object>> uploadPurchaseFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // Validate file is Excel
            if (!isExcelFile(file)) {
                response.put("success", false);
                response.put("message", "Please upload an Excel file");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // Process the file
            int recordsUploaded = fileProcessingService.processPurchaseFile(file);
            
            // Run comparison after processing purchase file
            System.out.println("Running comparison after purchase file upload");
            comparisonService.compareAllSalesAndPurchases();
            
            response.put("success", true);
            response.put("message", "Purchase file uploaded and processed successfully");
            response.put("recordsUploaded", recordsUploaded);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Add this to print detailed error in logs
            response.put("success", false);
            response.put("message", "Failed to upload purchase file: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearAllData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            fileProcessingService.clearAllData();
            response.put("success", true);
            response.put("message", "All data cleared successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Add this to print detailed error in logs
            response.put("success", false);
            response.put("message", "Failed to clear data: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private boolean isExcelFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && (filename.endsWith(".xlsx") || filename.endsWith(".xls"));
    }
}
