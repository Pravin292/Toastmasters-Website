package com.rathinam.toastmasters.modules.auth.service;

import com.rathinam.toastmasters.config.security.CustomUserDetails;
import com.rathinam.toastmasters.config.security.JwtProvider;
import com.rathinam.toastmasters.modules.account.entity.AccountEntity;
import com.rathinam.toastmasters.modules.account.entity.AccountRole;
import com.rathinam.toastmasters.modules.account.repository.AccountRepository;
import com.rathinam.toastmasters.modules.auth.dto.AuthResponse;
import com.rathinam.toastmasters.modules.auth.dto.LoginRequest;
import com.rathinam.toastmasters.modules.auth.dto.RegisterRequest;
import com.rathinam.toastmasters.modules.member.entity.MemberEntity;
import com.rathinam.toastmasters.modules.member.entity.MemberStatus;
import com.rathinam.toastmasters.modules.member.exception.DuplicateEmailException;
import com.rathinam.toastmasters.modules.member.repository.MemberRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtProvider jwtProvider,
                       AccountRepository accountRepository,
                       MemberRepository memberRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.accountRepository = accountRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return new AuthResponse(jwt, userDetails.getUsername(), userDetails.getAccount().getRole());
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        String normalizedEmail = registerRequest.getEmail().trim().toLowerCase();

        if (accountRepository.existsByEmail(normalizedEmail) || memberRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateEmailException(normalizedEmail);
        }

        AccountEntity account = new AccountEntity();
        account.setEmail(normalizedEmail);
        account.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        account.setRole(AccountRole.MEMBER);
        account.setEnabled(true);
        AccountEntity savedAccount = accountRepository.save(account);

        MemberEntity member = new MemberEntity();
        member.setAccountId(savedAccount.getId());
        member.setFirstName(registerRequest.getFirstName().trim());
        member.setLastName(registerRequest.getLastName().trim());
        member.setDisplayName(member.getFirstName() + " " + member.getLastName());
        member.setEmail(normalizedEmail);
        member.setPhoneNumber(registerRequest.getPhoneNumber());
        member.setJoinDate(LocalDate.now());
        member.setStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(normalizedEmail);
        loginRequest.setPassword(registerRequest.getPassword());
        return authenticateUser(loginRequest);
    }
}
