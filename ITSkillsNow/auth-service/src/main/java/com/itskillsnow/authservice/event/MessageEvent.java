package com.itskillsnow.authservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEvent {
    private String fullName;
    private String email;
    private String otpCode;
    private String subject;
    private String message;
}
