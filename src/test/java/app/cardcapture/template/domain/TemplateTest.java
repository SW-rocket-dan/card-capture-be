package app.cardcapture.template.domain;

import app.cardcapture.template.domain.entity.Prompt;
import app.cardcapture.template.domain.entity.Template;
import app.cardcapture.user.domain.entity.User;
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
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        template.increasePurchaseCount();

        // then
        assertThat(template.getPurchaseCount()).isEqualTo(1);
    }
}