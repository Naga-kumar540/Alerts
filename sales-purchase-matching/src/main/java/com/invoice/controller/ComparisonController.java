// ComparisonController.java
package com.invoice.controller;

import com.invoice.model.ComparisonResult;
import com.invoice.service.ComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comparison")
public class ComparisonController {

    @Autowired
    private ComparisonService comparisonService;

    @GetMapping
    public ResponseEntity<ComparisonResult> compareAllSalesAndPurchases() {
        ComparisonResult result = comparisonService.compareAllSalesAndPurchases();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
