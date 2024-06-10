package com.example.demo.controller;

import com.example.demo.Zookeeper.ZookeeperClient;
import com.example.demo.model.CustomRestTemplateConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.springframework.web.client.RestTemplate;


@RestController
public class ClientChat{

    private final ZookeeperClient zooKeeperClient;
    private final RestTemplate restTemplate;

    @Autowired
    public ClientChat(ZookeeperClient zooKeeperClient) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, CertificateException {
        this.zooKeeperClient = zooKeeperClient;
        this.restTemplate = CustomRestTemplateConfiguration.restTemplate();
    }

    /*@PostMapping("/send-message")
    public String sendMessage(@RequestParam String from, @RequestParam String to, @RequestParam String content) {
        try {
            String bestServer = zooKeeperClient.selectBestServer();
            String serverUrl = "http://" + bestServer + ":8080/send";
            restTemplate.postForObject(serverUrl, null, String.class, from, to, content);
            return "Message sent to " + to + " via server " + bestServer;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }*/

    //Method that will receive the parameters from the front-end and send them to the zookeeperclient
    @PostMapping("/login-user")
    public String sendLogin(@RequestBody String username, @RequestBody String password) {
        try {
            String bestServer = zooKeeperClient.selectBestServer();
            String serverUrl = "https://" + bestServer + ":8081/send-login";
            return restTemplate.postForObject(serverUrl, null, String.class, username,password);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/register-user")
    public String sendRegister(@RequestParam String username, @RequestParam String password) {
        try {
            String bestServer = zooKeeperClient.selectBestServer();
            String serverUrl = "https://" + bestServer + ":8081/send-register";

            /*MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("username", username);
            requestBody.add("password", password);*/

            /*HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);*/

/*
            return restTemplate.postForObject(serverUrl, requestEntity, String.class);
*/

            return restTemplate.postForObject(serverUrl, null, String.class, username,password);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
