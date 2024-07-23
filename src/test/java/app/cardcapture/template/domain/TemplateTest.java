package app.cardcapture.template.domain;

import app.cardcapture.user.domain.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateTest {

    @Test
    public void purchaseCount가_증가한다() {
        // given
        User user = new User();
        Prompt prompt = new Prompt();
        Template template = new Template(
                1L,
                user,
                "title",
                "description",
                0,
                0,
                "editor",
                "fileUrl",
                new ArrayList<>(),
                prompt,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        template.increasePurchaseCount();

        // then
        assertThat(template.getPurchaseCount()).isEqualTo(1);
    }
}