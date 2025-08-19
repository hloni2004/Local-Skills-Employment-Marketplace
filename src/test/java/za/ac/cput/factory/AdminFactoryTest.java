package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Admin;
import za.ac.cput.domain.User;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AdminFactoryTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFactory.createClientUser("Admin", "User", "admin@email.com", "Password123!");
    }

    @Test
    void createAdmin() {
        List<Admin.Permission> permissions = List.of(Admin.Permission.MANAGE_USERS, Admin.Permission.VERIFY_SKILLS);
        String actionsLog = "Admin created with manage users and verify skills permissions";

        Admin admin = AdminFactory.createAdmin(user, permissions, actionsLog);

        assertNotNull(admin);
        assertEquals(user, admin.getUser());
        assertEquals(permissions, admin.getPermissions());
        assertEquals(actionsLog, admin.getActionsLog());
        assertNotNull(admin.getAdminId());
        System.out.println(admin);
    }

    @Test
    void createAdmin_InvalidUser() {
        List<Admin.Permission> permissions = List.of(Admin.Permission.MANAGE_USERS);
        Admin admin = AdminFactory.createAdmin(null, permissions, "Actions log");
        assertNull(admin);
    }

    @Test
    void createAdmin_InvalidPermissions() {
        // Test with null permissions
        Admin admin = AdminFactory.createAdmin(user, null, "Actions log");
        assertNull(admin);

        // Test with empty permissions
        admin = AdminFactory.createAdmin(user, List.of(), "Actions log");
        assertNull(admin);
    }

    @Test
    void createAdmin_WithNullActionsLog() {
        List<Admin.Permission> permissions = List.of(Admin.Permission.RESOLVE_DISPUTES);
        Admin admin = AdminFactory.createAdmin(user, permissions, null);

        assertNotNull(admin);
        assertEquals("", admin.getActionsLog()); // Should be empty string when null is passed
    }

    @Test
    void createSuperAdmin() {
        Admin admin = AdminFactory.createSuperAdmin(user);

        assertNotNull(admin);
        assertEquals(user, admin.getUser());
        assertTrue(admin.getPermissions().contains(Admin.Permission.MANAGE_USERS));
        assertTrue(admin.getPermissions().contains(Admin.Permission.VERIFY_SKILLS));
        assertTrue(admin.getPermissions().contains(Admin.Permission.RESOLVE_DISPUTES));
        assertEquals("Super admin created", admin.getActionsLog());
        System.out.println(admin);
    }

    @Test
    void createSkillVerifierAdmin() {
        Admin admin = AdminFactory.createSkillVerifierAdmin(user);

        assertNotNull(admin);
        assertEquals(user, admin.getUser());
        assertTrue(admin.getPermissions().contains(Admin.Permission.VERIFY_SKILLS));
        assertEquals(1, admin.getPermissions().size());
        assertEquals("Skill verifier created", admin.getActionsLog());
        System.out.println(admin);
    }
}