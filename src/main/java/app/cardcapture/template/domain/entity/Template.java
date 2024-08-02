package app.cardcapture.template.domain.entity;

import app.cardcapture.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "templates")
@EntityListeners(AuditingEntityListener.class)
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description = "";

    @Column(nullable = false)
    private int likes = 0;

    @Column(nullable = false)
    private int purchaseCount = 0;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String editor;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    private List<TemplateTag> templateTags;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Prompt prompt;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void increasePurchaseCount() {
        this.purchaseCount++;
    }
}
