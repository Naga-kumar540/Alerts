//package com.invoice.controller;
//
//import com.invoice.model.ComparisonResult;
//import com.invoice.model.Notification;
//import com.invoice.repository.NotificationRepository;
//import com.invoice.service.ComparisonService;
//import com.invoice.service.NotificationService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/notifications")
//public class NotificationController {
//    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
//
//    @Autowired
//    private NotificationService notificationService;
//    
//    @Autowired
//    private NotificationRepository notificationRepository;
//    
//    @Autowired
//    private ComparisonService comparisonService;
//
//    /**
//     * Get all notifications with duplicate prevention
//     */
//    @GetMapping
//    public ResponseEntity<List<Notification>> getAllNotifications() {
//        try {
//            List<Notification> notifications = notificationService.getAllNotifications();
//            logger.info("Retrieved {} notifications", notifications.size());
//            return ResponseEntity.ok(notifications);
//        } catch (Exception e) {
//            logger.error("Error retrieving notifications", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Get new notifications with duplicate prevention
//     */
//    @GetMapping("/new")
//    public ResponseEntity<List<Notification>> getNewNotifications() {
//        try {
//            List<Notification> notifications = notificationService.getNewNotifications();
//            logger.info("Retrieved {} new notifications", notifications.size());
//            return ResponseEntity.ok(notifications);
//        } catch (Exception e) {
//            logger.error("Error retrieving new notifications", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Get notifications by type with duplicate prevention
//     */
//    @GetMapping("/type/{type}")
//    public ResponseEntity<List<Notification>> getNotificationsByType(@PathVariable String type) {
//        try {
//            List<Notification> notifications = notificationService.getNotificationsByType(type);
//            logger.info("Retrieved {} notifications of type {}", notifications.size(), type);
//            return ResponseEntity.ok(notifications);
//        } catch (Exception e) {
//            logger.error("Error retrieving notifications of type " + type, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Get overdue notifications with duplicate prevention
//     */
//    @GetMapping("/overdue")
//    public ResponseEntity<List<Notification>> getOverdueNotifications() {
//        try {
//            List<Notification> overdueNotifications = notificationRepository
//                .findByTypeAndStatusOrderByCreatedDateDesc("INVOICE_OVERDUE", "NEW")
//                .stream()
//                .collect(Collectors.collectingAndThen(
//                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(n -> 
//                        n.getDomainName() + n.getItemName()))), 
//                    ArrayList::new));
//            
//            logger.info("Retrieved {} unique overdue notifications", overdueNotifications.size());
//            return ResponseEntity.ok(overdueNotifications);
//        } catch (Exception e) {
//            logger.error("Failed to retrieve overdue notifications", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Get renewal notifications with duplicate prevention
//     */
//    @GetMapping("/renewals")
//    public ResponseEntity<List<Notification>> getRenewalNotifications() {
//        try {
//            List<Notification> renewalNotifications = notificationRepository
//                .findByTypeAndStatusOrderByCreatedDateDesc("RENEWAL", "NEW")
//                .stream()
//                .collect(Collectors.collectingAndThen(
//                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(n -> 
//                        n.getDomainName() + n.getItemName()))), 
//                    ArrayList::new));
//            
//            logger.info("Retrieved {} unique renewal notifications", renewalNotifications.size());
//            return ResponseEntity.ok(renewalNotifications);
//        } catch (Exception e) {
//            logger.error("Failed to retrieve renewal notifications", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Mark a specific notification as read
//     */
//    @PutMapping("/{id}/read")
//    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
//        Map<String, String> response = new HashMap<>();
//        try {
//            notificationService.markNotificationAsRead(id);
//            response.put("message", "Notification marked as read");
//            logger.info("Notification {} marked as read", id);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("Failed to mark notification {} as read", id, e);
//            response.put("message", "Failed to update notification: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Mark a specific notification as resolved
//     */
//    @PutMapping("/{id}/resolve")
//    public ResponseEntity<Map<String, String>> markAsResolved(@PathVariable Long id) {
//        Map<String, String> response = new HashMap<>();
//        try {
//            notificationService.markNotificationAsResolved(id);
//            response.put("message", "Notification marked as resolved");
//            logger.info("Notification {} marked as resolved", id);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("Failed to mark notification {} as resolved", id, e);
//            response.put("message", "Failed to update notification: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Check and retrieve renewal notifications
//     */
//    @GetMapping("/renewals/check")
//    public ResponseEntity<Map<String, Object>> checkRenewals() {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            // Get unique renewal notifications
//            List<Notification> notifications = comparisonService.checkAllRenewals().stream()
//                .collect(Collectors.collectingAndThen(
//                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(n -> 
//                        n.getDomainName() + n.getItemName()))), 
//                    ArrayList::new));
//            
//            response.put("success", true);
//            response.put("message", "Renewal check completed");
//            response.put("notificationsGenerated", notifications.size());
//            response.put("notifications", notifications);
//            
//            logger.info("Generated {} unique renewal notifications", notifications.size());
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("Failed to check renewals", e);
//            response.put("success", false);
//            response.put("message", "Failed to check renewals: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Retrieve invoice notifications with duplicate prevention
//     */
//    @GetMapping("/invoices")
//    public ResponseEntity<List<Notification>> getInvoiceNotifications() {
//        try {
//            List<String> invoiceTypes = Arrays.asList("INVOICE_NEEDED", "INVOICE_OVERDUE");
//            
//            // Get unique invoice notifications
//            List<Notification> notifications = notificationRepository
//                .findByTypeInAndStatusOrderByCreatedDateDesc(invoiceTypes, "NEW")
//                .stream()
//                .collect(Collectors.collectingAndThen(
//                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(n -> 
//                        n.getDomainName() + n.getItemName()))), 
//                    ArrayList::new));
//            
//            logger.info("Retrieved {} unique invoice notifications", notifications.size());
//            return ResponseEntity.ok(notifications);
//        } catch (Exception e) {
//            logger.error("Failed to retrieve invoice notifications", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Create an invoice notification for a specific renewal
//     */
//    @PostMapping("/create-invoice/{renewalId}")
//    public ResponseEntity<Map<String, Object>> createInvoiceForRenewal(@PathVariable Long renewalId) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            Notification invoiceNotification = notificationService.createInvoiceNotificationForRenewal(renewalId);
//            
//            if (invoiceNotification != null) {
//                response.put("success", true);
//                response.put("message", "Invoice notification created successfully");
//                response.put("notification", invoiceNotification);
//                logger.info("Created invoice notification for renewal {}", renewalId);
//                return ResponseEntity.ok(response);
//            } else {
//                response.put("success", false);
//                response.put("message", "Failed to create invoice notification. Either renewal not found or invoice notification already exists.");
//                logger.warn("Failed to create invoice notification for renewal {}", renewalId);
//                return ResponseEntity.badRequest().body(response);
//            }
//        } catch (Exception e) {
//            logger.error("Error creating invoice notification for renewal " + renewalId, e);
//            response.put("success", false);
//            response.put("message", "Error creating invoice notification: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Check for overdue invoices
//     */
//    @GetMapping("/check-overdue-invoices")
//    public ResponseEntity<Map<String, Object>> checkOverdueInvoices() {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            List<Notification> overdueNotifications = notificationService.checkAllOverdueItems();
//            
//            // Remove duplicates
//            overdueNotifications = overdueNotifications.stream()
//                .collect(Collectors.collectingAndThen(
//                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(n -> 
//                        n.getDomainName() + n.getItemName()))), 
//                    ArrayList::new));
//            
//            response.put("success", true);
//            response.put("message", "Overdue invoice check completed");
//            response.put("notificationsGenerated", overdueNotifications.size());
//            response.put("notifications", overdueNotifications);
//            
//            logger.info("Generated {} unique overdue notifications", overdueNotifications.size());
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("Failed to check overdue invoices", e);
//            response.put("success", false);
//            response.put("message", "Failed to check overdue invoices: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * Debug endpoint to fetch notifications
//     */
//    @GetMapping("/debug")
//    public ResponseEntity<Map<String, Object>> debugNotifications() {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            List<Notification> allNotifications = notificationService.getAllNotifications();
//            
//            response.put("success", true);
//            response.put("count", allNotifications.size());
//            response.put("notifications", allNotifications);
//            
//            logger.info("Debug: Retrieved {} notifications", allNotifications.size());
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("Error in debug notifications endpoint", e);
//            response.put("success", false);
//            response.put("message", "Error fetching notifications: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//}

