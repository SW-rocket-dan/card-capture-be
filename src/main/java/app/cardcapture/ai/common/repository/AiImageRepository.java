package app.cardcapture.ai.common.repository;

import app.cardcapture.ai.common.AiImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiImageRepository extends JpaRepository<AiImage, Long> {

}
