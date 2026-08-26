package com.rathinam.toastmasters.modules.member.mapper;

import com.rathinam.toastmasters.modules.member.dto.CreateMemberRequest;
import com.rathinam.toastmasters.modules.member.dto.MemberResponse;
import com.rathinam.toastmasters.modules.member.dto.UpdateMemberRequest;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public MemberEntity toEntity(CreateMemberRequest request) {
        MemberEntity entity = new MemberEntity();
        entity.setFirstName(request.getFirstName().trim());
        entity.setLastName(request.getLastName().trim());
        
        String displayName = (request.getDisplayName() != null && !request.getDisplayName().trim().isEmpty())
            ? request.getDisplayName().trim()
            : entity.getFirstName() + " " + entity.getLastName();
        entity.setDisplayName(displayName);

        entity.setEmail(request.getEmail());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setProfilePictureUrl(request.getProfilePictureUrl());
        entity.setJoinDate(request.getJoinDate());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : entity.getStatus());
        entity.setBio(request.getBio());

        return entity;
    }

    public void updateEntityFromRequest(MemberEntity entity, UpdateMemberRequest request) {
        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            entity.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            entity.setLastName(request.getLastName().trim());
        }
        if (request.getDisplayName() != null && !request.getDisplayName().trim().isEmpty()) {
            entity.setDisplayName(request.getDisplayName().trim());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            entity.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            entity.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfilePictureUrl() != null) {
            entity.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getJoinDate() != null) {
            entity.setJoinDate(request.getJoinDate());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getBio() != null) {
            entity.setBio(request.getBio());
        }
    }

    public MemberResponse toResponse(MemberEntity entity) {
        MemberResponse response = new MemberResponse();
        response.setId(entity.getId());
        response.setAccountId(entity.getAccountId());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setDisplayName(entity.getDisplayName());
        response.setEmail(entity.getEmail());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setProfilePictureUrl(entity.getProfilePictureUrl());
        response.setJoinDate(entity.getJoinDate());
        response.setStatus(entity.getStatus());
        response.setBio(entity.getBio());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        return response;
    }
}
