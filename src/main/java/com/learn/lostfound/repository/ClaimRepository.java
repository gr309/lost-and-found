package com.learn.lostfound.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.lostfound.entity.Claim;
import com.learn.lostfound.enums.ClaimStatus;

public interface ClaimRepository
        extends JpaRepository<Claim, Long> {

    List<Claim> findByUserId(Long userId);

    boolean existsByItemIdAndUserIdAndStatus(Long itemId, Long userId, ClaimStatus status);
}
