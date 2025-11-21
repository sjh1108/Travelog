package com.ssafy.travelog.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.travelog.model.dto.Account;
import com.ssafy.travelog.model.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService{

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = new Account();
        account.setId(username);
        account = userMapper.findUser(account);
        if(account != null){
            List<GrantedAuthority> authorities = new ArrayList<>();
            return new User(account.getId(), account.getPasswd(), authorities);
        }
        return null;
    }

    @Transactional
    public boolean join(String userId, String userPwd) {
        Account checkUser = new Account();
        checkUser.setId(userId);

        if (userMapper.findUser(checkUser) != null){
            return false;
        }
        Account newUser = new Account();
        newUser.setId(userId);
        newUser.setPasswd(encoder.encode(userPwd));
        userMapper.save(newUser);
        return true;
    }

}