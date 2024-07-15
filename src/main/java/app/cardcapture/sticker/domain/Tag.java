package app.cardcapture.sticker.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags")
//@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sticker_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Sticker sticker;

    @Column(nullable = false)
    private String korean;

    @Column(nullable = true)
    private String english;

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", korean='" + korean + '\'' +
                ", english='" + english + '\'' +
                '}';
    }
}
