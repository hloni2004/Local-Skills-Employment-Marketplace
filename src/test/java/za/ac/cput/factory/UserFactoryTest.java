package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.User;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    @Test
    void createUser() {
        // Test valid user creation
        User user = UserFactory.createUser("John", "Doe", "john.doe@email.com",
                "Password123!", "0821234567", List.of(User.Role.CLIENT), User.Mode.CLIENT);

        assertNotNull(user);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@email.com", user.getEmail());
        assertEquals("Password123!", user.getPassword());
        assertEquals("0821234567", user.getPhoneNumber());
        assertEquals(List.of(User.Role.CLIENT), user.getRoles());
        assertEquals(User.Mode.CLIENT, user.getCurrentMode());
        assertEquals(User.Status.ACTIVE, user.getStatus());
        assertNotNull(user.getUserId());
        assertNotNull(user.getDateJoined());
        System.out.print(user);
    }

    @Test
    void createUser_InvalidFirstName() {
        // Test with null first name
        User user = UserFactory.createUser(null, "Doe", "john.doe@email.com",
                "Password123!", "0821234567", List.of(User.Role.CLIENT), User.Mode.CLIENT);
        assertNull(user);

        // Test with empty first name
        user = UserFactory.createUser("", "Doe", "john.doe@email.com",
                "Password123!", "0821234567", List.of(User.Role.CLIENT), User.Mode.CLIENT);
        assertNull(user);

        // Test with invalid characters in first name
        user = UserFactory.createUser("John123", "Doe", "john.doe@email.com",
                "Password123!", "0821234567", List.of(User.Role.CLIENT), User.Mode.CLIENT);
        assertNull(user);
    }

    @Test
    void createUser_InvalidEmail() {
        User user = UserFactory.createUser("John", "Doe", "invalid-email",
                "Password123!", "0821234567", List.of(User.Role.CLIENT), User.Mode.CLIENT);
        assertNull(user);

        user = UserFactory.createUser("John", "Doe", "",
                "Password123!", "0821234567", List.of(User.Role.CLIENT), User.Mode.CLIENT);
        assertNull(user);
    }

    @Test
    void createUser_InvalidPassword() {
        // Test weak password
        User user = UserFactory.createUser("John", "Doe", "john.doe@email.com",
                "password", "0821234567", List.of(User.Role.CLIENT), User.Mode.CLIENT);
        assertNull(user);

        // Test password without special character
        user = UserFactory.createUser("John", "Doe", "john.doe@email.com",
                "Password123", "0821234567", List.of(User.Role.CLIENT), User.Mode.CLIENT);
        assertNull(user);
    }

    @Test
    void createUser_InvalidPhoneNumber() {
        User user = UserFactory.createUser("John", "Doe", "john.doe@email.com",
                "Password123!", "123", List.of(User.Role.CLIENT), User.Mode.CLIENT);
        assertNull(user);
    }

    @Test
    void createUser_NoRoles() {
        User user = UserFactory.createUser("John", "Doe", "john.doe@email.com",
                "Password123!", "0821234567", null, User.Mode.CLIENT);
        assertNull(user);

        user = UserFactory.createUser("John", "Doe", "john.doe@email.com",
                "Password123!", "0821234567", List.of(), User.Mode.CLIENT);
        assertNull(user);
    }

    @Test
    void createClientUser() {
        User user = UserFactory.createClientUser("Jane", "Smith", "jane.smith@email.com", "Password123!");

        assertNotNull(user);
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("jane.smith@email.com", user.getEmail());
        assertEquals(List.of(User.Role.CLIENT), user.getRoles());
        assertEquals(User.Mode.CLIENT, user.getCurrentMode());
        assertNull(user.getPhoneNumber());
    }

    @Test
    void createWorkerUser() {
        User user = UserFactory.createWorkerUser("Mike", "Johnson", "mike.johnson@email.com",
                "Password123!", "0827654321");

        assertNotNull(user);
        assertEquals("Mike", user.getFirstName());
        assertEquals("Johnson", user.getLastName());
        assertEquals("mike.johnson@email.com", user.getEmail());
        assertEquals("0827654321", user.getPhoneNumber());
        assertEquals(List.of(User.Role.WORKER), user.getRoles());
        assertEquals(User.Mode.WORKER, user.getCurrentMode());
    }

    @Test
    void createBothRoleUser() {
        User user = UserFactory.createBothRoleUser("Alex", "Brown", "alex.brown@email.com",
                "Password123!", "0823456789", User.Mode.CLIENT);

        assertNotNull(user);
        assertEquals("Alex", user.getFirstName());
        assertEquals("Brown", user.getLastName());
        assertEquals("alex.brown@email.com", user.getEmail());
        assertEquals("0823456789", user.getPhoneNumber());
        assertEquals(List.of(User.Role.BOTH), user.getRoles());
        assertEquals(User.Mode.CLIENT, user.getCurrentMode());
    }
}