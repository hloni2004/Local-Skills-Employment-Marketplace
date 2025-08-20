package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Admin;
import za.ac.cput.domain.User;
import za.ac.cput.repository.AdminRepository;
import za.ac.cput.factory.AdminFactory;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminService implements IService<Admin, String> {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Admin create(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }

        // Check if user is already an admin
        if (adminRepository.existsByUser(admin.getUser())) {
            throw new IllegalStateException("User is already an admin");
        }

        return adminRepository.save(admin);
    }

    @Override
    public Admin read(String adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));
    }

    @Override
    public Admin update(Admin admin) {
        if (!adminRepository.existsById(admin.getAdminId())) {
            throw new RuntimeException("Admin not found");
        }
        return adminRepository.save(admin);
    }

    @Override
    public Admin delete(String adminId) {
        Admin admin = read(adminId);
        adminRepository.deleteById(adminId);
        return admin;
    }

    // Business Logic Methods
    public Admin createSuperAdmin(User user) {
        Admin admin = AdminFactory.createSuperAdmin(user);
        if (admin == null) {
            throw new IllegalArgumentException("Invalid admin creation data");
        }
        return create(admin);
    }

    public Admin createSkillVerifier(User user) {
        Admin admin = AdminFactory.createSkillVerifierAdmin(user);
        if (admin == null) {
            throw new IllegalArgumentException("Invalid admin creation data");
        }
        return create(admin);
    }

    public Admin createCustomAdmin(User user, List<Admin.Permission> permissions, String actionsLog) {
        Admin admin = AdminFactory.createAdmin(user, permissions, actionsLog);
        if (admin == null) {
            throw new IllegalArgumentException("Invalid admin creation data");
        }
        return create(admin);
    }

    public Admin addPermission(String adminId, Admin.Permission permission) {
        Admin admin = read(adminId);
        List<Admin.Permission> currentPermissions = new java.util.ArrayList<>(admin.getPermissions());

        if (!currentPermissions.contains(permission)) {
            currentPermissions.add(permission);
            return update(new Admin.Builder()
                    .copy(admin)
                    .setPermissions(currentPermissions)
                    .build());
        }

        return admin;
    }

    public Admin removePermission(String adminId, Admin.Permission permission) {
        Admin admin = read(adminId);
        List<Admin.Permission> currentPermissions = new java.util.ArrayList<>(admin.getPermissions());

        if (currentPermissions.contains(permission)) {
            currentPermissions.remove(permission);
            return update(new Admin.Builder()
                    .copy(admin)
                    .setPermissions(currentPermissions)
                    .build());
        }

        return admin;
    }

    public Admin logAction(String adminId, String action) {
        Admin admin = read(adminId);
        String currentLog = admin.getActionsLog() != null ? admin.getActionsLog() : "";
        String newLogEntry = LocalDateTime.now() + ": " + action;
        String updatedLog = currentLog.isEmpty() ? newLogEntry : currentLog + "\n" + newLogEntry;

        return update(new Admin.Builder()
                .copy(admin)
                .setActionsLog(updatedLog)
                .build());
    }

    public Optional<Admin> findByUser(User user) {
        return adminRepository.findByUser(user);
    }

    public List<Admin> findAdminsByPermission(Admin.Permission permission) {
        return adminRepository.findByPermission(permission);
    }

    public List<Admin> findAdminsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return adminRepository.findByDateCreatedBetween(startDate, endDate);
    }

    public boolean isUserAdmin(User user) {
        return adminRepository.existsByUser(user);
    }

    public boolean hasPermission(User user, Admin.Permission permission) {
        Optional<Admin> admin = findByUser(user);
        return admin.isPresent() && admin.get().getPermissions().contains(permission);
    }
}