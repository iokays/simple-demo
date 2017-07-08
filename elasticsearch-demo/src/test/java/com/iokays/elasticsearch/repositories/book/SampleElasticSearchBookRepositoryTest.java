package com.iokays.elasticsearch.repositories.book;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iokays.elasticsearch.entities.Book;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SampleElasticSearchBookRepositoryTest {

	@Resource
	SampleElasticSearchBookRepository repository;
	
	@Test
	public void testSave() {
		Book book = new Book();
		for (int i = 0; i < 100000; i++) {
			book.setId(Integer.toString(i));
			repository.save(book);
		}
	}

}
