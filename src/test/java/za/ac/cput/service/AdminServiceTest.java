package za.ac.cput.service;



import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.*;
import za.ac.cput.factory.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// ========================= ADMIN SERVICE TEST =========================
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    private static Admin testAdmin;
    private static User testUser;

    @BeforeAll
    static void setup() {
        testUser = UserFactory.createClientUser(
                "Admin", "User", "admin@test.com", "Password123!"
        );
    }

    @Test
    @Order(1)
    void createUser() {
        testUser = userService.create(testUser);
        assertNotNull(testUser.getUserId());
        System.out.println("Created test user for admin: " + testUser);
    }

    @Test
    @Order(2)
    void createSuperAdmin() {
        testAdmin = adminService.createSuperAdmin(testUser);
        assertNotNull(testAdmin);
        assertNotNull(testAdmin.getAdminId());
        assertEquals(testUser.getUserId(), testAdmin.getUser().getUserId());
        assertTrue(testAdmin.getPermissions().contains(Admin.Permission.MANAGE_USERS));
        assertTrue(testAdmin.getPermissions().contains(Admin.Permission.VERIFY_SKILLS));
        assertTrue(testAdmin.getPermissions().contains(Admin.Permission.RESOLVE_DISPUTES));
        System.out.println("Created super admin: " + testAdmin);
    }

    @Test
    @Order(3)
    void create_DuplicateAdmin_ShouldFail() {
        assertThrows(IllegalStateException.class, () -> {
            adminService.createSuperAdmin(testUser);
        });
        System.out.println("Correctly prevented duplicate admin creation");
    }

    @Test
    @Order(4)
    void createSkillVerifier() {
        User verifierUser = UserFactory.createClientUser(
                "Skill", "Verifier", "verifier@test.com", "Password123!"
        );
        verifierUser = userService.create(verifierUser);

        Admin skillVerifier = adminService.createSkillVerifier(verifierUser);
        assertNotNull(skillVerifier);
        assertTrue(skillVerifier.getPermissions().contains(Admin.Permission.VERIFY_SKILLS));
        assertFalse(skillVerifier.getPermissions().contains(Admin.Permission.MANAGE_USERS));
        System.out.println("Created skill verifier admin: " + skillVerifier);
    }

    @Test
    @Order(5)
    void createCustomAdmin() {
        User customAdminUser = UserFactory.createClientUser(
                "Custom", "Admin", "custom@test.com", "Password123!"
        );
        customAdminUser = userService.create(customAdminUser);

        List<Admin.Permission> permissions = List.of(
                Admin.Permission.MANAGE_USERS,
                Admin.Permission.VIEW_REPORTS
        );

        Admin customAdmin = adminService.createCustomAdmin(
                customAdminUser, permissions, "Custom admin created for testing"
        );
        assertNotNull(customAdmin);
        assertEquals(2, customAdmin.getPermissions().size());
        assertTrue(customAdmin.getPermissions().contains(Admin.Permission.MANAGE_USERS));
        assertTrue(customAdmin.getPermissions().contains(Admin.Permission.VIEW_REPORTS));
        System.out.println("Created custom admin: " + customAdmin);
    }

    @Test
    @Order(6)
    void read() {
        Admin readAdmin = adminService.read(testAdmin.getAdminId());
        assertNotNull(readAdmin);
        assertEquals(testAdmin.getAdminId(), readAdmin.getAdminId());
        assertEquals(testAdmin.getUser().getUserId(), readAdmin.getUser().getUserId());
       // System.out.println("Read admin: " + readAdmin);
    }

    @Test
    @Order(7)
    void addPermission() {
        testAdmin = adminService.addPermission(testAdmin.getAdminId(), Admin.Permission.MANAGE_PAYMENTS);
        assertTrue(testAdmin.getPermissions().contains(Admin.Permission.MANAGE_PAYMENTS));
        System.out.println("Added MANAGE_PAYMENTS permission to admin");
    }

    @Test
    @Order(8)
    void removePermission() {
        testAdmin = adminService.removePermission(testAdmin.getAdminId(), Admin.Permission.VIEW_REPORTS);
        assertFalse(testAdmin.getPermissions().contains(Admin.Permission.VIEW_REPORTS));
        System.out.println("Removed VIEW_REPORTS permission from admin");
    }

    @Test
    @Order(9)
    void logAction() {
        testAdmin = adminService.logAction(testAdmin.getAdminId(), "Performed test action");
        assertNotNull(testAdmin.getActionsLog());
        assertTrue(testAdmin.getActionsLog().contains("Performed test action"));
        System.out.println("Logged action for admin");
    }

    @Test
    @Order(10)
    void findByUser() {
        Optional<Admin> foundAdmin = adminService.findByUser(testUser);
        assertTrue(foundAdmin.isPresent());
        assertEquals(testAdmin.getAdminId(), foundAdmin.get().getAdminId());
        System.out.println("Found admin by user");
    }

    @Test
    @Order(11)
    void hasPermission() {
        boolean hasManageUsers = adminService.hasPermission(testUser, Admin.Permission.MANAGE_USERS);
        boolean hasResolveDisputes = adminService.hasPermission(testUser, Admin.Permission.RESOLVE_DISPUTES);

        assertTrue(hasManageUsers);
        assertTrue(hasResolveDisputes);
        System.out.println("Permission checks working correctly");
    }

    @Test
    @Order(12)
    void isUserAdmin() {
        boolean isAdmin = adminService.isUserAdmin(testUser);
        assertTrue(isAdmin);
        System.out.println("Admin user check working correctly");
    }

    @Test
    @Order(13)
    void findAdminsByPermission() {
        List<Admin> adminsWithManageUsers = adminService.findAdminsByPermission(Admin.Permission.MANAGE_USERS);
        assertFalse(adminsWithManageUsers.isEmpty());
        assertTrue(adminsWithManageUsers.stream()
                .allMatch(admin -> admin.getPermissions().contains(Admin.Permission.MANAGE_USERS)));
        System.out.println("Found admins with MANAGE_USERS permission: " + adminsWithManageUsers.size());
    }

    @Test
    @Order(14)
    void findAdminsCreatedBetween() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

        List<Admin> adminsInRange = adminService.findAdminsCreatedBetween(yesterday, tomorrow);
        assertFalse(adminsInRange.isEmpty());
        System.out.println("Found admins created in date range: " + adminsInRange.size());
    }

    @Test
    @Order(15)
    void delete() {
        Admin deletedAdmin = adminService.delete(testAdmin.getAdminId());
        assertNotNull(deletedAdmin);
        assertEquals(testAdmin.getAdminId(), deletedAdmin.getAdminId());

        assertThrows(RuntimeException.class, () -> {
            adminService.read(testAdmin.getAdminId());
        });
        System.out.println("Deleted admin: " + deletedAdmin.getAdminId());
    }
}