package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Skill;
import za.ac.cput.domain.User;
import za.ac.cput.domain.WorkerProfile;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class WorkerProfileFactoryTest {

    private User user;
    private List<Skill> skills;

    @BeforeEach
    void setUp() {
        user = UserFactory.createWorkerUser("John", "Worker", "john.worker@email.com", "Password123!", "0821234567");
        Skill skill1 = SkillFactory.createSkill("Java Programming", "Programming", "Java development");
        Skill skill2 = SkillFactory.createSkill("Web Development", "Programming", "Frontend and backend development");
        skills = List.of(skill1, skill2);
    }

    @Test
    void createWorkerProfile() {
        String bio = "Experienced software developer with passion for creating quality applications.";
        WorkerProfile profile = WorkerProfileFactory.createWorkerProfile(user, bio, skills,
                "5 years experience", 500.0, "Cape Town");

        assertNotNull(profile);
        assertEquals(user, profile.getUser());
        assertEquals(bio, profile.getBio());
        assertEquals(skills, profile.getSkills());
        assertEquals("5 years experience", profile.getExperience());
        assertEquals(500.0, profile.getHourlyRate());
        assertEquals("Cape Town", profile.getLocation());
        assertEquals(WorkerProfile.AvailabilityStatus.AVAILABLE, profile.getAvailabilityStatus());
        assertEquals(WorkerProfile.VerificationStatus.PENDING, profile.getVerificationStatus());
        assertNull(profile.getRating());
        assertNull(profile.getVerificationCode());
        assertNotNull(profile.getProfileId());
    }

    @Test
    void createWorkerProfile_WithoutBio() {
        WorkerProfile profile = WorkerProfileFactory.createWorkerProfile(user, null, skills,
                "3 years experience", 400.0, "Johannesburg");

        assertNotNull(profile);
        assertNull(profile.getBio());
        assertEquals("3 years experience", profile.getExperience());
    }

    @Test
    void createWorkerProfile_InvalidUser() {
        WorkerProfile profile = WorkerProfileFactory.createWorkerProfile(null, "Bio", skills,
                "Experience", 400.0, "Location");
        assertNull(profile);
    }

    @Test
    void createWorkerProfile_InvalidBio() {
        // Test with bio too short
        WorkerProfile profile = WorkerProfileFactory.createWorkerProfile(user, "Short", skills,
                "Experience", 400.0, "Location");
        assertNull(profile);

        // Test with bio too long
        profile = WorkerProfileFactory.createWorkerProfile(user, "A".repeat(1001), skills,
                "Experience", 400.0, "Location");
        assertNull(profile);
    }

    @Test
    void createWorkerProfile_InvalidSkills() {
        // Test with null skills
        WorkerProfile profile = WorkerProfileFactory.createWorkerProfile(user, "Valid bio here", null,
                "Experience", 400.0, "Location");
        assertNull(profile);

        // Test with empty skills
        profile = WorkerProfileFactory.createWorkerProfile(user, "Valid bio here", List.of(),
                "Experience", 400.0, "Location");
        assertNull(profile);
    }

    @Test
    void createWorkerProfile_InvalidHourlyRate() {
        // Test with null hourly rate
        WorkerProfile profile = WorkerProfileFactory.createWorkerProfile(user, "Valid bio here", skills,
                "Experience", null, "Location");
        assertNull(profile);

        // Test with hourly rate too low
        profile = WorkerProfileFactory.createWorkerProfile(user, "Valid bio here", skills,
                "Experience", 30.0, "Location");
        assertNull(profile);

        // Test with hourly rate too high
        profile = WorkerProfileFactory.createWorkerProfile(user, "Valid bio here", skills,
                "Experience", 15000.0, "Location");
        assertNull(profile);
    }

    @Test
    void createWorkerProfile_InvalidLocation() {
        WorkerProfile profile = WorkerProfileFactory.createWorkerProfile(user, "Valid bio here", skills,
                "Experience", 400.0, null);
        assertNull(profile);

        profile = WorkerProfileFactory.createWorkerProfile(user, "Valid bio here", skills,
                "Experience", 400.0, "");
        assertNull(profile);
    }

    @Test
    void createBasicWorkerProfile() {
        WorkerProfile profile = WorkerProfileFactory.createBasicWorkerProfile(user, skills, 300.0, "Durban");

        assertNotNull(profile);
        assertEquals(user, profile.getUser());
        assertNull(profile.getBio());
        assertEquals(skills, profile.getSkills());
        assertNull(profile.getExperience());
        assertEquals(300.0, profile.getHourlyRate());
        assertEquals("Durban", profile.getLocation());
    }

    @Test
    void createBasicWorkerProfile_InvalidInput() {
        WorkerProfile profile = WorkerProfileFactory.createBasicWorkerProfile(null, skills, 300.0, "Location");
        assertNull(profile);
    }
}
