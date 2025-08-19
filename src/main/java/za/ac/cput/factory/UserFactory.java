package za.ac.cput.factory;

import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User Factory - Creates User entities with validation
 */
public class UserFactory {

    public static User createUser(String firstName, String lastName, String email, String password,
                                  String phoneNumber, List<User.Role> roles, User.Mode currentMode) {

        // Validate inputs using ValidationHelper
        if (ValidationHelper.isNullOrEmpty(firstName) || !ValidationHelper.isValidName(firstName)) {
            return null;
        }
        if (ValidationHelper.isNullOrEmpty(lastName) || !ValidationHelper.isValidName(lastName)) {
            return null;
        }
        if (!ValidationHelper.isValidEmail(email)) {
            return null;
        }
        if (!ValidationHelper.isValidPassword(password)) {
            return null;
        }
        if (phoneNumber != null && !ValidationHelper.isValidPhoneNumber(phoneNumber)) {
            return null;
        }
        if (roles == null || roles.isEmpty()) {
            return null;
        }

        return new User.Builder()
                .setUserId(ValidationHelper.generateId())
                .setFirstName(firstName.trim())
                .setLastName(lastName.trim())
                .setEmail(email.trim().toLowerCase())
                .setPassword(password)
                .setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null)
                .setRoles(roles)
                .setCurrentMode(currentMode)
                .setStatus(User.Status.ACTIVE)
                .setDateJoined(LocalDateTime.now())
                .build();
    }

    public static User createClientUser(String firstName, String lastName, String email, String password) {
        return createUser(firstName, lastName, email, password, null,
                List.of(User.Role.CLIENT), User.Mode.CLIENT);
    }

    public static User createWorkerUser(String firstName, String lastName, String email, String password,
                                        String phoneNumber) {
        return createUser(firstName, lastName, email, password, phoneNumber,
                List.of(User.Role.WORKER), User.Mode.WORKER);
    }

    public static User createBothRoleUser(String firstName, String lastName, String email, String password,
                                          String phoneNumber, User.Mode currentMode) {
        return createUser(firstName, lastName, email, password, phoneNumber,
                List.of(User.Role.BOTH), currentMode);
    }
}