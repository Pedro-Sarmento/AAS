package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Zookeeper.ZookeeperClient;
import com.example.demo.model.User;
import com.example.demo.model.Message;
import com.example.demo.services.UserService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class ServerChat {

    private final ZookeeperClient zookeeperClient;
    private final SimpMessagingTemplate messagingTemplate;
    private AtomicInteger currentLoad = new AtomicInteger(0);
    private String serverNodePath;

    @Autowired
    public ServerChat(ZookeeperClient zookeeperClient, SimpMessagingTemplate messagingTemplate) {
        this.zookeeperClient = zookeeperClient;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            if (!zookeeperClient.nodeExists("/servers")) {
                zookeeperClient.createNode("/servers");
            }

            serverNodePath = "/servers/server-" + System.currentTimeMillis();
            zookeeperClient.registerServer(serverNodePath, currentLoad.get());
            measureSystemLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/send")
    public void sendMessage(@RequestParam String from, @RequestParam String to, @RequestParam String content) {
        String destination = "/topic/messages/" + to;
        Message message = new Message(from, content);
        updateLoad(true);
        messagingTemplate.convertAndSend(destination, message);
    }

    @PostMapping("/send-login")
    public String sendLogin(@RequestParam String username, @RequestParam String password) {
        User client = UserService.findByUsername(username);
        return (client != null) ? "Valid" : "Invalid";
    }

    @PostMapping("/send-register")
    public String sendRegister(@RequestParam String username, @RequestParam String password) {
        User usercheck = UserService.findByUsername(username);
        if (usercheck == null) {
            User newUser = new User(username, password);
            UserService.saveUser(newUser);
            return "The User has been Registered";
        } else {
            return "User already exists";
        }
    }

    private void updateLoad(boolean increase) {
        int newLoad = increase ? currentLoad.incrementAndGet() : currentLoad.decrementAndGet();
        try {
            zookeeperClient.updateServerLoad(serverNodePath, zookeeperClient.getHost(), currentLoad.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void measureSystemLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double systemLoad = osBean.getSystemLoadAverage();
        boolean increase = systemLoad > 1.0;
        updateLoad(increase);
    }
}
