package com.mycode.datageniehack.datageniehack.Controller;

import com.mycode.datageniehack.datageniehack.Entity.User;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class YourController {

    @PostMapping("/yourEndpoint")
    public String handlePostRequest(@RequestBody User data) {
        // Your logic to handle the received data
        // Process 'data' received from the frontend


        return JSONObject.quote("Data received successfully");
    }
}