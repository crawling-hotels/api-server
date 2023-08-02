package com.example.demo.user.service;

import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private static final Map<String, UserDetails> USERS = new HashMap<>();

    static {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserDetails user1 = org.springframework.security.core.userdetails.User.withUsername("user1")
                .password(passwordEncoder.encode("user1Pass"))
                .roles("USER")
                .build();

        USERS.put("user1", user1);

        User user2 = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("user2Pass"))
                .role("USER")
                .build();
        USERS.put("user2", user2);

        User user3 = User.builder()
                .username("user3")
                .password(passwordEncoder.encode("user3Pass"))
                .role("USER")
                .build();
        USERS.put("user3", user3);

//        USERS.put("user2", new CustomUserDetails(User.builder()
//                .username("user2")
//                .password(passwordEncoder.encode("password2"))
//                .role(Role.ROLE_ADMIN)
//                .build()));
    }

    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username + " " + USERS.get(username));


//        return new CustomUserDetails(USERS.get(username).getUser());
        return USERS.get(username);


//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다."));

//        return new UserAdapter(user);
    }

}