package com.invoice.controller;

import com.invoice.model.Notification;
import com.invoice.service.ComparisonService;
import com.invoice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.invoice.model.Sales;
import com.invoice.repository.SalesRepository;
import com.invoice.repository.NotificationRepository;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private SalesRepository salesRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private ComparisonService comparisonService;

    /**
     * Get all notifications with duplicate prevention
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        try {
            List<Notification> notifications = notificationService.getAllNotifications();
            logger.info("Retrieved {} notifications", notifications.size());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error retrieving notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get new notifications with duplicate prevention
     */
    @GetMapping("/new")
    public ResponseEntity<List<Notification>> getNewNotifications() {
        try {
            List<Notification> notifications = notificationService.getNewNotifications();
            logger.info("Retrieved {} new notifications", notifications.size());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error retrieving new notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get notifications by type with duplicate prevention
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(@PathVariable String type) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByType(type);
            logger.info("Retrieved {} notifications of type {}", notifications.size(), type);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error retrieving notifications of type " + type, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get overdue notifications with duplicate prevention
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<Notification>> getOverdueNotifications() {
        try {
            List<Notification> overdueNotifications = notificationService.getOverdueNotifications();
            logger.info("Retrieved {} unique overdue notifications", overdueNotifications.size());
            return ResponseEntity.ok(overdueNotifications);
        } catch (Exception e) {
            logger.error("Failed to retrieve overdue notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get renewal notifications with duplicate prevention
     */
    @GetMapping("/renewals")
    public ResponseEntity<List<Notification>> getRenewalNotifications() {
        try {
            List<Notification> renewalNotifications = notificationService.getRenewalNotifications();
            logger.info("Retrieved {} unique renewal notifications", renewalNotifications.size());
            return ResponseEntity.ok(renewalNotifications);
        } catch (Exception e) {
            logger.error("Failed to retrieve renewal notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Mark a specific notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            notificationService.markNotificationAsRead(id);
            response.put("message", "Notification marked as read");
            logger.info("Notification {} marked as read", id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Notification {} not found", id);
            response.put("message", "Notification not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Failed to mark notification {} as read", id, e);
            response.put("message", "Failed to update notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Mark a specific notification as resolved
     */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<Map<String, String>> markAsResolved(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            notificationService.markNotificationAsResolved(id);
            response.put("message", "Notification marked as resolved");
            logger.info("Notification {} marked as resolved", id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Notification {} not found", id);
            response.put("message", "Notification not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Failed to mark notification {} as resolved", id, e);
            response.put("message", "Failed to update notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check and retrieve renewal notifications
     */
    @GetMapping("/renewals/check")
    public ResponseEntity<Map<String, Object>> checkRenewals() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Force check all renewals through comparison service
            List<Notification> notifications = comparisonService.checkAllRenewals();
            
            response.put("success", true);
            response.put("message", "Renewal check completed");
            response.put("notificationsGenerated", notifications.size());
            response.put("notifications", notifications);
            
            logger.info("Generated {} renewal notifications from manual check", notifications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to check renewals", e);
            response.put("success", false);
            response.put("message", "Failed to check renewals: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Retrieve invoice notifications with duplicate prevention
     */
    @GetMapping("/invoices")
    public ResponseEntity<List<Notification>> getInvoiceNotifications() {
        try {
            List<Notification> notifications = notificationService.getInvoiceNotifications();
            logger.info("Retrieved {} unique invoice notifications", notifications.size());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Failed to retrieve invoice notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create an invoice notification for a specific renewal
     */
    @PostMapping("/create-invoice/{renewalId}")
    public ResponseEntity<Map<String, Object>> createInvoiceForRenewal(@PathVariable Long renewalId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notification invoiceNotification = notificationService.createInvoiceNotificationForRenewal(renewalId);
            
            response.put("success", true);
            response.put("message", "Invoice notification created successfully");
            response.put("notification", invoiceNotification);
            logger.info("Created invoice notification for renewal {}", renewalId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error creating invoice notification for renewal " + renewalId, e);
            response.put("success", false);
            response.put("message", "Error creating invoice notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check for overdue invoices
     */
    @GetMapping("/check-overdue-invoices")
    public ResponseEntity<Map<String, Object>> checkOverdueInvoices() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Notification> overdueNotifications = notificationService.checkAllOverdueItems();
            
            response.put("success", true);
            response.put("message", "Overdue invoice check completed");
            response.put("notificationsGenerated", overdueNotifications.size());
            response.put("notifications", overdueNotifications);
            
            logger.info("Generated {} unique overdue notifications from manual check", overdueNotifications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to check overdue invoices", e);
            response.put("success", false);
            response.put("message", "Failed to check overdue invoices: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    /**
     * Check for invoice notifications related to renewals - NEW ENDPOINT
     */
    @GetMapping("/check-invoice-notifications")
    public ResponseEntity<Map<String, Object>> checkInvoiceNotifications() {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDate today = LocalDate.now();
            logger.info("Manually triggering invoice notifications check on {}", today);
            
            List<Notification> notifications = new ArrayList<>();
            
            // Find all active renewal notifications
            List<Notification> renewalNotifications = notificationService.getRenewalNotifications();
            
            for (Notification renewalNotification : renewalNotifications) {
                // If the renewal is due soon, create an invoice reminder
                if (renewalNotification.getDueDate() != null && 
                    (renewalNotification.getDueDate().isEqual(today) || 
                     renewalNotification.getDueDate().isEqual(today.plusDays(1)) || 
                     renewalNotification.getDueDate().isEqual(today.plusDays(2)))) {
                    
                    try {
                        Notification invoiceNotification = notificationService.createInvoiceNotificationForRenewal(
                                renewalNotification.getId());
                        
                        // Only add to list if it's a newly created notification
                        if (invoiceNotification != null && invoiceNotification.getCreatedDate().equals(today)) {
                            notifications.add(invoiceNotification);
                        }
                    } catch (Exception e) {
                        logger.warn("Error creating invoice notification for renewal {}: {}", 
                                renewalNotification.getId(), e.getMessage());
                    }
                }
            }
            
            response.put("success", true);
            response.put("message", "Invoice notification check completed");
            response.put("notificationsGenerated", notifications.size());
            response.put("notifications", notifications);
            
            logger.info("Generated {} invoice notifications from manual check", notifications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to check for invoice notifications", e);
            response.put("success", false);
            response.put("message", "Failed to check for invoice notifications: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Debug endpoint to examine sales with past due dates
     */
    @GetMapping("/debug-overdue-sales")
    public ResponseEntity<Map<String, Object>> debugOverdueSales() {
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDate today = LocalDate.now();
            
            // Get all sales
            List<Sales> allSales = salesRepository.findAll();
            
            // Filter to find those with end dates before today
            List<Sales> overdueSales = new ArrayList<>();
            for (Sales sale : allSales) {
                if (sale.getEndDate() != null && sale.getEndDate().isBefore(today)) {
                    overdueSales.add(sale);
                }
            }
            
            // Get existing overdue notifications
            List<Notification> existingOverdueNotifications = notificationRepository
                .findByTypeAndStatusOrderByCreatedDateDesc("INVOICE_OVERDUE", "NEW");
            
            response.put("success", true);
            response.put("currentDate", today.toString());
            response.put("totalSalesCount", allSales.size());
            response.put("overdueSalesCount", overdueSales.size());
            response.put("overdueSales", overdueSales);
            response.put("existingOverdueNotifications", existingOverdueNotifications);
            response.put("existingOverdueNotificationsCount", existingOverdueNotifications.size());
            
            logger.info("Debug: Found {} sales with {} overdue, and {} existing overdue notifications", 
                    allSales.size(), overdueSales.size(), existingOverdueNotifications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in debug overdue sales endpoint", e);
            response.put("success", false);
            response.put("message", "Error fetching overdue sales data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    /**
     * Get notifications by domain
     */
    @GetMapping("/domain/{domainName}")
    public ResponseEntity<List<Notification>> getNotificationsByDomain(@PathVariable String domainName) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByDomain(domainName);
            logger.info("Retrieved {} notifications for domain {}", notifications.size(), domainName);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error retrieving notifications for domain " + domainName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Debug endpoint to fetch notifications
     */
    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debugNotifications() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Notification> allNotifications = notificationService.getAllNotifications();
            
            response.put("success", true);
            response.put("count", allNotifications.size());
            response.put("notifications", allNotifications);
            
            logger.info("Debug: Retrieved {} notifications", allNotifications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in debug notifications endpoint", e);
            response.put("success", false);
            response.put("message", "Error fetching notifications: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
