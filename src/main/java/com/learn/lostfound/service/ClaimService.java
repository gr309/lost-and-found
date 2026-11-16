package com.learn.lostfound.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.learn.lostfound.dto.ClaimRequest;
import com.learn.lostfound.dto.ClaimResponse;

import com.learn.lostfound.entity.Claim;
import com.learn.lostfound.entity.Item;
import com.learn.lostfound.entity.User;

import com.learn.lostfound.enums.ClaimStatus;
import com.learn.lostfound.enums.ItemType;

import com.learn.lostfound.repository.ClaimRepository;
import com.learn.lostfound.repository.ItemRepository;
import com.learn.lostfound.repository.UserRepository;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ClaimService(
        ClaimRepository claimRepository,
        ItemRepository itemRepository,
        UserRepository userRepository
    ) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    // CREATE CLAIM
    @Transactional
    public ClaimResponse createClaim(
        ClaimRequest request,
        Long userId
    ) {

        // Find item
        Item item = itemRepository.findById(request.itemId())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item not found"
                )
            );

        // Only FOUND items can be claimed
        if (item.getType() != ItemType.FOUND) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only found items can be claimed"
            );
        }

        // Cannot claim own item
        if (item.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "You cannot claim your own item"
            );
        }

        // Find authenticated user
        User user = userRepository.findById(userId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found"
                )
            );

        // Prevent duplicate pending claims
        if (claimRepository.existsByItemIdAndUserIdAndStatus(
            item.getId(),
            userId,
            ClaimStatus.PENDING
        )) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "You already have a pending claim for this item"
            );
        }

        Claim claim = new Claim();

        claim.setItem(item);
        claim.setUser(user);
        claim.setDescription(request.description());

        claim.setStatus(ClaimStatus.PENDING);

        Claim savedClaim = claimRepository.save(claim);

        return toResponse(savedClaim);
    }

    // GET MY CLAIMS
    @Transactional(readOnly = true)
    public List<ClaimResponse> getMyClaims(Long userId) {

        return claimRepository.findByUserId(userId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    // APPROVE CLAIM
    @Transactional
    public ClaimResponse approveClaim(Long claimId, Long userId) {

        Claim claim = getClaimForReviewer(claimId, userId);

        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Claim has already been processed"
            );
        }

        claim.setStatus(ClaimStatus.APPROVED);

        return toResponse(claimRepository.save(claim));
    }

    // REJECT CLAIM
    @Transactional
    public ClaimResponse rejectClaim(Long claimId, Long userId) {

        Claim claim = getClaimForReviewer(claimId, userId);

        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Claim has already been processed"
            );
        }

        claim.setStatus(ClaimStatus.REJECTED);

        return toResponse(claimRepository.save(claim));
    }

    // FIND CLAIM AND VERIFY OWNERSHIP
    private Claim getClaimForReviewer(Long claimId, Long userId) {

        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Claim not found"
                )
            );

        if (!claim.getItem().getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You cannot review this claim"
            );
        }

        return claim;
    }

    // CONVERT ENTITY TO RESPONSE DTO
    private ClaimResponse toResponse(Claim claim) {

        return new ClaimResponse(
            claim.getId(),
            claim.getItem().getId(),
            claim.getDescription(),
            claim.getStatus(),
            claim.getCreatedAt()
        );
    }
}
