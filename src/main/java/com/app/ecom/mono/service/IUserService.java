package com.app.ecom.mono.service;

import java.util.List;
import java.util.Optional;

import com.app.ecom.mono.model.User;

public interface IUserService {

    List<User> getAllUsers();
    Optional<User>  getUserById(Long id);

    User createUser(User user);

    Optional<User> updateUser(Long id, User user);

    boolean deleteUser(Long id);

}