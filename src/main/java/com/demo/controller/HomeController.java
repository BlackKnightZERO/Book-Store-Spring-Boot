package com.demo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.beans.Book;
import com.demo.beans.Review;
import com.demo.database.DatabaseAccess;

@Controller
public class HomeController {
	
	@Autowired
	DatabaseAccess da;
	
	@Autowired
	@Lazy
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JdbcUserDetailsManager jdbcUserDetailsManager;
	
	@GetMapping("/")
    public String goHome(Model model){
		model.addAttribute("bookList", da.getbooks());
		return "index";
    }
	
	@GetMapping("/login")
    public String goTologin(HttpSession session){
		session.setAttribute("message", "");
        return "/login";
    }
	
	@GetMapping("/register")
    public String goToRegister(HttpSession session) {
		session.setAttribute("message", "");
    	return "register";
    }
	
	@PostMapping("/register")
    public String addUser(@RequestParam String userName, @RequestParam String password,  HttpSession session){
	   List<GrantedAuthority> authorityList = new ArrayList<>();
	   authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
 	   String encodedPassword = passwordEncoder.encode(password);
 	   User user = new User(userName,encodedPassword,authorityList);
 	   jdbcUserDetailsManager.createUser(user);
 	   session.setAttribute("message", "Registered successfully");
 	   return "redirect:/";
    }
	
	@GetMapping("/viewBook/{id}")
	public String goViewBook(@PathVariable Long id, Model model, HttpSession session){
		Book book = da.getBookById(id);
		List<Review> reviewList = da.getReviewByBookId( book.getId() );
		model.addAttribute("reviewPageHeader", "Reviews for the "+book.getTitle()+" by "+book.getAuthor());
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("bookId", book.getId());
		return "view-book";
	}
	
	@GetMapping("/admin/addBook")
	  public String goToAddBook(Model model, HttpSession session){
		session.setAttribute("message", "");  
		model.addAttribute("book", new Book());
	      return "/admin/add-book";
	  }
	   
	  @PostMapping("/admin/addBook")
	  public String addBook(@ModelAttribute Book book, Model model, HttpSession session){
		   if(da.addBook(book)) {
		   	session.setAttribute("message", "Book added successfully!");
		   }
		return "redirect:/";	   	
	  }
   
   @GetMapping("/user/addReview/{id}")
   public String goToAddReview(@PathVariable Long id, Model model, HttpSession session){
	   session.setAttribute("message", "");
	   Review review = new Review();
	   review.setBookId(id);
	   model.addAttribute("review", review);
       return "/user/add-review";
   }
   
   @PostMapping("/user/addReview")
   public String addReview(@ModelAttribute Review review, Model model, HttpSession session){
	   session.setAttribute("message", "");
	   if(da.addReview(review) ) {
	   		session.setAttribute("message", "Review added successfully!");
	   	}
		return "redirect:/viewBook/"+review.getBookId();
   }
   
	@GetMapping("/permission-denied")
	public String goToDenied(){
	    return "/error/permission-denied";
	}
	
	@GetMapping("/deleteBook/{id}")
	public String goIndexAfterDelete(@PathVariable Long id, Model model, HttpSession session){

		if(da.getBookById(id)!=null) {
			da.deleteBook(id);
		}
		return "redirect:/";
	}
   
}