package hu.webuni.userservice.security;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class PemUtils {

    public static PrivateKey getPrivateKey(String path) throws Exception {

        try(PEMParser pemParser = new PEMParser(new InputStreamReader(new FileInputStream(path)))){
            PEMKeyPair pemKeyPair = (PEMKeyPair)pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            KeyPair keyPair = converter.getKeyPair(pemKeyPair);
            return keyPair.getPrivate();
        }

    }

    public static PublicKey getPublicKey(String path) throws Exception {

        try(PEMParser pemParser = new PEMParser(new InputStreamReader(new FileInputStream(path)))){
            SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo)pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPublicKey(publicKeyInfo);
        }
    }
}
