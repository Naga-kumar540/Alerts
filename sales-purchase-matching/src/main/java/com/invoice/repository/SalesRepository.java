// STEP 3: Repository interfaces
// SalesRepository.java
package com.invoice.repository;

import com.invoice.model.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
    List<Sales> findByDomainNameContainingIgnoreCase(String domainName);
    
    List<Sales> findByItemNameContainingIgnoreCase(String itemName);
    
    List<Sales> findByEndDateBetween(LocalDate start, LocalDate end);
    
    @Query("SELECT s FROM Sales s WHERE s.endDate > CURRENT_DATE AND s.endDate <= ?1")
    List<Sales> findUpcomingRenewals(LocalDate thresholdDate);
    
    @Query("SELECT s FROM Sales s WHERE s.domainName = ?1 AND s.itemName = ?2")
    List<Sales> findByDomainAndItem(String domainName, String itemName);
    
    List<Sales> findByEndDateBefore(LocalDate date);
}
