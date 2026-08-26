package com.rathinam.toastmasters.modules.member.service;

import com.rathinam.toastmasters.modules.member.dto.CreateMemberRequest;
import com.rathinam.toastmasters.modules.member.dto.MemberResponse;
import com.rathinam.toastmasters.modules.member.dto.UpdateMemberRequest;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.exception.DuplicateEmailException;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.mapper.MemberMapper;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public MemberService(MemberRepository memberRepository, MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
            .map(memberMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (memberRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateEmailException(normalizedEmail);
        }

        MemberEntity entity = memberMapper.toEntity(request);
        MemberEntity savedEntity = memberRepository.save(entity);
        return memberMapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(UUID id) {
        MemberEntity entity = memberRepository.findById(id)
            .orElseThrow(() -> new MemberNotFoundException(id));
        return memberMapper.toResponse(entity);
    }

    @Transactional
    public MemberResponse updateMember(UUID id, UpdateMemberRequest request) {
        MemberEntity entity = memberRepository.findById(id)
            .orElseThrow(() -> new MemberNotFoundException(id));

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String normalizedNewEmail = request.getEmail().trim().toLowerCase();
            if (!normalizedNewEmail.equalsIgnoreCase(entity.getEmail()) &&
                memberRepository.existsByEmailIgnoreCase(normalizedNewEmail)) {
                throw new DuplicateEmailException(normalizedNewEmail);
            }
        }

        memberMapper.updateEntityFromRequest(entity, request);
        MemberEntity updatedEntity = memberRepository.save(entity);
        return memberMapper.toResponse(updatedEntity);
    }
}
