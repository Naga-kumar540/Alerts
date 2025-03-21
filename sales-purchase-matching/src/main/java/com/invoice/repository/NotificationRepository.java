//package com.invoice.repository;
//
//import com.invoice.model.Notification;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//public interface NotificationRepository extends JpaRepository<Notification, Long> {
//    // Existing methods
//    List<Notification> findByStatusOrderByDueDateAsc(String status);
//    
//    List<Notification> findByTypeAndStatusOrderByCreatedDateDesc(String type, String status);
//    
//    List<Notification> findByDomainNameAndItemNameAndType(String domainName, String itemName, String type);
//    
//    // New method to find unique notifications
//    @Query("SELECT n FROM Notification n " +
//           "WHERE n.type = :type " +
//           "AND n.domainName = :domainName " +
//           "AND n.itemName = :itemName " +
//           "AND n.status = :status " +
//           "ORDER BY n.createdDate DESC")
//    List<Notification> findUniqueNotifications(
//        @Param("type") String type, 
//        @Param("domainName") String domainName, 
//        @Param("itemName") String itemName, 
//        @Param("status") String status
//    );
//    
//    // Existing additional methods
//    List<Notification> findByTypeAndDueDateBefore(String type, LocalDate date);
//    
//    List<Notification> findByTypeAndDueDateBetween(String type, LocalDate startDate, LocalDate endDate);
//    
//    List<Notification> findByTypeAndStatus(String type, String status);
//    
//    List<Notification> findByTypeInAndStatusOrderByCreatedDateDesc(List<String> types, String status);
//    
//    List<Notification> findByTypeAndStatusAndDomainName(String type, String status, String domainName);
//    
//    List<Notification> findTop5ByTypeOrderByCreatedDateDesc(String type);
//    
//    List<Notification> findByDomainNameAndItemNameAndTypeAndStatusIn(
//        String domainName, String itemName, String type, List<String> statuses
//    );
//    
//    List<Notification> findByTypeAndDueDateBeforeAndStatusIn(
//        String type, LocalDate date, List<String> statuses
//    );
//    
//    // Additional method to find unique notifications across statuses
//    @Query("SELECT DISTINCT n FROM Notification n " +
//           "WHERE n.domainName = :domainName " +
//           "AND n.itemName = :itemName " +
//           "AND n.type = :type")
//    List<Notification> findUniqueNotificationsAcrossStatuses(
//        @Param("domainName") String domainName, 
//        @Param("itemName") String itemName, 
//        @Param("type") String type
//    );
//}


package com.invoice.repository;

import com.invoice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Basic status and type queries
    List<Notification> findByStatusOrderByDueDateAsc(String status);
    
    List<Notification> findByTypeAndStatusOrderByCreatedDateDesc(String type, String status);
    
    List<Notification> findByTypeInAndStatusOrderByCreatedDateDesc(List<String> types, String status);
    
    List<Notification> findByTypeAndStatusAndDomainName(String type, String status, String domainName);
    
    List<Notification> findByTypeAndStatus(String type, String status);
    
    // Due date related queries
    List<Notification> findByTypeAndDueDateBefore(String type, LocalDate date);
    
    List<Notification> findByTypeAndDueDateBetween(String type, LocalDate startDate, LocalDate endDate);
    
    List<Notification> findByTypeAndDueDateBeforeAndStatusIn(
        String type, LocalDate date, List<String> statuses
    );
    
    // Domain and item specific queries
    List<Notification> findByDomainName(String domainName);
    
    List<Notification> findByDomainNameAndItemNameAndType(
        String domainName, String itemName, String type
    );
    
    List<Notification> findByDomainNameAndItemNameAndTypeAndStatusIn(
        String domainName, String itemName, String type, List<String> statuses
    );
    
    // Recent notifications
    List<Notification> findTop5ByTypeOrderByCreatedDateDesc(String type);
    
    // Unique notification query - find notifications with exact match on all fields
    @Query("SELECT n FROM Notification n " +
           "WHERE n.type = :type " +
           "AND n.domainName = :domainName " +
           "AND n.itemName = :itemName " +
           "AND n.status = :status " +
           "ORDER BY n.createdDate DESC")
    List<Notification> findUniqueNotifications(
        @Param("type") String type, 
        @Param("domainName") String domainName, 
        @Param("itemName") String itemName, 
        @Param("status") String status
    );
    
    // Unique notifications across statuses - find notifications regardless of status
    @Query("SELECT n FROM Notification n " +
           "WHERE n.domainName = :domainName " +
           "AND n.itemName = :itemName " +
           "AND n.type = :type " +
           "ORDER BY n.createdDate DESC")
    List<Notification> findUniqueNotificationsAcrossStatuses(
        @Param("domainName") String domainName, 
        @Param("itemName") String itemName, 
        @Param("type") String type
    );
    
    // Additional query to find latest notifications by domain, item, and type
    @Query("SELECT n FROM Notification n " +
           "WHERE n.domainName = :domainName " +
           "AND n.itemName = :itemName " +
           "AND n.type = :type " +
           "ORDER BY n.createdDate DESC")
    List<Notification> findLatestNotificationsByDomainItemAndType(
        @Param("domainName") String domainName, 
        @Param("itemName") String itemName, 
        @Param("type") String type
    );
    
    // Query to find all notifications by domain with status NEW
    @Query("SELECT n FROM Notification n " +
           "WHERE n.domainName = :domainName " +
           "AND n.status = 'NEW' " +
           "ORDER BY n.createdDate DESC")
    List<Notification> findNewNotificationsByDomain(
        @Param("domainName") String domainName
    );
}
