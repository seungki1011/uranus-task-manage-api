package com.uranus.taskmanager.api.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uranus.taskmanager.api.request.SignupRequest;
import com.uranus.taskmanager.api.service.AuthenticationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private AuthenticationService authenticationService;

	@Test
	@DisplayName("회원 가입에 검증을 통과하면 OK를 기대한다")
	void test1() throws Exception {
		SignupRequest signupRequest = SignupRequest.builder()
			.userId("testuser1234")
			.email("testemail@gmail.com")
			.password("Testpassword1234!")
			.build();
		String requestBody = objectMapper.writeValueAsString(signupRequest);

		mockMvc.perform(post("/api/v1/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@ParameterizedTest
	@CsvSource({
		"'testtesttesttesttest1', 'User ID must be alphanumeric and must be between 2 and 20 characters'",
		"'1', 'User ID must be alphanumeric and must be between 2 and 20 characters'",
		"'test!!', 'User ID must be alphanumeric and must be between 2 and 20 characters'",
		"'한글아이디', 'User ID must be alphanumeric and must be between 2 and 20 characters'",
		"'test1한글', 'User ID must be alphanumeric and must be between 2 and 20 characters'",
	})
	@DisplayName("회원 가입에 userId는 영문과 숫자 조합에 2~20자를 지켜야한다")
	void test2(String userId, String userIdValidMsg) throws Exception {
		SignupRequest signupRequest = SignupRequest.builder()
			.userId(userId)
			.email("testemail@gmail.com")
			.password("Testpassword!")
			.build();
		String requestBody = objectMapper.writeValueAsString(signupRequest);

		mockMvc.perform(post("/api/v1/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.validation.userId").value(userIdValidMsg))
			.andDo(print());
	}

	@ParameterizedTest
	@CsvSource({
		"'test', 'The password must be alphanumeric "
			+ "including at least one special character and must be between 8 and 30 characters'",
		"'Test1234', 'The password must be alphanumeric "
			+ "including at least one special character and must be between 8 and 30 characters'",
		"'한글패스워드', 'The password must be alphanumeric "
			+ "including at least one special character and must be between 8 and 30 characters'",
		"'Test1234!한글', 'The password must be alphanumeric "
			+ "including at least one special character and must be between 8 and 30 characters'",
	})
	@DisplayName("회원 가입에 password는 하나 이상의 영문자, 숫자와 특수문자를 포함한 조합에 8~30자를 지켜야한다")
	void test3(String password, String passwordValidMsg) throws Exception {
		SignupRequest signupRequest = SignupRequest.builder()
			.userId("testuser1234")
			.email("testemail@gmail.com")
			.password(password)
			.build();
		String requestBody = objectMapper.writeValueAsString(signupRequest);

		mockMvc.perform(post("/api/v1/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.validation.password")
				.value(passwordValidMsg))
			.andDo(print());
	}

	static Stream<Arguments> provideInvalidInputs() {
		String userIdValidMsg = "User ID must not be blank";
		String emailValidMsg = "Email must not be blank";
		String passwordValidMsg = "Password must not be blank";
		return Stream.of(
			arguments(null, null, null, userIdValidMsg, emailValidMsg, passwordValidMsg), // null
			arguments("", "", "", userIdValidMsg, emailValidMsg, passwordValidMsg),   // 빈 문자열
			arguments(" ", " ", " ", userIdValidMsg, emailValidMsg, passwordValidMsg)  // 공백
		);
	}

	@ParameterizedTest
	@MethodSource("provideInvalidInputs")
	@DisplayName("회원 가입에 userId, email, password는 null, 공백, 빈 문자이면 안된다")
	void test4(String userId, String email, String password,
		String userIdValidMsg, String emailValidMsg, String passwordValidMsg) throws Exception {
		// given
		SignupRequest signupRequest = SignupRequest.builder()
			.userId(userId)
			.email(email)
			.password(password)
			.build();
		String requestBody = objectMapper.writeValueAsString(signupRequest);

		// when & then
		mockMvc.perform(post("/api/v1/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.validation.userId").value(hasItem(userIdValidMsg)))
			.andExpect(jsonPath("$.validation.email").value(hasItem(emailValidMsg)))
			.andExpect(jsonPath("$.validation.password").value(hasItem(passwordValidMsg)))
			.andDo(print());
	}

}