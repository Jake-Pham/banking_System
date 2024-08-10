package com.example.Banking_system.service.impl;

import com.example.Banking_system.dto.BankResponse;
import com.example.Banking_system.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
}
