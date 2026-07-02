package com.projekat.backend.dto;

public class LoginResponseDto {
    private Long userId;
    private String ime;
    private String prezime;
    private String username;
    private String userType;
    private String token;

    public LoginResponseDto() {
    }

    public LoginResponseDto(Long userId, String ime, String prezime, String username, String userType, String token) {
        this.userId = userId;
        this.ime = ime;
        this.prezime = prezime;
        this.username = username;
        this.userType = userType;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
