package com.rathinam.toastmasters.modules.member.repository;

import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import com.rathinam.toastmasters.modules.member.entity.MemberStatus;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<MemberEntity> findByEmailIgnoreCase(String email);

    long countByStatus(MemberStatus status);
}
