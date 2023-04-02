package com.example.certificateback.controller;

import com.example.certificateback.domain.User;
import com.example.certificateback.dto.RegistrationDTO;
import com.example.certificateback.service.interfaces.IUserService;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


public class UserController {

    @Autowired
    IUserService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@RequestBody RegistrationDTO userDTO) {
        User user = service.register(userDTO);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

}