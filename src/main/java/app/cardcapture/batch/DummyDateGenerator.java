package app.cardcapture.batch;

import app.cardcapture.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyDateGenerator {

    private final UserJdbcRepository userJdbcRepository;

    public DummyDateGenerator(UserJdbcRepository userJdbcRepository) {
        this.userJdbcRepository = userJdbcRepository;
    }

    public void generateTemplateDummyData(int count) {

    }

    @GetMapping("/dummy/user")
    public ResponseEntity<String> generateUserDummyDataWithExecutorService(@RequestParam int count) {
        int threadCount = 1000;
        int batchSize = count / threadCount;
        int chunkSize = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            runThread(count, i, batchSize, threadCount, executorService, chunkSize);
        }

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                return ResponseEntity.status(500)
                    .body("Error: Timeout occurred while waiting for threads to finish.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Error: Thread interrupted.");
        }

        return ResponseEntity.ok("Dummy data generation initiated successfully");
    }

    private void runThread(int count, int i, int batchSize, int threadCount,
        ExecutorService executorService, int chunkSize) {
        final int start = i * batchSize;
        final int end = (i == threadCount - 1) ? count : (i + 1) * batchSize;

        executorService.submit(() -> {
            List<User> users = new ArrayList<>();

            for (int j = start; j < end; j++) {
                generateRandomUser(users);
                users = saveIfChunkFull(chunkSize, users);
            }

            if (!users.isEmpty()) {
                userJdbcRepository.saveAll(users);
            }
        });
    }

    private void generateRandomUser(List<User> users) {
        User user = new User();
        user.setGoogleId(generateGoogleId());
        user.setEmail(generateUniqueEmail());
        user.setName(generateName());
        user.setVerifiedEmail(true);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        users.add(user);
    }

    private List<User> saveIfChunkFull(int chunkSize, List<User> users) {
        if (users.size() >= chunkSize) {
            userJdbcRepository.saveAll(users);
            users = new ArrayList<>();
        }
        return users;
    }

    private String generateGoogleId() {
        return String.valueOf(
            ThreadLocalRandom.current().nextLong(999999999999999999L));
    }

    private String generateUniqueEmail() {
        StringBuilder sb = new StringBuilder();
        sb.append(UUID.randomUUID());
        sb.append("@");
        sb.append(UUID.randomUUID());
        sb.append(".com");
        return sb.toString();
    }

    private String generateName() {
        int randomLength = ThreadLocalRandom.current().nextInt(1, 8);
        return UUID.randomUUID().toString().substring(0, randomLength);
    }
}
