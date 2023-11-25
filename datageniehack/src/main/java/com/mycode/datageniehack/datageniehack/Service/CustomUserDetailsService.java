//package com.mycode.datageniehack.datageniehack.Service;
//
//import com.mycode.datageniehack.datageniehack.Entity.User;
//import com.mycode.datageniehack.datageniehack.Repository.UserRepository;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    public CustomUserDetailsService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username){
//        try{
//            User user = userRepository.findByUsername(username);
//            return user;
//        }catch (Exception e){
//            new Exception("User not found with username: " + username);
//        }
//        return null;
//    }
//}
