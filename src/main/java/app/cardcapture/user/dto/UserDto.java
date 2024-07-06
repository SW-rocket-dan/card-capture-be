package app.cardcapture.user.dto;

import lombok.Getter;

@Getter
public class UserDto {
    private String id;
    private String name;
    private String email;

    public UserDto(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
