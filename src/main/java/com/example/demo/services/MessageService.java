package com.example.demo.services;

import com.example.demo.model.Message;
import com.example.demo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository repository;

    public void saveMessage(Message message) {
        repository.save(message);
    }
    public List<Message> findAll() {
        return repository.findAll();
    }
}
