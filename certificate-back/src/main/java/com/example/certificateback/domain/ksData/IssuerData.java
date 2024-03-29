package com.example.certificateback.domain.ksData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.PrivateKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuerData {
    private X500Name x500name;
    private PrivateKey privateKey;

}
