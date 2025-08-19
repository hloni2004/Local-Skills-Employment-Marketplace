package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Skill;
import static org.junit.jupiter.api.Assertions.*;

class SkillFactoryTest {

    @Test
    void createSkill() {
        Skill skill = SkillFactory.createSkill("Java Programming", "Programming",
                "Object-oriented programming language");

        assertNotNull(skill);
        assertEquals("Java Programming", skill.getName());
        assertEquals("Programming", skill.getCategory());
        assertEquals("Object-oriented programming language", skill.getDescription());
        assertEquals(Skill.VerificationStatus.PENDING, skill.getVerificationStatus());
        assertNotNull(skill.getSkillId());
        System.out.print(skill);
    }

    @Test
    void createSkill_WithoutDescription() {
        Skill skill = SkillFactory.createSkill("Python", "Programming", null);

        assertNotNull(skill);
        assertEquals("Python", skill.getName());
        assertEquals("Programming", skill.getCategory());
        assertNull(skill.getDescription());
        assertEquals(Skill.VerificationStatus.PENDING, skill.getVerificationStatus());
        System.out.print(skill);
    }

    @Test
    void createSkill_InvalidName() {
        // Test with null name
        Skill skill = SkillFactory.createSkill(null, "Programming", "Description");
        assertNull(skill);

        // Test with empty name
        skill = SkillFactory.createSkill("", "Programming", "Description");
        assertNull(skill);

        // Test with name too short
        skill = SkillFactory.createSkill("A", "Programming", "Description");
        assertNull(skill);

        // Test with name too long
        skill = SkillFactory.createSkill("A".repeat(51), "Programming", "Description");
        assertNull(skill);
        System.out.print(skill);
    }

    @Test
    void createSkill_InvalidCategory() {
        Skill skill = SkillFactory.createSkill("Java", null, "Description");
        assertNull(skill);

        skill = SkillFactory.createSkill("Java", "", "Description");
        assertNull(skill);
        System.out.print(skill);
    }

    @Test
    void createSkill_InvalidDescription() {
        // Test with description too short
        Skill skill = SkillFactory.createSkill("Java", "Programming", "Short");
        assertNull(skill);

        // Test with description too long
        skill = SkillFactory.createSkill("Java", "Programming", "A".repeat(501));
        assertNull(skill);
        System.out.print(skill);
    }

    @Test
    void createVerifiedSkill() {
        Skill skill = SkillFactory.createVerifiedSkill("JavaScript", "Programming",
                "Client-side scripting language");

        assertNotNull(skill);
        assertEquals("JavaScript", skill.getName());
        assertEquals("Programming", skill.getCategory());
        assertEquals("Client-side scripting language", skill.getDescription());
        assertEquals(Skill.VerificationStatus.VERIFIED, skill.getVerificationStatus());
        System.out.print(skill);
    }

    @Test
    void createVerifiedSkill_InvalidInput() {
        Skill skill = SkillFactory.createVerifiedSkill(null, "Programming", "Description");
        assertNull(skill);
        System.out.print(skill);
    }
}