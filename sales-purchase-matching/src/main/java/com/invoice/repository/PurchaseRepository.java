// PurchaseRepository.java
package com.invoice.repository;

import com.invoice.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByDomainNameContainingIgnoreCase(String domainName);
    
    List<Purchase> findBySubscriptionContainingIgnoreCase(String subscription);
    
    @Query("SELECT p FROM Purchase p WHERE p.domainName = ?1 AND p.subscription = ?2")
    List<Purchase> findByDomainAndSubscription(String domainName, String subscription);
}
