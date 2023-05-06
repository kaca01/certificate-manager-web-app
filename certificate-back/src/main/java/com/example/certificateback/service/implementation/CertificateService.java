package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.User;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.enumeration.RequestType;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.repository.ICertificateRepository;
import com.example.certificateback.repository.ICertificateRequestRepository;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    IUserRepository userRepository;

    @Autowired
    ICertificateRepository certificateRepository;

    @Autowired
    ICertificateRequestRepository certificateRequestRepository;

    @Override
    public List<CertificateDTO> getAllCertificates() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = userRepository.findByEmail(authentication.getName()).orElse(null);

        if(loggedUser != null) {
            List<Certificate> certificates = certificateRepository.findAll();
            List<CertificateDTO> certificatesDTO = new ArrayList<>();
            for (Certificate c : certificates){
                certificatesDTO.add(new CertificateDTO(c.getValidTo(), c.getValidFrom(), c.getSubject(), c.getCertificateType(),
                                    c.getSerialNumber()));
            }
            return certificatesDTO;
        }
        return null;
    }

    @Override
    public Boolean checkingValidation(String serialNumber) {
        Certificate certificate = certificateRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new NotFoundException("Certificate with that serial number does not exist"));

        if(certificate != null)
            return certificate.isValid() && !certificate.isWithdrawn();

        return false;
    }

    @Override
    public AllDTO<CertificateDTO> getIssuers() {
        List<CertificateDTO> certificates = getAllCertificates();
        certificates.removeIf(certificate -> Objects.equals(certificate.getCertificateType(), "END"));
        AllDTO<CertificateDTO> certificatesDTO = new AllDTO<>(certificates);
        return certificatesDTO;
    }

    @Override
    public CertificateDTO invalidate(String serialNumber, String withdrawnReason) {
        Certificate certificate = certificateRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new NotFoundException("Certificate with that serial number does not exist"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (loggedUser != certificate.getSubject()) throw new BadRequestException("Only certificate owner can withdraw the certificate.");

        certificate.setWithdrawn(true);
        certificate.setWithdrawnReason(withdrawnReason);
        certificateRepository.save(certificate);
        invalidateChildren(certificate);
        certificateRepository.flush();
        certificateRequestRepository.flush();
        return new CertificateDTO(certificate);
    }

    private void invalidateChildren(Certificate certificate) {
        if (!certificate.isWithdrawn()) {
            certificate.setWithdrawn(true);
            certificate.setWithdrawnReason("Issuer is withdrawn. This is refused by the system.");
            certificateRepository.save(certificate);
        }

        // the following lines is refusing all active cert requests with issuer that is withdrawn
        List<CertificateRequest> activeRequests = certificateRequestRepository.
                findByIssuerAndRequestType(certificate, RequestType.ACTIVE);

        for (CertificateRequest request : activeRequests) {
            request.setRequestType(RequestType.REFUSED);
            request.setRefusalReason("Issuer is withdrawn. This is refused by the system.");
            certificateRequestRepository.save(request);
        }

        List<Certificate> childrenCertificates = certificateRepository.findByIssuerSerialNumber(certificate.getSerialNumber());
        for (Certificate child : childrenCertificates) {
            invalidateChildren(child);
        }
    }
}
