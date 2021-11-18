package com.hust.qms.service;

import com.hust.qms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckRolesService {
    @Autowired
    private BaseService baseService;

    private boolean isMatchRole(List<String> currentRole, String allowRole) {
        String[] roleArr = allowRole.split(",");
        if (currentRole != null && !currentRole.isEmpty()) {
            for (String r : currentRole) {
                for (int i = 0; i < roleArr.length; i++) {
                    if (r.equals(roleArr[i])) return true;
                }
            }
        }
        return false;
    }

    public boolean authorizeRole(String role) {
        List<String> roles = baseService.getCurrentRoles();
        if (roles.size() > 0 && isMatchRole(roles, role)) {
            return true;
        }
        return false;
    }

    public boolean authorizeIdAndRole(Long userId, String roleStr) {
        Long currId = baseService.getCurrentId();
        List<String> roles = baseService.getCurrentRoles();

        if (currId.equals(userId) || isMatchRole(roles, roleStr)) {
            return true;
        }
        return false;
    }
}
