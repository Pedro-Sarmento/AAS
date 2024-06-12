package com.example.demo.controller;

import com.example.demo.Zookeeper.ZookeeperClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;


@RestController
public class ClientChat{

    private final ZookeeperClient zooKeeperClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public ClientChat(ZookeeperClient zooKeeperClient) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, CertificateException {
        this.zooKeeperClient = zooKeeperClient;
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
    public ResponseEntity<String> sendLogin(@RequestBody String username, @RequestBody String password) {
        try {
            String bestServer = zooKeeperClient.selectBestServer();
            String serverUrl = "https://" + bestServer + ":8081/send-login";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Convert the requestBody to a URL-encoded format
            StringBuilder encodedBody = new StringBuilder();
            for (Map.Entry<String, String> entry : requestBody.entrySet()) {
                if (encodedBody.length() > 0) {
                    encodedBody.append("&");
                }
                encodedBody.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            HttpEntity<String> requestEntity = new HttpEntity<>(encodedBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @PostMapping("/register-user")
    public ResponseEntity<String> sendRegister(@RequestParam String username, @RequestParam String password) {
        try {
            String bestServer = zooKeeperClient.selectBestServer();
            String serverUrl = "https://" + bestServer + ":8081/send-register";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Convert the requestBody to a URL-encoded format
            StringBuilder encodedBody = new StringBuilder();
            for (Map.Entry<String, String> entry : requestBody.entrySet()) {
                if (encodedBody.length() > 0) {
                    encodedBody.append("&");
                }
                encodedBody.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            HttpEntity<String> requestEntity = new HttpEntity<>(encodedBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


   /* @PostMapping("/register-user")
    public String sendRegister(@RequestParam String username, @RequestParam String password) {
        try {
            String bestServer = zooKeeperClient.selectBestServer();
            String serverUrl = "https://" + bestServer + ":8081/send-register";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Convert the requestBody to a URL-encoded format
            StringBuilder encodedBody = new StringBuilder();
            for (Map.Entry<String, String> entry : requestBody.entrySet()) {
                if (encodedBody.length() > 0) {
                    encodedBody.append("&");
                }
                encodedBody.append(entry.getKey()).append("=").append(entry.getValue());
            }

            HttpEntity<String> requestEntity = new HttpEntity<>(encodedBody.toString(), headers);
            restTemplate.postForObject(serverUrl, requestEntity, String.class);
            return "Valid";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }*/

}
