package com.example.franchise;

import com.example.franchise.domain.spi.UserPersistencePort;
import com.example.franchise.domain.usecase.UserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = FranchiseApiApplication.class)
class ResilientApiApplicationTests {

	@MockBean
	private UserPersistencePort userPersistencePort;

	@Autowired
	private UserUseCase userUseCase;

	@Test
	void contextLoads() {
	}

}
