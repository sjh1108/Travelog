package com.ssafy.travelog.model.mapper;

import com.ssafy.travelog.model.dto.Account;

public interface UserMapper {
    Account findUser(Account account);
    void save(Account account);
}
