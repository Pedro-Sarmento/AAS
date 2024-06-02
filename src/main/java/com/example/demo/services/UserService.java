package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserRepository;
import com.example.demo.model.User;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private static UserRepository repository;

    public static void saveUser(User user){
        repository.save(user);
    }

    public static void deleteUser(User user){
        repository.delete(user);
    }

    public static List<User> getAllUsers(){
        return repository.findAll();
    }

    public static User findByUsername(String username){
        List<User> users = getAllUsers();
        for(User user: users){
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }
}
