package com.learn.Repository;

import com.learn.Model.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    // You can define custom query methods if needed, for example:
    Optional<AccessToken> findBySourceId(String sourceId);

//    AccessToken findBySourceIdAndIsActiveTrue(String sourceId);
    @Query("SELECT a FROM AccessToken a WHERE a.sourceId = ?1 AND a.active = true")
    AccessToken findBySourceIdAndActiveStatus(String sourceId);

    // You could also find by name or active status, depending on your needs
    List<AccessToken> findByActive(boolean active);
}
