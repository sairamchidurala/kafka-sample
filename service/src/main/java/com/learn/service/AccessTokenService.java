package com.learn.service;

import com.learn.Model.AccessToken;
import com.learn.Repository.AccessTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccessTokenService {

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    // Create or update an access token
    public AccessToken saveAccessToken(String sourceId, String token, String name) {
        AccessToken accessToken = new AccessToken();
        accessToken.setSourceId(sourceId);
        accessToken.setToken(token);
        accessToken.setCreatedOn(LocalDateTime.now());
        accessToken.setUpdatedOn(LocalDateTime.now());
        accessToken.setActive(true);  // Set to true or manage according to your logic
        accessToken.setName(name);

        return accessTokenRepository.save(accessToken);
    }

    public String getActiveAccessTokenBySourceId(String sourceId) {
//        AccessToken token = accessTokenRepository.findBySourceIdAndIsActiveTrue(sourceId);
        AccessToken token = accessTokenRepository.findBySourceIdAndActiveStatus(sourceId);
        if (token == null) {
            throw new RuntimeException("No active access token found for sourceId: " + sourceId);
        }
        return token.getToken();  // Assuming `getToken()` returns the access token string
    }

    // Get all active tokens
    public List<AccessToken> getAllActiveTokens() {
        return accessTokenRepository.findByActive(true);
    }
}
