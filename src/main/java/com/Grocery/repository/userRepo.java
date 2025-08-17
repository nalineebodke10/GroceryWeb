package com.Grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Grocery.model.user;

public interface userRepo extends JpaRepository<user, Long> {

    user findByMobileAndPassword(String mobile, String password);

    user findByMobile(String mobile); 

}
