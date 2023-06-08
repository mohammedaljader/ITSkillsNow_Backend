package com.itskillsnow.authservice.repository;

import com.itskillsnow.authservice.model.OTPCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OTPCodeRepository extends JpaRepository<OTPCode, String> {
    Optional<OTPCode> findByOtpCode(String otpCode);

    @Transactional
    @Modifying
    @Query("DELETE FROM OTPCode o WHERE o.username = :username")
    void deleteAllByUsername(String username);
}
