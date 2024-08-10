package com.example.Banking_system.service.impl;

import com.example.Banking_system.dto.AccountInfo;
import com.example.Banking_system.dto.BankResponse;
import com.example.Banking_system.dto.EmailDetails;
import com.example.Banking_system.dto.UserRequest;
import com.example.Banking_system.entity.User;
import com.example.Banking_system.repository.UserRepository;
import com.example.Banking_system.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Create an account - saving a new user into the db
         * Check if user already has an account
         */
        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();
        User saveduser = userRepository.save(newUser);
        //Send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(saveduser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulation! Your account has been successfully Created. \nYour Account Detail: \n" +
                        "Account Name: " + saveduser.getFirstName() + " " + saveduser.getLastName() + " " + saveduser.getOtherName() +
                        "\nAccount Number:" + saveduser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(saveduser.getAccountBalance())
                        .accountNumber(saveduser.getAccountNumber())
                        .accountName(saveduser.getFirstName() + " " + saveduser.getLastName() + " " + saveduser.getOtherName())
                        .build())
                .build();
    }
}
