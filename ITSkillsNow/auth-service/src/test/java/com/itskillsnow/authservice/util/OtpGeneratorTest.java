package com.itskillsnow.authservice.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class OtpGeneratorTest {
    @Test
    void generateOTP_shouldReturnSixDigitCode() {
        String otpCode = OtpGenerator.generateOTP();

        Assertions.assertEquals(6, otpCode.length(), "OTP code length should be 6");
    }

    @Test
    void generateOTP_shouldReturnNumericCode() {
        String otpCode = OtpGenerator.generateOTP();

        Assertions.assertTrue(otpCode.matches("\\d+"), "OTP code should only contain numeric characters");
    }
}