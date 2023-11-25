package com.mycode.datageniehack.datageniehack.Controller;

import com.mycode.datageniehack.datageniehack.Entity.User;
import com.mycode.datageniehack.datageniehack.Repository.UserRepository;
import com.mycode.datageniehack.datageniehack.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UserService userService; // Assuming you have a UserService

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/user")
    public List<User> getAllUsers(){
        return userService.findAll();
    }

    @GetMapping("/user/{id}")
    public User show(@PathVariable String id){
        long userId = Long.parseLong(id);
        return userService.getUserById(userId);
    }

//    @PostMapping("/user/search")
//    public List<User> search(@RequestBody Map<String, String> body){
//        String searchTerm = body.get("text");
//        return userService.findByTitleContainingOrContentContaining(searchTerm, searchTerm);
//    }
//
    @PostMapping("/user")
    public User create(@RequestBody User user){

        return userService.saveUser(user);
    }
//    @PostMapping("/testuser")
//    public void testuser(@RequestBody User user){
//        System.out.println("********************************");
//        System.out.println(user.getFirstName());
//    }
////
//    @PutMapping("/user/{id}")
//    public User update(@PathVariable String id, @RequestBody Map<String, String> body){
//        int userId = Integer.parseInt(id);
//        // getting blog
//       User user = userRepository.findById(userId);
//        user.setTitle(body.get("title"));
//        user.setContent(body.get("content"));
//        return userRepository.save(blog);
//    }
//
//    @DeleteMapping("user/{id}")
//    public boolean delete(@PathVariable String id){
//        int blogId = Integer.parseInt(id);
//        userRepository.deleteById(UserId);
//        return true;
//    }
    @PostMapping("/validate")
    public String validateLogin(@RequestParam String username, @RequestParam String password) {
        // Check if the provided username and password exist in the database
        User user = userService.findByUsername(username);
        System.out.println(user.toString());

        if (user != null && user.getPassword().equals(password)) {
            return "Login successful! Welcome, " + user.getUsername();
        } else {
            return "Invalid username or password. Please try again.";
        }
    }

    // Additional endpoints can be added for registration, logout, etc. as needed
}
