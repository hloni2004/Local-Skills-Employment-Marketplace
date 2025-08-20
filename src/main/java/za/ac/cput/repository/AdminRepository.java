package za.ac.cput.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.*;


import java.time.*;
import java.util.*;


@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

    // Find admin by user
    Optional<Admin> findByUser(User user);

    // Find admins with specific permission
    @Query("SELECT a FROM Admin a WHERE :permission MEMBER OF a.permissions")
    List<Admin> findByPermission(@Param("permission") Admin.Permission permission);

    // Find admins created within date range
    @Query("SELECT a FROM Admin a WHERE a.dateCreated BETWEEN :startDate AND :endDate")
    List<Admin> findByDateCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    // Check if user is admin
    boolean existsByUser(User user);
}
