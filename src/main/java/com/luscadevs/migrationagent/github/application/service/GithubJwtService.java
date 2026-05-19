package com.luscadevs.migrationagent.github.application.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;

@Service
public class GithubJwtService {

    @Value("${github.app.id}")
    private String appId;

    @Value("${github.app.private-key-path}")
    private String privateKeyPath;

    public String generateJwt() {

        try {

            PrivateKey privateKey = loadPrivateKey();

            Instant now = Instant.now();

            return Jwts.builder()
                    .issuer(appId)
                    .issuedAt(Date.from(now.minusSeconds(60)))
                    .expiration(Date.from(now.plusSeconds(600)))
                    .signWith(privateKey, Jwts.SIG.RS256)
                    .compact();

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Error generating GitHub JWT",
                    ex);
        }
    }

    private PrivateKey loadPrivateKey() throws Exception {

        try (PEMParser pemParser = new PEMParser(
                Files.newBufferedReader(
                        Path.of(privateKeyPath)))) {

            Object object = pemParser.readObject();

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            if (object instanceof PEMKeyPair keyPair) {
                return converter.getKeyPair(keyPair)
                        .getPrivate();
            }

            if (object instanceof PrivateKeyInfo keyInfo) {
                return converter.getPrivateKey(keyInfo);
            }

            throw new IllegalStateException(
                    "Unsupported private key format");
        }
    }
}