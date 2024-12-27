package com.cosi.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cosi.CosiApplication;
import com.cosi.api.ApiExceptionHandlerTests.ErrorTestController;
import com.cosi.api.exception.BadRequestException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration(classes = {CosiApplication.class, ErrorTestController.class})
public class ApiExceptionHandlerTests {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void 요청_성공함() throws Exception {
        mockMvc.perform(get("/success")).andDo(result -> {
            System.out.println(" >>> " + result.getResponse().getContentAsString());
        });
    }

    @Test
    void 클라이언트_에러_발생함() throws Exception {
        mockMvc
                .perform(get("/client-error"))
                .andDo(result -> {
                    System.out.println("응답 본문: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().is4xxClientError());
    }
    
    @Test
    void 서버_에러_발생함() throws Exception {
        mockMvc
                .perform(get("/sever-error"))
                .andDo(result -> {
                    System.out.println("응답 본문: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().is5xxServerError());
    }

    @Nested
    @DisplayName("스프링 validation 검증")
    class SpringValidationTest {
        @Test
        @DisplayName("성공: name is NotBlank AND age is positive integer")
        void 스프링_validation_검증_성공함() throws Exception {
            mockMvc
                    .perform(get("/spring-validation-error?name=smith&age=10"))
                    .andDo(result -> {
                        System.out.println("응답 본문: " + result.getResponse().getContentAsString());
                    })
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("실패: age is negative")
        void 스프링_validation_검증_실패함_이유는_나이가_음수임() throws Exception {
            mockMvc
                    .perform(get("/spring-validation-error?name=smith&age=-1"))
                    .andDo(result -> {
                        System.out.println("응답 본문: " + result.getResponse().getContentAsString());
                    })
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("실패: name is null")
        void 스프링_validation_검증_실패함_이유는_이름이_없음() throws Exception {
            mockMvc
                    .perform(get("/spring-validation-error?age=10"))
                    .andDo(result -> {
                        System.out.println("응답 본문: " + result.getResponse().getContentAsString());
                    })
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("실패: name is blank AND age is negative")
        void 스프링_validation_검증_실패함_이유는_이름이_공백이고_나이가_음수임() throws Exception {
            mockMvc
                    .perform(get("/spring-validation-error?name=  &age=10"))
                    .andDo(result -> {
                        System.out.println("응답 본문: " + result.getResponse().getContentAsString());
                    })
                    .andExpect(status().is4xxClientError());
        }
    }

    @RestController
    public static class ErrorTestController {

        @GetMapping("/success")
        public String 무조건_성공() {
            return "아싸 성공함";
        }

        @GetMapping("/client-error")
        public String 클라이언트_에러_발생() {
            throw new BadRequestException(HttpStatus.NOT_FOUND, "클라이언트 에러가 발생하면 성공임");
        }

        @GetMapping("/sever-error")
        public String 서버_에러_발생() {
            throw new RuntimeException("서버 에러가 발생하면 성공임");
        }

        @GetMapping("/spring-validation-error")
        public String 스프링_validation_검증_에러_발생(@ModelAttribute @Valid ApiExceptionHandlerTests.TestPerson testPerson) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return "검증 성공함\nperson = " + gson.toJson(testPerson);
        }
    }

    /**
     * 테스트 용 DTO
     */
    public static class TestPerson {

        @NotBlank(message = "이름이 null 또는 empty 또는 blank 이면 안 됨")
        private String name;

        @Positive(message = "나이 필수는 아닌데 음수면 안 됨")
        private int age;

        public TestPerson(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
