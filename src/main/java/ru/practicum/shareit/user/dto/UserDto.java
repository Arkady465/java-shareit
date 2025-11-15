package ru.practicum.shareit.user.dto;

import jdk.jshell.Snippet;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/** DTO returned/accepted via REST for users. */
public class UserDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    public UserDto() {
    }

    public UserDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static Snippet builder() {
        return null;
    }

    public static Snippet builder() {
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
