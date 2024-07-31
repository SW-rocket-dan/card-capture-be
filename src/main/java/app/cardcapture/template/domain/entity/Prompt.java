package app.cardcapture.template.domain.entity;

import app.cardcapture.template.domain.Emphasis;
import app.cardcapture.template.domain.Phrase;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Embedded
    private Phrase phrase;

    @Embedded
    private Emphasis emphasis;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String model;
}
