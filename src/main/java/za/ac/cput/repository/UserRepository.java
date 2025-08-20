package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Find user by email
    Optional<User> findByEmail(String email);

    // Check if email exists
    boolean existsByEmail(String email);

    // Find users by role
    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    List<User> findByRole(@Param("role") User.Role role);

    // Find users by current mode
    List<User> findByCurrentMode(User.Mode currentMode);

    // Find users by status
    List<User> findByStatus(User.Status status);

    // Find active users
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findActiveUsers();

    // Search users by first name or last name
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContaining(@Param("name") String name);

    // Find users by phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Find users who joined within a date range
    @Query("SELECT u FROM User u WHERE u.dateJoined BETWEEN :startDate AND :endDate")
    List<User> findByDateJoinedBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                       @Param("startDate") java.time.LocalDateTime endDate);

    // Count users by status
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    Long countByStatus(@Param("status") User.Status status);

    // Find users with worker profiles
    @Query("SELECT u FROM User u WHERE u.workerProfile IS NOT NULL")
    List<User> findUsersWithWorkerProfile();
}