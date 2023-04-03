package com.itskillsnow.authservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthEvent {
    private UserPayload userPayload;
    private String eventType;
}
