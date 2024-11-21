package com.epam.edp.demo.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import com.epam.edp.demo.dto.request.RoleDto;
import com.epam.edp.demo.model.RoleEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleServiceImpl implements RoleService {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Override
    public RoleDto getRoleByEmail(String email) {
        RoleEntity role = objectMapper.convertValue(dynamoDBMapper.load(RoleEntity.class, email), RoleEntity.class);
        return objectMapper.convertValue(role, RoleDto.class);
    }
}
