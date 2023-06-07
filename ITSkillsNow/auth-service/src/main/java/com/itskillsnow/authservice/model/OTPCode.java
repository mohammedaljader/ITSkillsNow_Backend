package com.itskillsnow.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "otp_code")
public class OTPCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID otpId;

    private String otpCode;

    private String username;

    private LocalTime createdAt;
}
