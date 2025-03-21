//// STEP 5: Utility classes
//// ExcelHelper.java
//package com.invoice.util;
//
//import com.invoice.model.Purchase;
//import com.invoice.model.Sales;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//@Component
//public class ExcelHelper {
//
//    public List<Sales> parseSalesExcel(InputStream is) throws IOException {
//        List<Sales> salesList = new ArrayList<>();
//        
//        Workbook workbook = null;
//        try {
//            // Try to open as XLSX first
//            try {
//                workbook = new XSSFWorkbook(is);
//            } catch (Exception e) {
//                // If fails, try as XLS
//                is.reset(); // Reset the stream
//                workbook = new HSSFWorkbook(is);
//            }
//            
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rows = sheet.iterator();
//            
//            // Skip header row
//            if (rows.hasNext()) {
//                rows.next();
//            }
//            
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//                Sales sales = new Sales();
//                
//                // Populate from Excel rows - handling different cell types
//                // Invoice Date
//                Cell dateCell = currentRow.getCell(0);
//                if (dateCell != null) {
//                    if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
//                        sales.setInvoiceDate(dateCell.getDateCellValue().toInstant()
//                                .atZone(ZoneId.systemDefault()).toLocalDate());
//                    }
//                }
//                
//                // Invoice Number
//                Cell numberCell = currentRow.getCell(1);
//                if (numberCell != null) {
//                    if (numberCell.getCellType() == CellType.NUMERIC) {
//                        sales.setInvoiceNumber(String.valueOf((long)numberCell.getNumericCellValue()));
//                    } else {
//                        sales.setInvoiceNumber(numberCell.getStringCellValue());
//                    }
//                }
//                
//                // Invoice Status
//                Cell statusCell = currentRow.getCell(2);
//                if (statusCell != null) {
//                    sales.setInvoiceStatus(statusCell.getStringCellValue());
//                }
//                
//                // Customer Name
//                Cell customerCell = currentRow.getCell(3);
//                if (customerCell != null) {
//                    sales.setCustomerName(customerCell.getStringCellValue());
//                }
//                
//                // GST Treatment
//                Cell gstCell = currentRow.getCell(4);
//                if (gstCell != null) {
//                    sales.setGstTreatment(gstCell.getStringCellValue());
//                }
//                
//                // Due Date
//                Cell dueDateCell = currentRow.getCell(5);
//                if (dueDateCell != null && dueDateCell.getCellType() == CellType.NUMERIC && 
//                    DateUtil.isCellDateFormatted(dueDateCell)) {
//                    sales.setDueDate(dueDateCell.getDateCellValue().toInstant()
//                            .atZone(ZoneId.systemDefault()).toLocalDate());
//                }
//                
//                // Invoice Value
//                Cell valueCell = currentRow.getCell(6);
//                if (valueCell != null && valueCell.getCellType() == CellType.NUMERIC) {
//                    sales.setInvoiceValue(valueCell.getNumericCellValue());
//                }
//                
//                // Due Amount
//                Cell dueAmtCell = currentRow.getCell(7);
//                if (dueAmtCell != null && dueAmtCell.getCellType() == CellType.NUMERIC) {
//                    sales.setDueAmount(dueAmtCell.getNumericCellValue());
//                }
//                
//                // Item Name
//                Cell itemCell = currentRow.getCell(8);
//                if (itemCell != null) {
//                    sales.setItemName(itemCell.getStringCellValue());
//                }
//                
//                // Domain Name
//                Cell domainCell = currentRow.getCell(9);
//                if (domainCell != null) {
//                    sales.setDomainName(domainCell.getStringCellValue());
//                }
//                
//                // Quantity
//                Cell qtyCell = currentRow.getCell(10);
//                if (qtyCell != null && qtyCell.getCellType() == CellType.NUMERIC) {
//                    sales.setQuantity((int)qtyCell.getNumericCellValue());
//                }
//                
//                // Price
//                Cell priceCell = currentRow.getCell(11);
//                if (priceCell != null && priceCell.getCellType() == CellType.NUMERIC) {
//                    sales.setPrice(priceCell.getNumericCellValue());
//                }
//                
//                // Start Date
//                Cell startDateCell = currentRow.getCell(12);
//                if (startDateCell != null && startDateCell.getCellType() == CellType.NUMERIC && 
//                    DateUtil.isCellDateFormatted(startDateCell)) {
//                    sales.setStartDate(startDateCell.getDateCellValue().toInstant()
//                            .atZone(ZoneId.systemDefault()).toLocalDate());
//                }
//                
//                // End Date
//                Cell endDateCell = currentRow.getCell(13);
//                if (endDateCell != null && endDateCell.getCellType() == CellType.NUMERIC && 
//                    DateUtil.isCellDateFormatted(endDateCell)) {
//                    sales.setEndDate(endDateCell.getDateCellValue().toInstant()
//                            .atZone(ZoneId.systemDefault()).toLocalDate());
//                }
//                
//                // Billing Frequency
//                Cell freqCell = currentRow.getCell(14);
//                if (freqCell != null) {
//                    sales.setBillingFrequency(freqCell.getStringCellValue());
//                }
//                
//                // Add to list if it has required fields
//                if (sales.getItemName() != null && sales.getDomainName() != null) {
//                    salesList.add(sales);
//                }
//            }
//            
//            return salesList;
//        } finally {
//            if (workbook != null) {
//                workbook.close();
//            }
//            if (is != null) {
//                is.close();
//            }
//        }
//    }
//    
//    public List<Purchase> parsePurchaseExcel(InputStream is) throws IOException {
//        List<Purchase> purchaseList = new ArrayList<>();
//        
//        Workbook workbook = null;
//        try {
//            // Try to open as XLSX first
//            try {
//                workbook = new XSSFWorkbook(is);
//            } catch (Exception e) {
//                // If fails, try as XLS
//                is.reset(); // Reset the stream
//                workbook = new HSSFWorkbook(is);
//            }
//            
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rows = sheet.iterator();
//            
//            // Skip header row
//            if (rows.hasNext()) {
//                rows.next();
//            }
//            
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//                Purchase purchase = new Purchase();
//                
//                // Console
//                Cell consoleCell = currentRow.getCell(0);
//                if (consoleCell != null) {
//                    purchase.setConsole(consoleCell.getStringCellValue());
//                }
//                
//                // Domain Name
//                Cell domainCell = currentRow.getCell(1);
//                if (domainCell != null) {
//                    purchase.setDomainName(domainCell.getStringCellValue());
//                }
//                
//                // Subscription
//                Cell subscriptionCell = currentRow.getCell(2);
//                if (subscriptionCell != null) {
//                    purchase.setSubscription(subscriptionCell.getStringCellValue());
//                }
//                
//                // Description
//                Cell descCell = currentRow.getCell(3);
//                if (descCell != null) {
//                    purchase.setDescription(descCell.getStringCellValue());
//                }
//                
//                // Order Name
//                Cell orderCell = currentRow.getCell(4);
//                if (orderCell != null) {
//                    purchase.setOrderName(orderCell.getStringCellValue());
//                }
//                
//                // Start Date
//                Cell startDateCell = currentRow.getCell(5);
//                if (startDateCell != null && startDateCell.getCellType() == CellType.NUMERIC && 
//                    DateUtil.isCellDateFormatted(startDateCell)) {
//                    purchase.setStartDate(startDateCell.getDateCellValue().toInstant()
//                            .atZone(ZoneId.systemDefault()).toLocalDate());
//                }
//                
//                // End Date
//                Cell endDateCell = currentRow.getCell(6);
//                if (endDateCell != null && endDateCell.getCellType() == CellType.NUMERIC && 
//                    DateUtil.isCellDateFormatted(endDateCell)) {
//                    purchase.setEndDate(endDateCell.getDateCellValue().toInstant()
//                            .atZone(ZoneId.systemDefault()).toLocalDate());
//                }
//                
//                // Quantity
//                Cell qtyCell = currentRow.getCell(7);
//                if (qtyCell != null && qtyCell.getCellType() == CellType.NUMERIC) {
//                    purchase.setQuantity((int)qtyCell.getNumericCellValue());
//                }
//                
//                // PO Number
//                Cell poCell = currentRow.getCell(8);
//                if (poCell != null) {
//                    if (poCell.getCellType() == CellType.NUMERIC) {
//                        purchase.setPoNumber(String.valueOf((long)poCell.getNumericCellValue()));
//                    } else if (poCell.getCellType() == CellType.STRING) {
//                        purchase.setPoNumber(poCell.getStringCellValue());
//                    }
//                }
//                
//                // Amount
//                Cell amountCell = currentRow.getCell(9);
//                if (amountCell != null && amountCell.getCellType() == CellType.NUMERIC) {
//                    purchase.setAmount(amountCell.getNumericCellValue());
//                }
//                
//                // Customer ID
//                Cell customerIdCell = currentRow.getCell(10);
//                if (customerIdCell != null) {
//                    purchase.setCustomerId(customerIdCell.getStringCellValue());
//                }
//                
//                // SKU ID
//                Cell skuCell = currentRow.getCell(11);
//                if (skuCell != null) {
//                    if (skuCell.getCellType() == CellType.NUMERIC) {
//                        purchase.setSkuId(String.valueOf((long)skuCell.getNumericCellValue()));
//                    } else if (skuCell.getCellType() == CellType.STRING) {
//                        purchase.setSkuId(skuCell.getStringCellValue());
//                    }
//                }
//                
//                // Rate PU
//                Cell rateCell = currentRow.getCell(12);
//                if (rateCell != null && rateCell.getCellType() == CellType.NUMERIC) {
//                    purchase.setRatePu(rateCell.getNumericCellValue());
//                }
//                
//                // Add to list if it has required fields
//                if (purchase.getDomainName() != null && purchase.getSubscription() != null) {
//                    purchaseList.add(purchase);
//                }
//            }
//            
//            return purchaseList;
//        } finally {
//            if (workbook != null) {
//                workbook.close();
//            }
//            if (is != null) {
//                is.close();
//            }
//        }
//    }
//}
package com.invoice.util;

