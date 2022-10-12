package com.nigel.bookgame.rest.unit.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.nigel.bookgame.rest.repository.IdGenerator;

/**
 * Test class for {@link IdGenerator}.
 * 
 * @author nigel
 */
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class IdGeneratorTest {
    
	@Autowired
	private IdGenerator generator1;
	
	@Autowired
	private IdGenerator generator2;
	
	@Test
	public void testPrototypeScoping() throws Exception {
		Assertions.assertThat(this.generator1.getNextId()).isEqualTo(1);
		Assertions.assertThat(this.generator1.getNextId()).isEqualTo(2);
		Assertions.assertThat(this.generator2.getNextId()).isEqualTo(1);
		Assertions.assertThat(this.generator2.getNextId()).isEqualTo(2);
	}
}