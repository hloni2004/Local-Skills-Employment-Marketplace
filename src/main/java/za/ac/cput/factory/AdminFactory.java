package za.ac.cput.factory;

import za.ac.cput.domain.Admin;
import za.ac.cput.domain.*;
import za.ac.cput.util.ValidationHelper;

import java.time.LocalDateTime;
import java.util.List;

public class AdminFactory {

    public static Admin createAdmin(User user, List<Admin.Permission> permissions, String actionsLog) {

        // Validate inputs
        if (user == null) {
            return null;
        }
        if (permissions == null || permissions.isEmpty()) {
            return null;
        }

        return new Admin.Builder()
                .setAdminId(ValidationHelper.generateId())
                .setUser(user)
                .setPermissions(permissions)
                .setActionsLog(actionsLog != null ? actionsLog.trim() : "")
                .setDateCreated(LocalDateTime.now()) // Add missing dateCreated
                .build();
    }

    public static Admin createSuperAdmin(User user) {
        return createAdmin(user, List.of(
                Admin.Permission.MANAGE_USERS,
                Admin.Permission.VERIFY_SKILLS,
                Admin.Permission.RESOLVE_DISPUTES,
                Admin.Permission.MANAGE_PAYMENTS,
                Admin.Permission.VIEW_REPORTS
        ), "Super admin created");
    }

    public static Admin createSkillVerifierAdmin(User user) {
        return createAdmin(user, List.of(Admin.Permission.VERIFY_SKILLS), "Skill verifier created");
    }
}