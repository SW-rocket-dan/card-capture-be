package app.cardcapture.common;

import app.cardcapture.ai.openai.controller.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JacksonThreadSafetyTest {

    @Autowired
    private ObjectMapper objectMapper;

    private static final int NUM_THREADS = 10;

    @Test
    public void testObjectMapperThreadSafety() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            int threadId = i;
            executorService.submit(() -> {
                try {
                    String jsonString = String.format("{\"name\":\"Person%d\", \"age\":%d}",
                        threadId, threadId * 10);
                    Person person = objectMapper.readValue(jsonString, Person.class);
                    assertEquals("Person" + threadId, person.getName());
                    assertEquals(threadId * 10, person.getAge());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }
}
