package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.User;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.factory.UserFactory;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements IService<User, String> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(User user) {
        ValidationHelper.ValidationResult result = ValidationHelper.validateUser(user);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid user data: " + result.getErrorMessage());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Hash password before saving
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        User userToSave = new User.Builder()
                .copy(user)
                .setPassword(hashedPassword)
                .build();

        return userRepository.save(userToSave);
    }

    @Override
    public User read(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Override
    public User update(User user) {
        if (!userRepository.existsById(user.getUserId())) {
            throw new RuntimeException("User not found");
        }

        ValidationHelper.ValidationResult result = ValidationHelper.validateUser(user);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Invalid user data: " + result.getErrorMessage());
        }

        return userRepository.save(user);
    }

    @Override
    public User delete(String userId) {
        User user = read(userId);
        userRepository.deleteById(userId);
        return user;
    }

    // Business Logic Methods
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(String firstName, String lastName, String email, String password,
                             String phoneNumber, List<User.Role> roles, User.Mode currentMode) {
        User newUser = UserFactory.createUser(firstName, lastName, email, password, phoneNumber, roles, currentMode);
        if (newUser == null) {
            throw new IllegalArgumentException("Invalid user registration data");
        }
        return create(newUser);
    }

    public boolean authenticateUser(String email, String rawPassword) {
        Optional<User> user = findByEmail(email);
        return user.isPresent() && passwordEncoder.matches(rawPassword, user.get().getPassword());
    }

    public User switchMode(String userId, User.Mode newMode) {
        User user = read(userId);

        // Validate user can switch to this mode
        if (!user.getRoles().contains(User.Role.BOTH) &&
                !user.getRoles().contains(newMode == User.Mode.CLIENT ? User.Role.CLIENT : User.Role.WORKER)) {
            throw new IllegalArgumentException("User doesn't have permission for this mode");
        }

        return update(new User.Builder().copy(user).setCurrentMode(newMode).build());
    }

    public User suspendUser(String userId, String reason) {
        User user = read(userId);
        return update(new User.Builder().copy(user).setStatus(User.Status.SUSPENDED).build());
    }

    public User activateUser(String userId) {
        User user = read(userId);
        return update(new User.Builder().copy(user).setStatus(User.Status.ACTIVE).build());
    }

    public List<User> findActiveUsers() {
        return userRepository.findActiveUsers();
    }

    public List<User> findUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findUsersWithWorkerProfile() {
        return userRepository.findUsersWithWorkerProfile();
    }
}
