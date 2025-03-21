//package com.invoice.service;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import com.invoice.model.Notification;
//import com.invoice.model.Sales;
//import com.invoice.repository.NotificationRepository;
//import com.invoice.repository.SalesRepository;
//
//@Service
//public class NotificationService {
//    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
//
//    @Autowired
//    private NotificationRepository notificationRepository;
//    
//    @Autowired
//    private SalesRepository salesRepository;
//
//    // Method to get unique notifications
//    public List<Notification> getAllNotifications() {
//        List<Notification> allNotifications = notificationRepository.findAll();
//        return removeDuplicateNotifications(allNotifications);
//    }
//    
//    // Remove duplicate notifications
//    private List<Notification> removeDuplicateNotifications(List<Notification> notifications) {
//        return notifications.stream()
//            .collect(Collectors.collectingAndThen(
//                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(n -> 
//                    n.getDomainName() + n.getItemName() + n.getType() + n.getStatus()))), 
//                ArrayList::new));
//    }
//    
//    // Save notification with duplicate prevention
//    public void saveNotification(Notification notification) {
//        // Check for existing unique notification
//        List<Notification> existingNotifications = notificationRepository.findUniqueNotifications(
//            notification.getType(), 
//            notification.getDomainName(), 
//            notification.getItemName(), 
//            notification.getStatus()
//        );
//        
//        if (existingNotifications.isEmpty()) {
//            notificationRepository.save(notification);
//        }
//    }
//    
//    // Get new notifications without duplicates
//    public List<Notification> getNewNotifications() {
//        List<Notification> newNotifications = notificationRepository.findByStatusOrderByDueDateAsc("NEW");
//        return removeDuplicateNotifications(newNotifications);
//    }
//    
//    // Get notifications by type without duplicates
//    public List<Notification> getNotificationsByType(String type) {
//        List<Notification> typeNotifications = notificationRepository.findByTypeAndStatusOrderByCreatedDateDesc(type, "NEW");
//        return removeDuplicateNotifications(typeNotifications);
//    }
//    
//    // Mark notification as read
//    public void markNotificationAsRead(Long id) {
//        Notification notification = notificationRepository.findById(id).orElse(null);
//        if (notification != null) {
//            notification.setStatus("READ");
//            notificationRepository.save(notification);
//        }
//    }
//    
//    // Mark notification as resolved
//    public void markNotificationAsResolved(Long id) {
//        Notification notification = notificationRepository.findById(id).orElse(null);
//        if (notification != null) {
//            notification.setStatus("RESOLVED");
//            notificationRepository.save(notification);
//        }
//    }
//    
//    // Check overdue invoices
//    @Scheduled(cron = "0 0 10 * * ?")  // Run at 10:00 AM every day
//    public void checkOverdueInvoices() {
//        LocalDate today = LocalDate.now();
//        Set<Notification> uniqueOverdueNotifications = new HashSet<>();
//        
//        // Find all sales items
//        List<Sales> allSales = salesRepository.findAll();
//        
//        for (Sales sale : allSales) {
//            // Check if the end date is before or equal to today
//            if (sale.getEndDate().isBefore(today) || sale.getEndDate().isEqual(today)) {
//                // Check for existing unique overdue notifications across statuses
//                List<Notification> existingOverdueNotifications = notificationRepository
//                    .findUniqueNotificationsAcrossStatuses(
//                        sale.getDomainName(), 
//                        sale.getItemName(), 
//                        "INVOICE_OVERDUE"
//                    );
//                
//                // If no existing overdue notification
//                if (existingOverdueNotifications.isEmpty()) {
//                    Notification overdueNotification = createOverdueNotification(
//                        sale.getDomainName(), 
//                        sale.getItemName(), 
//                        String.format("URGENT: Invoice for %s on %s is now OVERDUE", 
//                            sale.getItemName(), 
//                            sale.getDomainName()
//                        ), 
//                        sale.getEndDate(), 
//                        today
//                    );
//                    
//                    uniqueOverdueNotifications.add(overdueNotification);
//                }
//            }
//        }
//        
//        // Check invoice notifications past due
//        List<Notification> invoiceNotifications = notificationRepository
//            .findByTypeAndDueDateBefore("INVOICE_NEEDED", today);
//        
//        for (Notification invoiceNotification : invoiceNotifications) {
//            if ("NEW".equals(invoiceNotification.getStatus()) || 
//                "READ".equals(invoiceNotification.getStatus())) {
//                
//                // Check for existing unique overdue notification
//                List<Notification> existingOverdueNotifications = notificationRepository
//                    .findUniqueNotificationsAcrossStatuses(
//                        invoiceNotification.getDomainName(), 
//                        invoiceNotification.getItemName(), 
//                        "INVOICE_OVERDUE"
//                    );
//                
//                if (existingOverdueNotifications.isEmpty()) {
//                    Notification overdueNotification = createOverdueNotification(
//                        invoiceNotification.getDomainName(), 
//                        invoiceNotification.getItemName(), 
//                        invoiceNotification.getMessage(), 
//                        invoiceNotification.getDueDate(), 
//                        today
//                    );
//                    
//                    uniqueOverdueNotifications.add(overdueNotification);
//                }
//            }
//        }
//        
//        // Save unique overdue notifications
//        if (!uniqueOverdueNotifications.isEmpty()) {
//            notificationRepository.saveAll(uniqueOverdueNotifications);
//            logger.info("Generated {} unique overdue notifications", uniqueOverdueNotifications.size());
//        }
//    }
//    
//    // Check upcoming renewals
//    @Scheduled(cron = "0 0 0 * * ?")  // Run at midnight every day
//    public void checkUpcomingRenewals() {
//        LocalDate today = LocalDate.now();
//        Set<Notification> uniqueRenewalNotifications = new HashSet<>();
//        
//        // Check for different renewal frequencies
//        checkRenewalsByFrequency("Monthly", 4, today, uniqueRenewalNotifications);
//        checkRenewalsByFrequency("Quarterly", 7, today, uniqueRenewalNotifications);
//        checkRenewalsByFrequency("Semi Annual", 14, today, uniqueRenewalNotifications);
//        checkRenewalsByFrequency("Annual", 30, today, uniqueRenewalNotifications);
//        
//        // Save unique renewal notifications
//        if (!uniqueRenewalNotifications.isEmpty()) {
//            notificationRepository.saveAll(uniqueRenewalNotifications);
//            logger.info("Generated {} unique renewal notifications", uniqueRenewalNotifications.size());
//        }
//    }
//    
//    // Check renewals for a specific frequency
//    private void checkRenewalsByFrequency(String frequency, int daysBefore, LocalDate today, Set<Notification> notifications) {
//        LocalDate thresholdDate = today.plusDays(daysBefore);
//        LocalDate renewalThreshold = today.plusDays(7);
//        
//        // Find sales with end dates in the threshold range
//        List<Sales> allSales = salesRepository.findAll();
//        
//        for (Sales sale : allSales) {
//            // Check if the sale matches the billing frequency
//            if (frequency.equalsIgnoreCase(sale.getBillingFrequency())) {
//                // Check for renewals within the next 7 days
//                if ((sale.getEndDate().isAfter(today) || sale.getEndDate().isEqual(today)) && 
//                    sale.getEndDate().isBefore(renewalThreshold)) {
//                    
//                    // Check for existing unique renewal notification
//                    List<Notification> existingRenewalNotifications = notificationRepository
//                        .findUniqueNotificationsAcrossStatuses(
//                            sale.getDomainName(), 
//                            sale.getItemName(), 
//                            "RENEWAL"
//                        );
//                    
//                    // If no existing renewal notification
//                    if (existingRenewalNotifications.isEmpty()) {
//                        Notification renewalNotification = new Notification();
//                        renewalNotification.setType("RENEWAL");
//                        renewalNotification.setDomainName(sale.getDomainName());
//                        renewalNotification.setItemName(sale.getItemName());
//                        renewalNotification.setMessage(String.format(
//                            "Upcoming renewal for %s on %s. Renewal date: %s", 
//                            sale.getItemName(), 
//                            sale.getDomainName(), 
//                            sale.getEndDate()
//                        ));
//                        renewalNotification.setCreatedDate(today);
//                        renewalNotification.setDueDate(sale.getEndDate());
//                        renewalNotification.setStatus("NEW");
//                        
//                        notifications.add(renewalNotification);
//                    }
//                }
//            }
//        }
//    }
//    
//    // Helper method to create overdue notification
//    private Notification createOverdueNotification(
//        String domainName, 
//        String itemName, 
//        String baseMessage, 
//        LocalDate originalDueDate, 
//        LocalDate today
//    ) {
//        Notification overdueNotification = new Notification();
//        overdueNotification.setType("INVOICE_OVERDUE");
//        overdueNotification.setDomainName(domainName);
//        overdueNotification.setItemName(itemName);
//        overdueNotification.setMessage(String.format(
//            "URGENT: Overdue Invoice - %s on %s. Originally due on: %s", 
//            itemName, 
//            domainName,
//            originalDueDate
//        ));
//        overdueNotification.setCreatedDate(today);
//        overdueNotification.setDueDate(today);
//        overdueNotification.setStatus("NEW");
//        
//        return overdueNotification;
//    }
//    
//    // Method to generate manual invoice notification for a specific renewal
//    public Notification createInvoiceNotificationForRenewal(Long renewalNotificationId) {
//        Notification renewalNotification = notificationRepository.findById(renewalNotificationId).orElse(null);
//        
//        if (renewalNotification != null && "RENEWAL".equals(renewalNotification.getType())) {
//            // Check for existing unique invoice notification
//            List<Notification> existingNotifications = notificationRepository
//                .findUniqueNotificationsAcrossStatuses(
//                    renewalNotification.getDomainName(), 
//                    renewalNotification.getItemName(), 
//                    "INVOICE_NEEDED"
//                );
//            
//            if (existingNotifications.isEmpty()) {
//                Notification invoiceNotification = new Notification();
//                invoiceNotification.setType("INVOICE_NEEDED");
//                invoiceNotification.setDomainName(renewalNotification.getDomainName());
//                invoiceNotification.setItemName(renewalNotification.getItemName());
//                invoiceNotification.setMessage(String.format(
//                    "Please raise invoice for renewal of %s on %s. Renewal due on: %s", 
//                    renewalNotification.getItemName(), 
//                    renewalNotification.getDomainName(),
//                    renewalNotification.getDueDate()
//                ));
//                invoiceNotification.setCreatedDate(LocalDate.now());
//                invoiceNotification.setDueDate(renewalNotification.getDueDate());
//                invoiceNotification.setStatus("NEW");
//                
//                return notificationRepository.save(invoiceNotification);
//            } else {
//                return existingNotifications.get(0);
//            }
//        }
//        
//        return null;
//    }
//    
//    // Method to check and return all overdue items
//    public List<Notification> checkAllOverdueItems() {
//        LocalDate today = LocalDate.now();
//        Set<Notification> uniqueOverdueNotifications = new HashSet<>();
//        
//        // Find sales with end dates in the past
//        List<Sales> overdueItems = salesRepository.findByEndDateBefore(today);
//        
//        for (Sales sale : overdueItems) {
//            // Check for existing unique overdue notification
//            List<Notification> existingOverdueNotifications = notificationRepository
//                .findUniqueNotificationsAcrossStatuses(
//                    sale.getDomainName(), 
//                    sale.getItemName(), 
//                    "INVOICE_OVERDUE"
//                );
//            
//            if (existingOverdueNotifications.isEmpty()) {
//                Notification overdueNotification = createOverdueNotification(
//                    sale.getDomainName(), 
//                    sale.getItemName(), 
//                    String.format("Overdue sales item %s", sale.getItemName()), 
//                    sale.getEndDate(), 
//                    today
//                );
//                
//                uniqueOverdueNotifications.add(notificationRepository.save(overdueNotification));
//            }
//        }
//        
//        return new ArrayList<>(uniqueOverdueNotifications);
//    }
//}
package com.invoice.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoice.model.Notification;
import com.invoice.model.Sales;
import com.invoice.repository.NotificationRepository;
import com.invoice.repository.SalesRepository;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private SalesRepository salesRepository;

    /**
     * Get all notifications with consistent duplicate prevention
     */
    public List<Notification> getAllNotifications() {
        List<Notification> allNotifications = notificationRepository.findAll();
        return removeDuplicateNotifications(allNotifications);
    }
    
    /**
     * Remove duplicate notifications using a consistent approach
     * This is the core method for duplicate prevention and should be used across the application
     */
    private List<Notification> removeDuplicateNotifications(List<Notification> notifications) {
        // Using domain, item, and type as uniqueness criteria - status is intentionally omitted
        // This ensures we get the latest notification for a specific item regardless of status
        return notifications.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator
                    .comparing(Notification::getDomainName)
                    .thenComparing(Notification::getItemName)
                    .thenComparing(Notification::getType)
                    .thenComparing(Notification::getCreatedDate, Comparator.reverseOrder()))), 
                ArrayList::new));
    }
    
    /**
     * Save notification with consistent duplicate prevention
     */
    @Transactional
    public Notification saveNotification(Notification notification) {
        // Check for existing notification with same domain, item, and type
        List<Notification> existingNotifications = notificationRepository
            .findUniqueNotificationsAcrossStatuses(
                notification.getDomainName(), 
                notification.getItemName(), 
                notification.getType()
            );
        
        if (existingNotifications.isEmpty()) {
            logger.info("Saving new notification: {}|{}|{}", 
                notification.getType(), notification.getDomainName(), notification.getItemName());
            return notificationRepository.save(notification);
        } else {
            logger.info("Notification already exists: {}|{}|{}", 
                notification.getType(), notification.getDomainName(), notification.getItemName());
            return existingNotifications.get(0);
        }
    }
    
    /**
     * Get new notifications with consistent duplicate prevention
     */
    public List<Notification> getNewNotifications() {
        List<Notification> newNotifications = notificationRepository.findByStatusOrderByDueDateAsc("NEW");
        return removeDuplicateNotifications(newNotifications);
    }
    
    /**
     * Get notifications by type with consistent duplicate prevention
     */
    public List<Notification> getNotificationsByType(String type) {
        List<Notification> typeNotifications = notificationRepository.findByTypeAndStatusOrderByCreatedDateDesc(type, "NEW");
        return removeDuplicateNotifications(typeNotifications);
    }
    
    /**
     * Get notifications by multiple types with consistent duplicate prevention
     */
    public List<Notification> getNotificationsByTypes(List<String> types, String status) {
        List<Notification> typeNotifications = notificationRepository.findByTypeInAndStatusOrderByCreatedDateDesc(types, status);
        return removeDuplicateNotifications(typeNotifications);
    }
    
    /**
     * Mark notification as read
     */
    @Transactional
    public Notification markNotificationAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + id));
        
        notification.setStatus("READ");
        return notificationRepository.save(notification);
    }
    
    /**
     * Mark notification as resolved
     */
    @Transactional
    public Notification markNotificationAsResolved(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + id));
        
        notification.setStatus("RESOLVED");
        return notificationRepository.save(notification);
    }
    
    /**
     * Check overdue invoices - scheduled job
     */
    @Scheduled(cron = "0 0 10 * * ?")  // Run at 10:00 AM every day
    @Transactional
    public void checkOverdueInvoices() {
        LocalDate today = LocalDate.now();
        logger.info("Running scheduled overdue invoice check for date: {}", today);
        
        List<Notification> newOverdueNotifications = new ArrayList<>();
        
        // Find all sales items that are overdue
        List<Sales> allSales = salesRepository.findAll();
        
        for (Sales sale : allSales) {
            // Check if the end date is before or equal to today
            if (sale.getEndDate().isBefore(today) || sale.getEndDate().isEqual(today)) {
                // Create overdue notification if it doesn't exist
                Notification overdueNotification = createOverdueNotificationIfNeeded(
                    sale.getDomainName(), 
                    sale.getItemName(), 
                    String.format("URGENT: Invoice for %s on %s is now OVERDUE", 
                        sale.getItemName(), 
                        sale.getDomainName()
                    ), 
                    sale.getEndDate(), 
                    today
                );
                
                if (overdueNotification != null) {
                    newOverdueNotifications.add(overdueNotification);
                }
            }
        }
        
        // Check invoice notifications past due
        List<Notification> invoiceNotifications = notificationRepository
            .findByTypeAndDueDateBefore("INVOICE_NEEDED", today);
        
        for (Notification invoiceNotification : invoiceNotifications) {
            if ("NEW".equals(invoiceNotification.getStatus()) || 
                "READ".equals(invoiceNotification.getStatus())) {
                
                Notification overdueNotification = createOverdueNotificationIfNeeded(
                    invoiceNotification.getDomainName(), 
                    invoiceNotification.getItemName(), 
                    invoiceNotification.getMessage(), 
                    invoiceNotification.getDueDate(), 
                    today
                );
                
                if (overdueNotification != null) {
                    newOverdueNotifications.add(overdueNotification);
                }
            }
        }
        
        // Log results
        if (!newOverdueNotifications.isEmpty()) {
            logger.info("Generated {} unique overdue notifications", newOverdueNotifications.size());
        } else {
            logger.info("No new overdue notifications generated");
        }
    }
    
    /**
     * Check upcoming renewals - scheduled job
     */
    @Scheduled(cron = "0 0 0 * * ?")  // Run at midnight every day
    @Transactional
    public void checkUpcomingRenewals() {
        LocalDate today = LocalDate.now();
        logger.info("Running scheduled renewal check for date: {}", today);
        
        List<Notification> newRenewalNotifications = new ArrayList<>();
        
        // Check for different renewal frequencies
        newRenewalNotifications.addAll(checkRenewalsByFrequency("Monthly", 4, today));
        newRenewalNotifications.addAll(checkRenewalsByFrequency("Quarterly", 7, today));
        newRenewalNotifications.addAll(checkRenewalsByFrequency("Semi Annual", 14, today));
        newRenewalNotifications.addAll(checkRenewalsByFrequency("Annual", 30, today));
        
        // Log results
        if (!newRenewalNotifications.isEmpty()) {
            logger.info("Generated {} unique renewal notifications", newRenewalNotifications.size());
        } else {
            logger.info("No new renewal notifications generated");
        }
    }
    
    /**
     * Check renewals for a specific frequency
     */
    private List<Notification> checkRenewalsByFrequency(String frequency, int daysBefore, LocalDate today) {
        LocalDate renewalThreshold = today.plusDays(7);
        List<Notification> newNotifications = new ArrayList<>();
        
        // Find sales with matching frequency
        List<Sales> allSales = salesRepository.findAll().stream()
            .filter(sale -> frequency.equalsIgnoreCase(sale.getBillingFrequency()))
            .collect(Collectors.toList());
        
        for (Sales sale : allSales) {
            // Check for renewals within the next 7 days
            if ((sale.getEndDate().isAfter(today) || sale.getEndDate().isEqual(today)) && 
                sale.getEndDate().isBefore(renewalThreshold)) {
                
                // Create renewal notification if it doesn't exist
                Notification renewalNotification = createRenewalNotificationIfNeeded(
                    sale.getDomainName(),
                    sale.getItemName(),
                    sale.getEndDate(),
                    today
                );
                
                if (renewalNotification != null) {
                    newNotifications.add(renewalNotification);
                }
            }
        }
        
        return newNotifications;
    }
    
    /**
     * Helper method to create overdue notification only if it doesn't exist
     * Returns the newly created notification or null if it already exists
     */
    @Transactional
    private Notification createOverdueNotificationIfNeeded(
        String domainName, 
        String itemName, 
        String baseMessage, 
        LocalDate originalDueDate, 
        LocalDate today
    ) {
        // Check for existing unique overdue notification
        List<Notification> existingOverdueNotifications = notificationRepository
            .findUniqueNotificationsAcrossStatuses(
                domainName, 
                itemName, 
                "INVOICE_OVERDUE"
            );
        
        // If no existing overdue notification, create one
        if (existingOverdueNotifications.isEmpty()) {
            Notification overdueNotification = new Notification();
            overdueNotification.setType("INVOICE_OVERDUE");
            overdueNotification.setDomainName(domainName);
            overdueNotification.setItemName(itemName);
            overdueNotification.setMessage(String.format(
                "URGENT: Overdue Invoice - %s on %s. Originally due on: %s", 
                itemName, 
                domainName,
                originalDueDate
            ));
            overdueNotification.setCreatedDate(today);
            overdueNotification.setDueDate(today);
            overdueNotification.setStatus("NEW");
            
            return notificationRepository.save(overdueNotification);
        }
        
        return null;
    }
    
    /**
     * Helper method to create renewal notification only if it doesn't exist
     * Returns the newly created notification or null if it already exists
     */
    @Transactional
    private Notification createRenewalNotificationIfNeeded(
        String domainName,
        String itemName,
        LocalDate renewalDate,
        LocalDate today
    ) {
        // Check for existing unique renewal notification
        List<Notification> existingRenewalNotifications = notificationRepository
            .findUniqueNotificationsAcrossStatuses(
                domainName, 
                itemName, 
                "RENEWAL"
            );
        
        // If no existing renewal notification, create one
        if (existingRenewalNotifications.isEmpty()) {
            Notification renewalNotification = new Notification();
            renewalNotification.setType("RENEWAL");
            renewalNotification.setDomainName(domainName);
            renewalNotification.setItemName(itemName);
            renewalNotification.setMessage(String.format(
                "Upcoming renewal for %s on %s. Renewal date: %s", 
                itemName, 
                domainName, 
                renewalDate
            ));
            renewalNotification.setCreatedDate(today);
            renewalNotification.setDueDate(renewalDate);
            renewalNotification.setStatus("NEW");
            
            return notificationRepository.save(renewalNotification);
        }
        
        return null;
    }
    
    /**
     * Method to generate manual invoice notification for a specific renewal
     */
    @Transactional
    public Notification createInvoiceNotificationForRenewal(Long renewalNotificationId) {
        Notification renewalNotification = notificationRepository.findById(renewalNotificationId)
            .orElseThrow(() -> new IllegalArgumentException("Renewal notification not found with id: " + renewalNotificationId));
        
        if (!"RENEWAL".equals(renewalNotification.getType())) {
            throw new IllegalArgumentException("Notification is not a renewal notification");
        }
        
        // Check for existing unique invoice notification
        List<Notification> existingNotifications = notificationRepository
            .findUniqueNotificationsAcrossStatuses(
                renewalNotification.getDomainName(), 
                renewalNotification.getItemName(), 
                "INVOICE_NEEDED"
            );
        
        if (existingNotifications.isEmpty()) {
            Notification invoiceNotification = new Notification();
            invoiceNotification.setType("INVOICE_NEEDED");
            invoiceNotification.setDomainName(renewalNotification.getDomainName());
            invoiceNotification.setItemName(renewalNotification.getItemName());
            invoiceNotification.setMessage(String.format(
                "Please raise invoice for renewal of %s on %s. Renewal due on: %s", 
                renewalNotification.getItemName(), 
                renewalNotification.getDomainName(),
                renewalNotification.getDueDate()
            ));
            invoiceNotification.setCreatedDate(LocalDate.now());
            invoiceNotification.setDueDate(renewalNotification.getDueDate());
            invoiceNotification.setStatus("NEW");
            
            return notificationRepository.save(invoiceNotification);
        } else {
            return existingNotifications.get(0);
        }
    }
    
    /**
     * Method to check and return all overdue items
     */
    @Transactional
    public List<Notification> checkAllOverdueItems() {
        LocalDate today = LocalDate.now();
        List<Notification> newOverdueNotifications = new ArrayList<>();
        
        // Find sales with end dates in the past
        List<Sales> overdueItems = salesRepository.findByEndDateBefore(today);
        
        for (Sales sale : overdueItems) {
            Notification overdueNotification = createOverdueNotificationIfNeeded(
                sale.getDomainName(), 
                sale.getItemName(), 
                String.format("Overdue sales item %s", sale.getItemName()), 
                sale.getEndDate(), 
                today
            );
            
            if (overdueNotification != null) {
                newOverdueNotifications.add(overdueNotification);
            }
        }
        
        return newOverdueNotifications;
    }
    
    /**
     * Get notifications by domain
     */
    public List<Notification> getNotificationsByDomain(String domainName) {
        List<Notification> domainNotifications = notificationRepository.findByDomainName(domainName);
        return removeDuplicateNotifications(domainNotifications);
    }
    
    /**
     * Get all overdue notifications 
     */
    public List<Notification> getOverdueNotifications() {
        List<Notification> overdueNotifications = notificationRepository
            .findByTypeAndStatusOrderByCreatedDateDesc("INVOICE_OVERDUE", "NEW");
        return removeDuplicateNotifications(overdueNotifications);
    }
    
    /**
     * Get all renewal notifications
     */
    public List<Notification> getRenewalNotifications() {
        List<Notification> renewalNotifications = notificationRepository
            .findByTypeAndStatusOrderByCreatedDateDesc("RENEWAL", "NEW");
        return removeDuplicateNotifications(renewalNotifications);
    }
    
    /**
     * Get all invoice-related notifications
     */
    public List<Notification> getInvoiceNotifications() {
        List<String> invoiceTypes = Arrays.asList("INVOICE_NEEDED", "INVOICE_OVERDUE");
        return getNotificationsByTypes(invoiceTypes, "NEW");
    }
}