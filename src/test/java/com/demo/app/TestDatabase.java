package com.demo.app;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.demo.beans.Book;
import com.demo.database.DatabaseAccess;

@SpringBootTest
class TestDatabase {
	
	private DatabaseAccess da;
	
	@Autowired
	public void setDa(DatabaseAccess da) {
		this.da = da;
	}

	@Test
	public void testDatabaseAdd() {
		
		Book book = new Book();
		
		book.setTitle("TEST TITLE");
		book.setAuthor("TEST AUTHOR");
		
		int prevSize = da.getbooks().size();
		da.addBook(book);
		int newSize = da.getbooks().size();
		
		assertEquals(newSize, prevSize+1);
	}
	
	@Test
	public void testDatabaseDelete() {
		List<Book> bookList = da.getbooks();
		
		Long id = bookList.get(0).getId();
		
		int prevSize = da.getbooks().size();
		
		da.deleteBook(id);
		
		int newSize = da.getbooks().size();
		
		assertEquals(newSize, prevSize-1);
	}

}
