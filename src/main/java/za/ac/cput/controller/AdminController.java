package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Admin;
import za.ac.cput.domain.User;
import za.ac.cput.service.AdminService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // -------------------- CRUD --------------------

    @PostMapping
    public ResponseEntity<Admin> create(@RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.create(admin));
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<Admin> read(@PathVariable String adminId) {
        return ResponseEntity.ok(adminService.read(adminId));
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<Admin> update(@PathVariable String adminId, @RequestBody Admin admin) {
        admin = new Admin.Builder().copy(admin).setAdminId(adminId).build();
        return ResponseEntity.ok(adminService.update(admin));
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Admin> delete(@PathVariable String adminId) {
        return ResponseEntity.ok(adminService.delete(adminId));
    }

    // -------------------- BUSINESS METHODS --------------------

    @PostMapping("/super/{userId}")
    public ResponseEntity<Admin> createSuperAdmin(@PathVariable String userId) {
        User user = new User.Builder().setUserId(userId).build();
        return ResponseEntity.ok(adminService.createSuperAdmin(user));
    }

    @PostMapping("/skill-verifier/{userId}")
    public ResponseEntity<Admin> createSkillVerifier(@PathVariable String userId) {
        User user = new User.Builder().setUserId(userId).build();
        return ResponseEntity.ok(adminService.createSkillVerifier(user));
    }

    @PostMapping("/{adminId}/permissions/add")
    public ResponseEntity<Admin> addPermission(@PathVariable String adminId,
                                               @RequestParam Admin.Permission permission) {
        return ResponseEntity.ok(adminService.addPermission(adminId, permission));
    }

    @PostMapping("/{adminId}/permissions/remove")
    public ResponseEntity<Admin> removePermission(@PathVariable String adminId,
                                                  @RequestParam Admin.Permission permission) {
        return ResponseEntity.ok(adminService.removePermission(adminId, permission));
    }

    @PostMapping("/{adminId}/log")
    public ResponseEntity<Admin> logAction(@PathVariable String adminId,
                                           @RequestParam String action) {
        return ResponseEntity.ok(adminService.logAction(adminId, action));
    }

    // -------------------- SEARCH / QUERY --------------------

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Optional<Admin>> findByUser(@PathVariable String userId) {
        User user = new User.Builder().setUserId(userId).build();
        return ResponseEntity.ok(adminService.findByUser(user));
    }

    @GetMapping("/by-permission")
    public ResponseEntity<List<Admin>> findByPermission(@RequestParam Admin.Permission permission) {
        return ResponseEntity.ok(adminService.findAdminsByPermission(permission));
    }

    @GetMapping("/created-between")
    public ResponseEntity<List<Admin>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(adminService.findAdminsCreatedBetween(startDate, endDate));
    }

    @GetMapping("/is-admin/{userId}")
    public ResponseEntity<Boolean> isUserAdmin(@PathVariable String userId) {
        User user = new User.Builder().setUserId(userId).build();
        return ResponseEntity.ok(adminService.isUserAdmin(user));
    }

    @GetMapping("/has-permission/{userId}")
    public ResponseEntity<Boolean> hasPermission(@PathVariable String userId,
                                                 @RequestParam Admin.Permission permission) {
        User user = new User.Builder().setUserId(userId).build();
        return ResponseEntity.ok(adminService.hasPermission(user, permission));
    }
}
