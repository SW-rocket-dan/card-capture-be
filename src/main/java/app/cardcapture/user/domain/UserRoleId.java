package app.cardcapture.user.domain;

import java.io.Serializable;
import java.util.Objects;

public class UserRoleId implements Serializable {

    private Long user;
    private Role role;

    public UserRoleId() {}

    public UserRoleId(Long user, Role role) {
        this.user = user;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleId that = (UserRoleId) o;
        return Objects.equals(user, that.user) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, role);
    }
}