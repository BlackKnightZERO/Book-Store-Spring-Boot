package com.demo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.demo.beans.Book;
import com.demo.beans.Review;
import com.demo.beans.ErrorMessage;
import com.demo.database.DatabaseAccess;

@RestController
@RequestMapping("/books")
public class BookController {
	
	@Autowired
	private DatabaseAccess da;
	
	@GetMapping
	public List<Book> getBookCollection() {
		return da.getbooksWithReviewsApi();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getBookWithReviewsById(@PathVariable Long id){
		try {
			Book book = da.getSinglebookWithReviewsApi(id);
			if(book==null) throw new Exception();
			return ResponseEntity.ok(book);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("404","Not Found"));
		}
	}
	
	@GetMapping("/{bookId}/reviews/{reviewId}")
	public ResponseEntity<?> getBookWithReviewsById(@PathVariable Long bookId, @PathVariable Long reviewId){
		try {
			Book book = da.getSinglebookWithReviewsApi(bookId);
			if(book==null) throw new Exception();
			Review review = da.getSingleReviewApi(bookId, reviewId);
			if(review==null) throw new Exception();
			return ResponseEntity.ok(review);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("404","Not Found"));
		}
	}
	
	@PostMapping(consumes="application/json")
	public  ResponseEntity<?> postStudent(@RequestBody Review review){
		
		try {
			Long id = da.addReviewApi(review);
			review.setId(id);
			URI Location = ServletUriComponentsBuilder.fromCurrentRequest()
													.path("/{id}")
													.buildAndExpand(id)
													.toUri();
			
			return ResponseEntity.created(Location).body(review);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
								.body(new ErrorMessage("500","Server Error"));
		}
	}
	
}
