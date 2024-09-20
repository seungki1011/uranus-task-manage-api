package com.uranus.taskmanager.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uranus.taskmanager.api.domain.member.Member;
import com.uranus.taskmanager.api.exception.InvalidLoginIdentityException;
import com.uranus.taskmanager.api.exception.InvalidLoginPasswordException;
import com.uranus.taskmanager.api.repository.MemberRepository;
import com.uranus.taskmanager.api.request.LoginRequest;
import com.uranus.taskmanager.api.response.LoginResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;

	@Transactional
	public LoginResponse login(LoginRequest loginRequest) {
		/**
		 * Todo
		 * loginId, email 조회와 password 검증 분리
		 * password 암호화 로직 후에 검증 로직 수정
		 */
		Member member = memberRepository.findByLoginIdOrEmail(loginRequest.getLoginId(), loginRequest.getEmail())
			.orElseThrow(InvalidLoginIdentityException::new);

		if (!member.getPassword().equals(loginRequest.getPassword())) {
			throw new InvalidLoginPasswordException();
		}

		return LoginResponse.fromEntity(member);
	}
}
