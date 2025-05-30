package com.ayd.employee_service.auth.jwt.ports;

import java.util.List;

import com.ayd.employee_service.users.models.User;

public interface ForJwtGenerator {
    /**
     * Genera un token jwt con los permisos del usuario
     * 
     * @param user
     * @param permissions
     * @return
     */
    public String generateToken(User user, List<String> permissions);
}
