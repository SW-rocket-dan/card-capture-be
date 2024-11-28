package app.cardcapture.batch;

import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DummyDataGenerator {

    private final UserJdbcRepository userJdbcRepository;

    public void generateUserDummyDataWithExecutorService(int count) {
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
                throw new BusinessLogicException(ErrorCode.TIMEOUT);
            }
        } catch (InterruptedException e) {
            throw new BusinessLogicException(ErrorCode.INTERREPTED_EXCEPTION);
        }
    }

    private void runThread(int count, int i, int batchSize, int threadCount,
        ExecutorService executorService, int chunkSize) {
        final int start = i * batchSize;
        final int end = (i == threadCount - 1) ? count : (i + 1) * batchSize;

        executorService.submit(() -> {
            List<User> users = new ArrayList<>();

            for (int j = start; j < end; j++) {
                User newUser = generateRandomUser();
                users.add(newUser);
                users = saveIfChunkFull(chunkSize, users);
            }

            if (!users.isEmpty()) {
                userJdbcRepository.saveAll(users);
            }
        });
    }

    private User generateRandomUser() {
        User user = new User();
        user.setGoogleId(generateGoogleId());
        user.setEmail(generateUniqueEmail());
        user.setName(generateName());
        user.setVerifiedEmail(true);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return user;
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

    public void generateUserDummyDataWithOneThread(int count) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            User newUser = generateRandomUser();
            users.add(newUser);
        }

        userJdbcRepository.saveAll(users);
    }
}
