package app.cardcapture.user.controller;

import app.cardcapture.security.AuthenticatedControllerTest;
import app.cardcapture.user.dto.UserMapper;
import app.cardcapture.user.dto.UserProfileResponseDto;
import app.cardcapture.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest extends AuthenticatedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserMapper userMapper;

//    @MockBean
    private UserService userService;

    private UserProfileResponseDto userProfileResponseDto;

    @BeforeEach
    void setUp() {
        userProfileResponseDto = new UserProfileResponseDto(
            "test@example.com",
            "Test User",
            "http://example.com/picture.jpg",
            LocalDateTime.now()
        );

        given(userMapper.toUserProfileResponseDto(mockUser)).willReturn(userProfileResponseDto);
    } // TODO: UserMapper를 mock하지 않고 이 부분 삭제 가능

    @Test
    public void getUserDetails_success() throws Exception {
        // Given

        // When
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/me")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.name").value("Test User"))
            .andExpect(jsonPath("$.data.picture").value("http://example.com/picture.jpg"))
            .andExpect(jsonPath("$.data.createdAt").exists());
    }
}