package za.ac.cput.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.domain.User;
import za.ac.cput.factory.UserFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    private static User clientUser;
    private static User workerUser;
    private static User bothRoleUser;

    @BeforeAll
    static void setup() {
        clientUser = UserFactory.createClientUser(
                "John", "Client", "client@test.com", "Password123!"
        );

        workerUser = UserFactory.createWorkerUser(
                "Jane", "Worker", "worker@test.com", "Password123!", "0821234567"
        );

        bothRoleUser = UserFactory.createBothRoleUser(
                "Alex", "Both", "both@test.com", "Password123!", "0829876543", User.Mode.CLIENT
        );
    }

    @Test
    @Order(1)
    void create_ClientUser() {
        assertNotNull(clientUser);
        clientUser = userService.create(clientUser);
        assertNotNull(clientUser.getUserId());
        assertEquals("client@test.com", clientUser.getEmail());
        assertEquals(User.Status.ACTIVE, clientUser.getStatus());
        assertTrue(clientUser.getRoles().contains(User.Role.CLIENT));
        System.out.println("Created client user: " + clientUser);
    }

    @Test
    @Order(2)
    void create_WorkerUser() {
        assertNotNull(workerUser);
        workerUser = userService.create(workerUser);
        assertNotNull(workerUser.getUserId());
        assertEquals("worker@test.com", workerUser.getEmail());
        assertEquals("0821234567", workerUser.getPhoneNumber());
        assertTrue(workerUser.getRoles().contains(User.Role.WORKER));
        System.out.println("Created worker user: " + workerUser);
    }

    @Test
    @Order(3)
    void create_BothRoleUser() {
        assertNotNull(bothRoleUser);
        bothRoleUser = userService.create(bothRoleUser);
        assertNotNull(bothRoleUser.getUserId());
        assertEquals("both@test.com", bothRoleUser.getEmail());
        assertTrue(bothRoleUser.getRoles().contains(User.Role.BOTH));
        assertEquals(User.Mode.CLIENT, bothRoleUser.getCurrentMode());
        System.out.println("Created both-role user: " + bothRoleUser);
    }

    @Test
    @Order(4)
    void create_DuplicateEmail_ShouldFail() {
        User duplicateUser = UserFactory.createClientUser(
                "Duplicate", "User", "client@test.com", "Password123!"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            userService.create(duplicateUser);
        });
        System.out.println("Correctly prevented duplicate email creation");
    }

    @Test
    @Order(5)
    void create_InvalidUser_ShouldFail() {
        User invalidUser = new User.Builder()
                .setUserId("invalid-id")
                .setFirstName("") // Invalid empty name
                .setLastName("Test")
                .setEmail("invalid-email") // Invalid email format
                .setPassword("123") // Invalid password
                .setRoles(List.of(User.Role.CLIENT))
                .setCurrentMode(User.Mode.CLIENT)
                .setStatus(User.Status.ACTIVE)
                .setDateJoined(LocalDateTime.now())
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            userService.create(invalidUser);
        });
        System.out.println("Correctly prevented invalid user creation");
    }

    @Test
    @Order(6)
    void read() {
        User readUser = userService.read(clientUser.getUserId());
        assertNotNull(readUser);
        assertEquals(clientUser.getUserId(), readUser.getUserId());
        assertEquals(clientUser.getEmail(), readUser.getEmail());

    }

    @Test
    @Order(7)
    void update() {
        User updatedUser = new User.Builder()
                .copy(clientUser)
                .setFirstName("UpdatedJohn")
                .build();

        clientUser = userService.update(updatedUser);
        assertEquals("UpdatedJohn", clientUser.getFirstName());
        System.out.println("Updated user: " + clientUser);
    }

    @Test
    @Order(8)
    void findByEmail() {
        Optional<User> foundUser = userService.findByEmail("worker@test.com");
        assertTrue(foundUser.isPresent());
        assertEquals(workerUser.getUserId(), foundUser.get().getUserId());

    }

    @Test
    @Order(9)
    void registerUser() {
        User registeredUser = userService.registerUser(
                "New", "User", "newuser@test.com", "Password123!",
                "0823456789", List.of(User.Role.WORKER), User.Mode.WORKER
        );

        assertNotNull(registeredUser);
        assertNotNull(registeredUser.getUserId());
        assertEquals("newuser@test.com", registeredUser.getEmail());
        assertEquals("0823456789", registeredUser.getPhoneNumber());
        System.out.println("Registered new user: " + registeredUser);
    }

    @Test
    @Order(10)
    void authenticateUser_ValidCredentials() {
        boolean isAuthenticated = userService.authenticateUser("client@test.com", "Password123!");
        assertTrue(isAuthenticated);
        System.out.println("Authentication successful for valid credentials");
    }

    @Test
    @Order(11)
    void authenticateUser_InvalidCredentials() {
        boolean isAuthenticated = userService.authenticateUser("client@test.com", "WrongPassword");
        assertFalse(isAuthenticated);
        System.out.println("Authentication failed for invalid credentials");
    }

    @Test
    @Order(12)
    void switchMode() {
        // Switch both-role user from CLIENT to WORKER mode
        bothRoleUser = userService.switchMode(bothRoleUser.getUserId(), User.Mode.WORKER);
        assertEquals(User.Mode.WORKER, bothRoleUser.getCurrentMode());
        System.out.println("Switched user mode to WORKER: " + bothRoleUser);
    }

    @Test
    @Order(13)
    void switchMode_InvalidRole_ShouldFail() {
        // Try to switch client-only user to WORKER mode
        assertThrows(IllegalArgumentException.class, () -> {
            userService.switchMode(clientUser.getUserId(), User.Mode.WORKER);
        });
        System.out.println("Correctly prevented invalid mode switch");
    }

    @Test
    @Order(14)
    void suspendUser() {
        User suspendedUser = userService.suspendUser(workerUser.getUserId(), "Test suspension");
        assertEquals(User.Status.SUSPENDED, suspendedUser.getStatus());
        System.out.println("Suspended user: " + suspendedUser);
    }

    @Test
    @Order(15)
    void activateUser() {
        User activatedUser = userService.activateUser(workerUser.getUserId());
        assertEquals(User.Status.ACTIVE, activatedUser.getStatus());
        System.out.println("Activated user: " + activatedUser);
    }

    @Test
    @Order(16)
    void findActiveUsers() {
        List<User> activeUsers = userService.findActiveUsers();
        assertFalse(activeUsers.isEmpty());
        assertTrue(activeUsers.stream()
                .allMatch(user -> user.getStatus() == User.Status.ACTIVE));
        System.out.println("Active users count: " + activeUsers.size());
    }

    @Test
    @Order(17)
    void findUsersByRole() {
        List<User> clientUsers = userService.findUsersByRole(User.Role.CLIENT);
        List<User> workerUsers = userService.findUsersByRole(User.Role.WORKER);
        List<User> bothRoleUsers = userService.findUsersByRole(User.Role.BOTH);

        assertFalse(clientUsers.isEmpty());
        assertFalse(workerUsers.isEmpty());
        assertFalse(bothRoleUsers.isEmpty());

        System.out.println("Client users: " + clientUsers.size());
        System.out.println("Worker users: " + workerUsers.size());
        System.out.println("Both-role users: " + bothRoleUsers.size());
    }

    @Test
    @Order(18)
    void searchUsersByName() {
        List<User> foundUsers = userService.searchUsersByName("John");
        assertFalse(foundUsers.isEmpty());
        assertTrue(foundUsers.stream()
                .anyMatch(user -> user.getFirstName().contains("John")));
        System.out.println("Users found by name search: " + foundUsers.size());
    }

    @Test
    @Order(19)
    void emailExists() {
        boolean exists = userService.emailExists("client@test.com");
        boolean notExists = userService.emailExists("nonexistent@test.com");

        assertTrue(exists);
        assertFalse(notExists);
        System.out.println("Email existence check working correctly");
    }

    @Test
    @Order(20)
    void findUsersWithWorkerProfile() {
        List<User> usersWithProfile = userService.findUsersWithWorkerProfile();
        // Initially should be empty since we haven't created worker profiles yet
        System.out.println("Users with worker profiles: " + usersWithProfile.size());
    }

    @Test
    @Order(21)
    void delete() {
        User deletedUser = userService.delete(clientUser.getUserId());
        assertNotNull(deletedUser);
        assertEquals(clientUser.getUserId(), deletedUser.getUserId());

        // Verify user no longer exists
        assertThrows(RuntimeException.class, () -> {
            userService.read(clientUser.getUserId());
        });
        System.out.println("Deleted user: " + deletedUser.getUserId());
    }
}