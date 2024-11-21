package com.epam.edp.demo.repository;


import com.epam.edp.demo.model.LoginEntity;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@EnableScan
@Repository
public interface UserRepository extends CrudRepository<LoginEntity, String> {
     Optional<LoginEntity> findByEmail(String email);
}
