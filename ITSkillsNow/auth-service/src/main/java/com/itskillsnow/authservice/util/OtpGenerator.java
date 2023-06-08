package com.itskillsnow.authservice.util;
import java.util.concurrent.ThreadLocalRandom;

public class OtpGenerator {
    public static String generateOTP() {
        int otpLength = 6;

        int min = (int) Math.pow(10, otpLength - 1);
        int max = (int) Math.pow(10, otpLength) - 1;

        int otpValue = ThreadLocalRandom.current().nextInt(min, max + 1);

        return String.valueOf(otpValue);
    }
}
