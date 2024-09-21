package app.cardcapture.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRoleIdTest {

    @Test
    public void 동일한_유저와_역할을_가진_UserRoleId_객체는_같아야_한다() {
        // given
        UserRoleId id1 = new UserRoleId(1L, Role.USER);
        UserRoleId id2 = new UserRoleId(1L, Role.USER);

        // when & then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    public void 다른_유저를_가진_UserRoleId_객체는_같지_않아야_한다() {
        // given
        UserRoleId id1 = new UserRoleId(1L, Role.USER);
        UserRoleId id2 = new UserRoleId(2L, Role.USER);

        // when & then
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    public void 다른_역할을_가진_UserRoleId_객체는_같지_않아야_한다() {
        // given
        UserRoleId id1 = new UserRoleId(1L, Role.USER);
        UserRoleId id2 = new UserRoleId(1L, Role.ADMIN);

        // when & then
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    public void null과_비교시_false를_반환해야한다() {
        // given
        UserRoleId id1 = new UserRoleId(1L, Role.USER);

        // when & then
        assertThat(id1.equals(null)).isFalse();
    }

    @Test
    public void 자기_자신과는_동일해야한다() {
        // given
        UserRoleId id1 = new UserRoleId(1L, Role.USER);

        // when & then
        assertThat(id1).isEqualTo(id1);
    }

    @Test
    public void 동일한_유저와_역할을_가진_UserRoleId_객체는_같은_hashCode를_가져야_한다() {
        // given
        UserRoleId id1 = new UserRoleId(1L, Role.USER);
        UserRoleId id2 = new UserRoleId(1L, Role.USER);

        // when & then
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}
