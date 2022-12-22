package com.demo.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.demo.beans.Book;
import com.demo.beans.Review;

@Repository
public class DatabaseAccess {

	@Autowired
	private NamedParameterJdbcTemplate jdbc;
	
	public List<String> getAuthorities() {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT DISTINCT authority FROM authorities";
		List<String> authorities = jdbc.queryForList(query, namedParameters, String.class);
		return authorities;
	
	}
	
	public List<Book> getbooks() {
		
		String query = "SELECT * FROM books";
		
		BeanPropertyRowMapper<Book> mapper = new BeanPropertyRowMapper<>(Book.class); 
		
		List<Book> books = jdbc.query(query, mapper);
		
		return books;
		
	}
	
	public Book getBookById(Long id) {	
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		String query = "SELECT * FROM books WHERE id = :id";
		
		parameters.addValue("id", id);		
		
		Book book = null;
		
		try {
			book = (Book) jdbc.queryForObject(query, parameters, new BeanPropertyRowMapper<Book>(Book.class));
		} catch(EmptyResultDataAccessException ex) {
			System.out.println("404 | NOT FOUND");
		}
		
		return book;
		
	}
	
	public List<Review> getReviewByBookId(Long bookId) {	
		
		BeanPropertyRowMapper<Review> mapper = new BeanPropertyRowMapper<>(Review.class); 
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		String query = "SELECT * FROM reviews WHERE bookId = :bookId";
		
		parameters.addValue("bookId", bookId);		
		
		List<Review> reviews = jdbc.query(query, parameters, mapper);
		
		return reviews;
		
	}
	
	public boolean addReview(Review review) {
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		String query = "INSERT INTO reviews (bookId, text) VALUES (:bookId, :text)";
		
		parameters.addValue("bookId", review.getBookId());
		parameters.addValue("text", review.getText());
		
		return jdbc.update(query, parameters) > 0;
		
	}
	
	public boolean addBook(Book book) {
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		String query = "INSERT INTO books (title, author) VALUES (:title, :author)";
		
		parameters.addValue("title", book.getTitle());
		parameters.addValue("author", book.getAuthor());
		
		return jdbc.update(query, parameters) > 0;
		
	}
	
	public boolean deleteBook(Long id) {
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		String query = "DELETE FROM reviews WHERE bookId = :id";
		
		parameters.addValue("id", id);

		jdbc.update(query, parameters);
		
		query = "DELETE FROM books WHERE id = :id";
		
		parameters.addValue("id", id);
		
		return jdbc.update(query, parameters) > 0;
		
		
	}
	
	/// API
	public List<Book> getbooksWithReviewsApi() {
		
		BeanPropertyRowMapper<Book> bookMapper = new BeanPropertyRowMapper<>(Book.class); 
		
		String query = "SELECT * FROM books";
		List<Book> books = jdbc.query(query, bookMapper);
		
		for(Book book : books) {
			book.setReviews(this.getReviewsByBookIdApi(book.getId()));
		}

		return books;
		
	}
	
	public Book getSinglebookWithReviewsApi(Long id) {
		
		BeanPropertyRowMapper<Book> bookMapper = new BeanPropertyRowMapper<>(Book.class); 
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		String query = "SELECT * FROM books WHERE id = :id";
		
		parameters.addValue("id", id);		
		
		Book book = null;
		
		try {
			book = (Book) jdbc.queryForObject(query, parameters, bookMapper);
			book.setReviews(this.getReviewsByBookIdApi(book.getId()));
		} catch(EmptyResultDataAccessException ex) {
			System.out.println("404 | NOT FOUND");
		}
		
		return book;
		
	}
	
	
	public List<Review> getReviewsByBookIdApi(Long bookId) {
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		BeanPropertyRowMapper<Review> reviewMapper = new BeanPropertyRowMapper<>(Review.class);
		
		String query = "SELECT * FROM reviews WHERE bookId = :bookId";
		
		parameters.addValue("bookId", bookId);		
		
		List<Review> reviews = null;
		
		try {
			reviews = (List<Review>) jdbc.query(query, parameters, reviewMapper);
		} catch(EmptyResultDataAccessException ex) {
			System.out.println("404 | NOT FOUND");
		}
		
		return reviews;
		
	}
	
	public Review getSingleReviewApi(Long bookId, Long reviewId) {
		
		BeanPropertyRowMapper<Review> reviewMapper = new BeanPropertyRowMapper<>(Review.class); 
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		String query = "SELECT * FROM reviews WHERE bookId = :bookId AND id = :reviewId";
		
		parameters.addValue("reviewId", reviewId);
		parameters.addValue("bookId", bookId);	
		
		Review review = null;
		
		try {
			review = (Review) jdbc.queryForObject(query, parameters, reviewMapper);
		} catch(EmptyResultDataAccessException ex) {
			System.out.println("404 | NOT FOUND");
		}
		
		return review;
		
	}
	
	public Long addReviewApi(Review review) {
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		String query = "INSERT INTO reviews (bookId, text) VALUES (:bookId, :text)";
		
		parameters.addValue("bookId", review.getBookId());
		parameters.addValue("text", review.getText());
		
		KeyHolder generatedkey = new GeneratedKeyHolder();
		
		int returnValue = jdbc.update(query, parameters, generatedkey);
		
		Long reviewId = (Long) generatedkey.getKey();
		
		return (returnValue > 0) ? reviewId : 0;
		
	}
	
	
}
