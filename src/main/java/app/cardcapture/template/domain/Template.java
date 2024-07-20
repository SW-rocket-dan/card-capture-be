package app.cardcapture.template.domain;

import app.cardcapture.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "templates")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
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

    @Column(nullable = false)
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
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void increasePurchaseCount() {
        this.purchaseCount++;
    }
}
