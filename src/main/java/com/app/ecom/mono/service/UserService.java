package com.app.ecom.mono.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.ecom.mono.Repo.IUserRepo;
import com.app.ecom.mono.entity.AddressEntity;
import com.app.ecom.mono.entity.UserEntity;
import com.app.ecom.mono.model.Address;
import com.app.ecom.mono.model.User;
import com.app.ecom.mono.model.UserRole;

@Service
public class UserService implements IUserService {

    private final IUserRepo userRepo;

    public UserService(IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::toUser)
                .toList();
    }

    @Override
    public User createUser(User user) {
        UserEntity entity = toEntity(user);
        entity.setId(null);
        return toUser(userRepo.save(entity));
    }

    @Override
    public Optional<User> updateUser(Long id, User user) {
        if (!userRepo.existsById(id)) {
            return Optional.empty();
        }

        UserEntity entity = toEntity(user);
        entity.setId(id);
        return Optional.of(toUser(userRepo.save(entity)));
    }

    @Override
    public boolean deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            return false;
        }

        userRepo.deleteById(id);
        return true;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id).map(this::toUser);
    }

    private User toUser(UserEntity entity) {
        User user = new User();
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setPhone(entity.getPhone());
        user.setUserRole(entity.getUserRole());
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getAddress() != null) {
            Address address = new Address();
            address.setStreet(entity.getAddress().getStreet());
            address.setCity(entity.getAddress().getCity());
            address.setState(entity.getAddress().getState());
            address.setCounty(entity.getAddress().getCounty());
            address.setZipcode(entity.getAddress().getZipcode());
            user.setAddress(address);
        }

        return user;
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setPhone(user.getPhone());
        entity.setUserRole(user.getUserRole() == null ? UserRole.CUSTOMER : user.getUserRole());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        if (user.getAddress() != null) {
            AddressEntity addressEntity = new AddressEntity();
            addressEntity.setStreet(user.getAddress().getStreet());
            addressEntity.setCity(user.getAddress().getCity());
            addressEntity.setState(user.getAddress().getState());
            addressEntity.setCounty(user.getAddress().getCounty());
            addressEntity.setZipcode(user.getAddress().getZipcode());
            addressEntity.setUser(entity);
            entity.setAddress(addressEntity);
        }

        return entity;
    }

}
