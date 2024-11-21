package com.epam.edp.demo.service;


import com.epam.edp.demo.dto.request.RoleDto;

public interface RoleService {
    RoleDto getRoleByEmail(String email);
}
