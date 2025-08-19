package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

public class SkillFactory {

    public static Skill createSkill(String name, String category, String description) {

        // Validate inputs
        if (ValidationHelper.isNullOrEmpty(name) || !ValidationHelper.isValidLength(name, 2, 50)) {
            return null;
        }
        if (ValidationHelper.isNullOrEmpty(category)) {
            return null;
        }
        if (description != null && !ValidationHelper.isValidLength(description, 10, 500)) {
            return null;
        }

        return new Skill.Builder()
                .setSkillId(ValidationHelper.generateId())
                .setName(name.trim())
                .setCategory(category.trim())
                .setDescription(description != null ? description.trim() : null)
                .setVerificationStatus(Skill.VerificationStatus.PENDING)
                .build();
    }

    public static Skill createVerifiedSkill(String name, String category, String description) {
        Skill skill = createSkill(name, category, description);
        if (skill == null) return null;

        return new Skill.Builder()
                .copy(skill)
                .setVerificationStatus(Skill.VerificationStatus.VERIFIED)
                .build();
    }
}