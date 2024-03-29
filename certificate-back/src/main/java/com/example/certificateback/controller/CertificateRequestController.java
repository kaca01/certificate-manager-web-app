package com.example.certificateback.controller;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.dto.ErrorDTO;
import com.example.certificateback.service.implementation.RecaptchaService;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "https://localhost:4200")
@RequestMapping("/api/certificate-request")
public class CertificateRequestController {

    @Autowired
    RecaptchaService recaptchaService;
    @Autowired
    ICertificateRequestService service;

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AllDTO<CertificateRequestDTO>> getUserRequests() {
        return new ResponseEntity<>(service.getUserRequests(), HttpStatus.OK);
    }

    @GetMapping(value = "/issuer", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<AllDTO<CertificateRequestDTO>> getRequestsBasedOnIssuer() {
        return new ResponseEntity<>(service.getRequestsBasedOnIssuer(), HttpStatus.OK);
    }

    @GetMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AllDTO<CertificateRequestDTO>> getAllRequests() {
        return new ResponseEntity<>(service.getAllRequests(), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CertificateRequestDTO> create(@RequestBody CertificateRequestDTO certificateRequestDTO,
                                                        @RequestHeader("recaptcha") String recaptcha) {
        recaptchaService.checkResponse(recaptcha);
        return new ResponseEntity<>(service.insert(certificateRequestDTO), HttpStatus.CREATED);
    }

    @PutMapping(value = "/accept/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CertificateDTO> acceptRequest(@PathVariable Long id) {
        CertificateDTO certificate = service.acceptRequest(id);
        return new ResponseEntity<>(certificate, HttpStatus.OK);
    }

    @PutMapping(value = "/refuse/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CertificateRequestDTO> refuseRequest(@PathVariable Long id, @RequestBody ErrorDTO reason) {
        CertificateRequestDTO request = service.refuseRequest(id, reason);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }
}
