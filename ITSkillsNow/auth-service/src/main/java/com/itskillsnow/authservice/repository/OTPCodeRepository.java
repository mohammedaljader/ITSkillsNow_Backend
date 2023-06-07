package com.itskillsnow.authservice.repository;

import com.itskillsnow.authservice.model.OTPCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPCodeRepository extends JpaRepository<OTPCode, String> {
    Optional<OTPCode> findByOtpCode(String otpCode);
}
