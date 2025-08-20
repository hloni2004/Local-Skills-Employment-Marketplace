package za.ac.cput.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.*;

import java.util.*;

@Repository
public interface SkillRepository extends JpaRepository<Skill, String> {

    // Find skills by name
    Optional<Skill> findByName(String name);

    // Find skills by category
    List<Skill> findByCategory(String category);

    // Find skills by verification status
    List<Skill> findByVerificationStatus(Skill.VerificationStatus status);

    // Find verified skills
    @Query("SELECT s FROM Skill s WHERE s.verificationStatus = 'VERIFIED'")
    List<Skill> findVerifiedSkills();

    // Search skills by name containing keyword
    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Skill> searchByName(@Param("keyword") String keyword);

    // Find skills by category and verification status
    List<Skill> findByCategoryAndVerificationStatus(String category, Skill.VerificationStatus status);

    // Check if skill name exists
    boolean existsByName(String name);
}
