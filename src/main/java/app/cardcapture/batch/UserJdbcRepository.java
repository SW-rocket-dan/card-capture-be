package app.cardcapture.batch;

import app.cardcapture.user.domain.entity.User;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<User> users) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO users (`google_id`,`email`,`name`,`verified_email`,`created_at`,`updated_at`) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `google_id` = VALUES(`google_id`)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, users.get(i).getGoogleId());
                    ps.setString(2, users.get(i).getEmail());
                    ps.setString(3, users.get(i).getName());
                    ps.setBoolean(4, users.get(i).isVerifiedEmail());
                    ps.setObject(5, users.get(i).getCreatedAt());
                    ps.setObject(6, users.get(i).getUpdatedAt());
                }

                @Override
                public int getBatchSize() {
                    return users.size();
                }
            });
    }
}
