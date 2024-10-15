package app.cardcapture.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BatchController {

    private final DummyDataGenerator dummyDataGenerator;

    @GetMapping("/dummy/user/custom-threads")
    public ResponseEntity<String> generateUserDummyDataWithCustomThreads(@RequestParam int count) {
        dummyDataGenerator.generateUserDummyDataWithExecutorService(count);

        return ResponseEntity.ok("Dummy data generation initiated successfully");
    }

    @GetMapping("/dummy/user")
    public ResponseEntity<String> generateUserDummyDataWithOneThread(@RequestParam int count) {
        dummyDataGenerator.generateUserDummyDataWithOneThread(count);

        return ResponseEntity.ok("Dummy data generation initiated successfully");
    }
}
