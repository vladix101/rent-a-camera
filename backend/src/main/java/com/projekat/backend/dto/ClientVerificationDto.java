package com.projekat.backend.dto;

public class ClientVerificationDto {
    private ClientRegistrationDto client;
    private String email;
    private String code;

    public ClientRegistrationDto getClient() {
        return client;
    }

    public void setClient(ClientRegistrationDto client) {
        this.client = client;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
