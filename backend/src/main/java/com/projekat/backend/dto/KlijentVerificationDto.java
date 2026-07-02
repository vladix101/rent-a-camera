package com.projekat.backend.dto;

public class KlijentVerificationDto {
    private KlijentRegistrationDto klijent;
    private String email;
    private String code;

    public KlijentRegistrationDto getKlijent() {
        return klijent;
    }

    public void setKlijent(KlijentRegistrationDto klijent) {
        this.klijent = klijent;
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
