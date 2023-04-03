package com.example.certificateback.controller;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.dto.UserDTO;
import com.example.certificateback.service.interfaces.ICertificateService;
import com.example.certificateback.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    ICertificateService certificateService;

    @Autowired
    IUserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AllDTO<CertificateDTO>> getCertificateForLoggedUser()
    {
        List<CertificateDTO> certificatesDTO = certificateService.viewMyCertificates();
        AllDTO<CertificateDTO> allMyCertificates = new AllDTO<>(certificatesDTO);
        return new ResponseEntity<>(allMyCertificates, HttpStatus.OK);
    }
}