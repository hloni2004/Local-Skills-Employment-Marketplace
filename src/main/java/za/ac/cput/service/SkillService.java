package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Skill;
import za.ac.cput.repository.SkillRepository;
import za.ac.cput.factory.SkillFactory;
import za.ac.cput.util.ValidationHelper;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SkillService implements IService<Skill, String> {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Skill create(Skill skill) {
        ValidationHelper.ValidationResult result = ValidationHelper.validateSkill(skill);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid skill data: " + result.getErrorMessage());
        }

        // Check if skill name already exists
        if (skillRepository.existsByName(skill.getName())) {
            throw new IllegalArgumentException("Skill with this name already exists");
        }

        return skillRepository.save(skill);
    }

    @Override
    public Skill read(String skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found with ID: " + skillId));
    }

    @Override
    public Skill update(Skill skill) {
        if (!skillRepository.existsById(skill.getSkillId())) {
            throw new RuntimeException("Skill not found");
        }

        ValidationHelper.ValidationResult result = ValidationHelper.validateSkill(skill);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid skill data: " + result.getErrorMessage());
        }

        // Check if updated name conflicts with existing skill (excluding current skill)
        Optional<Skill> existingSkill = skillRepository.findByName(skill.getName());
        if (existingSkill.isPresent() && !existingSkill.get().getSkillId().equals(skill.getSkillId())) {
            throw new IllegalArgumentException("Skill with this name already exists");
        }

        return skillRepository.save(skill);
    }

    @Override
    public Skill delete(String skillId) {
        Skill skill = read(skillId);
        skillRepository.deleteById(skillId);
        return skill;
    }

    // Business Logic Methods
    public Skill createSkill(String name, String category, String description) {
        Skill skill = SkillFactory.createSkill(name, category, description);
        if (skill == null) {
            throw new IllegalArgumentException("Invalid skill creation data");
        }
        return create(skill);
    }

    public Skill createVerifiedSkill(String name, String category, String description) {
        Skill skill = SkillFactory.createVerifiedSkill(name, category, description);
        if (skill == null) {
            throw new IllegalArgumentException("Invalid verified skill creation data");
        }
        return create(skill);
    }

    public Skill verifySkill(String skillId) {
        Skill skill = read(skillId);

        if (skill.getVerificationStatus() == Skill.VerificationStatus.VERIFIED) {
            throw new IllegalStateException("Skill is already verified");
        }

        return update(new Skill.Builder()
                .copy(skill)
                .setVerificationStatus(Skill.VerificationStatus.VERIFIED)
                .build());
    }

    public Skill rejectSkill(String skillId) {
        Skill skill = read(skillId);

        if (skill.getVerificationStatus() == Skill.VerificationStatus.REJECTED) {
            throw new IllegalStateException("Skill is already rejected");
        }

        return update(new Skill.Builder()
                .copy(skill)
                .setVerificationStatus(Skill.VerificationStatus.REJECTED)
                .build());
    }

    public Skill resetSkillVerification(String skillId) {
        Skill skill = read(skillId);

        return update(new Skill.Builder()
                .copy(skill)
                .setVerificationStatus(Skill.VerificationStatus.PENDING)
                .build());
    }

    public Optional<Skill> findByName(String name) {
        return skillRepository.findByName(name);
    }

    public List<Skill> findSkillsByCategory(String category) {
        return skillRepository.findByCategory(category);
    }

    public List<Skill> findSkillsByVerificationStatus(Skill.VerificationStatus status) {
        return skillRepository.findByVerificationStatus(status);
    }

    public List<Skill> findVerifiedSkills() {
        return skillRepository.findVerifiedSkills();
    }

    public List<Skill> findPendingSkills() {
        return skillRepository.findByVerificationStatus(Skill.VerificationStatus.PENDING);
    }

    public List<Skill> searchSkillsByName(String keyword) {
        return skillRepository.searchByName(keyword);
    }

    public List<Skill> findVerifiedSkillsByCategory(String category) {
        return skillRepository.findByCategoryAndVerificationStatus(category, Skill.VerificationStatus.VERIFIED);
    }

    public boolean skillExists(String name) {
        return skillRepository.existsByName(name);
    }

    public List<String> getAllCategories() {
        return skillRepository.findAll().stream()
                .map(Skill::getCategory)
                .distinct()
                .sorted()
                .toList();
    }

    public List<Skill> getSkillsRequiringVerification() {
        return findPendingSkills();
    }

    public long countSkillsByStatus(Skill.VerificationStatus status) {
        return skillRepository.findByVerificationStatus(status).size();
    }

    public boolean canDeleteSkill(String skillId) {
        // In a complete implementation, this would check if skill is being used
        // by any worker profiles before allowing deletion
        return true;
    }
}