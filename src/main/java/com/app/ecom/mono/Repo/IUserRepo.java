package com.app.ecom.mono.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.ecom.mono.entity.UserEntity;

@Repository 
public interface IUserRepo extends JpaRepository<UserEntity, Long> {

}
