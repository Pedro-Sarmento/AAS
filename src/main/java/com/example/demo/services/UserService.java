package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserRepository;
import com.example.demo.model.User;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public void saveUser(User user){
        repository.save(user);
    }

    public void deleteUser(User user){
        repository.delete(user);
    }

    public List<User> getAllUsers(){
        return repository.findAll();
    }

    public User findByUsername(String username){
        /*List<User> users = getAllUsers();
        for(User user: users){
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;*/
        return repository.findByUsername(username);
    }
}
