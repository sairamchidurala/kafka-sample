package com.learn.Repository;

import com.learn.Model.AccessToken;
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

    // Find an access token by sourceId
    public Optional<AccessToken> getAccessTokenBySourceId(String sourceId) {
        return accessTokenRepository.findBySourceId(sourceId);
    }

    // Get all active tokens
    public List<AccessToken> getAllActiveTokens() {
        return accessTokenRepository.findByActive(true);
    }
}
