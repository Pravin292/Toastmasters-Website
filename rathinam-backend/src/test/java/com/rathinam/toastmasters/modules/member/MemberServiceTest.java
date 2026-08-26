package com.rathinam.toastmasters.modules.member;

import com.rathinam.toastmasters.modules.member.dto.CreateMemberRequest;
import com.rathinam.toastmasters.modules.member.dto.MemberResponse;
import com.rathinam.toastmasters.modules.member.dto.UpdateMemberRequest;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.entity.MemberStatus;
import com.rathinam.toastmasters.modules.member.exception.DuplicateEmailException;
import com.rathinam.toastmasters.modules.member.exception.MemberNotFoundException;
import com.rathinam.toastmasters.modules.member.mapper.MemberMapper;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import com.rathinam.toastmasters.modules.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    private UUID memberId;
    private CreateMemberRequest createRequest;
    private MemberEntity memberEntity;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();

        createRequest = new CreateMemberRequest();
        createRequest.setFirstName("Jane");
        createRequest.setLastName("Doe");
        createRequest.setEmail("jane.doe@example.com");
        createRequest.setJoinDate(LocalDate.of(2026, 1, 15));
        createRequest.setBio("Toastmaster Enthusiast");

        memberEntity = new MemberEntity();
        memberEntity.setId(memberId);
        memberEntity.setFirstName("Jane");
        memberEntity.setLastName("Doe");
        memberEntity.setDisplayName("Jane Doe");
        memberEntity.setEmail("jane.doe@example.com");
        memberEntity.setJoinDate(LocalDate.of(2026, 1, 15));
        memberEntity.setStatus(MemberStatus.ACTIVE);
        memberEntity.setBio("Toastmaster Enthusiast");
    }

    @Test
    void createMember_WithValidData_ReturnsCreatedMember() {
        when(memberRepository.existsByEmailIgnoreCase("jane.doe@example.com")).thenReturn(false);
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(memberEntity);

        MemberResponse response = memberService.createMember(createRequest);

        assertNotNull(response);
        assertEquals(memberId, response.getId());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("jane.doe@example.com", response.getEmail());
        assertEquals(MemberStatus.ACTIVE, response.getStatus());

        verify(memberRepository).existsByEmailIgnoreCase("jane.doe@example.com");
        verify(memberRepository).save(any(MemberEntity.class));
    }

    @Test
    void createMember_WithDuplicateEmail_ThrowsDuplicateEmailException() {
        when(memberRepository.existsByEmailIgnoreCase("jane.doe@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> memberService.createMember(createRequest));

        verify(memberRepository).existsByEmailIgnoreCase("jane.doe@example.com");
    }

    @Test
    void getMemberById_WhenMemberExists_ReturnsMember() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));

        MemberResponse response = memberService.getMemberById(memberId);

        assertNotNull(response);
        assertEquals(memberId, response.getId());
        assertEquals("jane.doe@example.com", response.getEmail());

        verify(memberRepository).findById(memberId);
    }

    @Test
    void getMemberById_WhenMemberDoesNotExist_ThrowsMemberNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(memberRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(unknownId));

        verify(memberRepository).findById(unknownId);
    }

    @Test
    void updateMember_WithValidData_UpdatesAndReturnsMember() {
        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setFirstName("Janet");
        updateRequest.setBio("Advanced Communicator");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(memberRepository.save(any(MemberEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberResponse response = memberService.updateMember(memberId, updateRequest);

        assertNotNull(response);
        assertEquals("Janet", response.getFirstName());
        assertEquals("Advanced Communicator", response.getBio());

        verify(memberRepository).findById(memberId);
        verify(memberRepository).save(memberEntity);
    }

    @Test
    void updateMember_WithDuplicateEmail_ThrowsDuplicateEmailException() {
        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setEmail("existing@example.com");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(memberRepository.existsByEmailIgnoreCase("existing@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> memberService.updateMember(memberId, updateRequest));

        verify(memberRepository).findById(memberId);
        verify(memberRepository).existsByEmailIgnoreCase("existing@example.com");
    }
}
