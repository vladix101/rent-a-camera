package com.projekat.backend.dto;

public class ZaposleniDto {
    private Long id;
    private String ime;
    private String prezime;
    private String username;
    private String email;

    public ZaposleniDto() {
    }

    public ZaposleniDto(Long id, String ime, String prezime, String username, String email) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
