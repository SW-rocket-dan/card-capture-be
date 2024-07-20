package app.cardcapture.template.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "prompts")
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phrase_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Phrase phrase;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emphasis_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Emphasis emphasis;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String model;
}