import com.invoice.model.Purchase;
import com.invoice.model.Sales;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelHelper {

    public List<Sales> parseSalesExcel(InputStream is) throws IOException {
        List<Sales> salesList = new ArrayList<>();
        
        // Convert the stream to a byte array to allow for multiple reads
        byte[] bytes = is.readAllBytes();
        
        Workbook workbook = null;
        try {
            // Try to open as XLSX first
            try {
                InputStream xlsxStream = new ByteArrayInputStream(bytes);
                workbook = new XSSFWorkbook(xlsxStream);
            } catch (Exception e) {
                // If fails, try as XLS
                InputStream xlsStream = new ByteArrayInputStream(bytes);
                workbook = new HSSFWorkbook(xlsStream);
            }
            
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }
            
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Sales sales = new Sales();
                
                // Populate from Excel rows - handling different cell types
                // Invoice Date
                Cell dateCell = currentRow.getCell(0);
                if (dateCell != null) {
                    if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                        sales.setInvoiceDate(dateCell.getDateCellValue().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate());
                    }
                }
                
                // Invoice Number
                Cell numberCell = currentRow.getCell(1);
                if (numberCell != null) {
                    if (numberCell.getCellType() == CellType.NUMERIC) {
                        sales.setInvoiceNumber(String.valueOf((long)numberCell.getNumericCellValue()));
                    } else {
                        sales.setInvoiceNumber(numberCell.getStringCellValue());
                    }
                }
                
                // Invoice Status
                Cell statusCell = currentRow.getCell(2);
                if (statusCell != null) {
                    sales.setInvoiceStatus(statusCell.getStringCellValue());
                }
                
                // Customer Name
                Cell customerCell = currentRow.getCell(3);
                if (customerCell != null) {
                    sales.setCustomerName(customerCell.getStringCellValue());
                }
                
                // GST Treatment
                Cell gstCell = currentRow.getCell(4);
                if (gstCell != null) {
                    sales.setGstTreatment(gstCell.getStringCellValue());
                }
                
                // Due Date
                Cell dueDateCell = currentRow.getCell(5);
                if (dueDateCell != null && dueDateCell.getCellType() == CellType.NUMERIC && 
                    DateUtil.isCellDateFormatted(dueDateCell)) {
                    sales.setDueDate(dueDateCell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate());
                }
                
                // Invoice Value
                Cell valueCell = currentRow.getCell(6);
                if (valueCell != null && valueCell.getCellType() == CellType.NUMERIC) {
                    sales.setInvoiceValue(valueCell.getNumericCellValue());
                }
                
                // Due Amount
                Cell dueAmtCell = currentRow.getCell(7);
                if (dueAmtCell != null && dueAmtCell.getCellType() == CellType.NUMERIC) {
                    sales.setDueAmount(dueAmtCell.getNumericCellValue());
                }
                
                // Item Name
                Cell itemCell = currentRow.getCell(8);
                if (itemCell != null) {
                    sales.setItemName(itemCell.getStringCellValue());
                }
                
                // Domain Name
                Cell domainCell = currentRow.getCell(9);
                if (domainCell != null) {
                    sales.setDomainName(domainCell.getStringCellValue());
                }
                
                // Quantity
                Cell qtyCell = currentRow.getCell(10);
                if (qtyCell != null && qtyCell.getCellType() == CellType.NUMERIC) {
                    sales.setQuantity((int)qtyCell.getNumericCellValue());
                }
                
                // Price
                Cell priceCell = currentRow.getCell(11);
                if (priceCell != null && priceCell.getCellType() == CellType.NUMERIC) {
                    sales.setPrice(priceCell.getNumericCellValue());
                }
                
                // Start Date
                Cell startDateCell = currentRow.getCell(12);
                if (startDateCell != null && startDateCell.getCellType() == CellType.NUMERIC && 
                    DateUtil.isCellDateFormatted(startDateCell)) {
                    sales.setStartDate(startDateCell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate());
                }
                
                // End Date
                Cell endDateCell = currentRow.getCell(13);
                if (endDateCell != null && endDateCell.getCellType() == CellType.NUMERIC && 
                    DateUtil.isCellDateFormatted(endDateCell)) {
                    sales.setEndDate(endDateCell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate());
                }
                
                // Billing Frequency
                Cell freqCell = currentRow.getCell(14);
                if (freqCell != null) {
                    sales.setBillingFrequency(freqCell.getStringCellValue());
                }
                
                // Add to list if it has required fields
                if (sales.getItemName() != null && sales.getDomainName() != null) {
                    salesList.add(sales);
                }
            }
            
            return salesList;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }
    
    public List<Purchase> parsePurchaseExcel(InputStream is) throws IOException {
        List<Purchase> purchaseList = new ArrayList<>();
        
        // Convert the stream to a byte array to allow for multiple reads
        byte[] bytes = is.readAllBytes();
        
        Workbook workbook = null;
        try {
            // Try to open as XLSX first
            try {
                InputStream xlsxStream = new ByteArrayInputStream(bytes);
                workbook = new XSSFWorkbook(xlsxStream);
            } catch (Exception e) {
                // If fails, try as XLS
                InputStream xlsStream = new ByteArrayInputStream(bytes);
                workbook = new HSSFWorkbook(xlsStream);
            }
            
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }
            
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Purchase purchase = new Purchase();
                
                // Console
                Cell consoleCell = currentRow.getCell(0);
                if (consoleCell != null) {
                    purchase.setConsole(consoleCell.getStringCellValue());
                }
                
                // Domain Name
                Cell domainCell = currentRow.getCell(1);
                if (domainCell != null) {
                    purchase.setDomainName(domainCell.getStringCellValue());
                }
                
                // Subscription
                Cell subscriptionCell = currentRow.getCell(2);
                if (subscriptionCell != null) {
                    purchase.setSubscription(subscriptionCell.getStringCellValue());
                }
                
                // Description
                Cell descCell = currentRow.getCell(3);
                if (descCell != null) {
                    purchase.setDescription(descCell.getStringCellValue());
                }
                
                // Order Name
                Cell orderCell = currentRow.getCell(4);
                if (orderCell != null) {
                    purchase.setOrderName(orderCell.getStringCellValue());
                }
                
                // Start Date
                Cell startDateCell = currentRow.getCell(5);
                if (startDateCell != null && startDateCell.getCellType() == CellType.NUMERIC && 
                    DateUtil.isCellDateFormatted(startDateCell)) {
                    purchase.setStartDate(startDateCell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate());
                }
                
                // End Date
                Cell endDateCell = currentRow.getCell(6);
                if (endDateCell != null && endDateCell.getCellType() == CellType.NUMERIC && 
                    DateUtil.isCellDateFormatted(endDateCell)) {
                    purchase.setEndDate(endDateCell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate());
                }
                
                // Quantity
                Cell qtyCell = currentRow.getCell(7);
                if (qtyCell != null && qtyCell.getCellType() == CellType.NUMERIC) {
                    purchase.setQuantity((int)qtyCell.getNumericCellValue());
                }
                
                // PO Number
                Cell poCell = currentRow.getCell(8);
                if (poCell != null) {
                    if (poCell.getCellType() == CellType.NUMERIC) {
                        purchase.setPoNumber(String.valueOf((long)poCell.getNumericCellValue()));
                    } else if (poCell.getCellType() == CellType.STRING) {
                        purchase.setPoNumber(poCell.getStringCellValue());
                    }
                }
                
                // Amount
                Cell amountCell = currentRow.getCell(9);
                if (amountCell != null && amountCell.getCellType() == CellType.NUMERIC) {
                    purchase.setAmount(amountCell.getNumericCellValue());
                }
                
                // Customer ID
                Cell customerIdCell = currentRow.getCell(10);
                if (customerIdCell != null) {
                    purchase.setCustomerId(customerIdCell.getStringCellValue());
                }
                
                // SKU ID
                Cell skuCell = currentRow.getCell(11);
                if (skuCell != null) {
                    if (skuCell.getCellType() == CellType.NUMERIC) {
                        purchase.setSkuId(String.valueOf((long)skuCell.getNumericCellValue()));
                    } else if (skuCell.getCellType() == CellType.STRING) {
                        purchase.setSkuId(skuCell.getStringCellValue());
                    }
                }
                
                // Rate PU
                Cell rateCell = currentRow.getCell(12);
                if (rateCell != null && rateCell.getCellType() == CellType.NUMERIC) {
                    purchase.setRatePu(rateCell.getNumericCellValue());
                }
                
                // Add to list if it has required fields
                if (purchase.getDomainName() != null && purchase.getSubscription() != null) {
                    purchaseList.add(purchase);
                }
            }
            
            return purchaseList;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }
}	