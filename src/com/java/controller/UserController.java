package com.java.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.java.components.Address;
import com.java.components.Cart;
import com.java.components.CartEntry;
import com.java.components.User;
import com.java.components.UserDetails;
import com.java.exception.MyCustomException;
import com.java.service.CartServiceImpl;
import com.java.service.UserServiceImpl;
import com.java.util.LastState;

@Controller
@SessionAttributes(names = { "user", "login", "userdetails", "address" })
public class UserController {

	@ModelAttribute("user")
	public User initUser() {
		return new User();
	}

	@ModelAttribute("userdetails")
	public UserDetails initUserDetails() {
		return new UserDetails();
	}

	@ModelAttribute("address")
	public Address initAddress() {
		return new Address();
	}


	@Autowired
	@Qualifier("userservice")
	private UserServiceImpl userService;

	@Autowired
	private CartServiceImpl cartService;

	@RequestMapping(path = "/loginUser", method = RequestMethod.POST)
	public ModelAndView loginUser(@ModelAttribute("user") User user, HttpServletRequest req, HttpServletResponse resp)
			throws MyCustomException {
		ModelAndView mv = new ModelAndView("redirect:home");
		HttpSession session = req.getSession(true);
		String email = user.getUserEmail();
		String password = user.getUserPassword();

		user = userService.getUser(email);
		if (user != null && user.getUserEmail() != null && user.getUserPassword().equals(password)) {
			UserDetails details = user.getUserDetails();
			session.setAttribute("userdetails", details);
			Cart cart = (Cart) session.getAttribute("cart");
			if (details != null && details.getCart() != null) {
				if (cart != null) {
					cartService.addEntriesToCart(user, cart.getCartEntries());
				}
				cart = cartService.getCart(user);
			}
			session.setAttribute("cart", cart);
			System.out.println("from session "+session.getAttribute("cart"));
		} else {
			req.getSession().removeAttribute("user");
			mv.addObject("errorMsg", "Invalid username or password");
			mv.setViewName("loginPage");
		}
		return mv;
	}

	@RequestMapping(path = "/registerUser", method = RequestMethod.POST)
	public ModelAndView registerUser(@ModelAttribute("user") User user,
			@ModelAttribute("userdetails") UserDetails details) {
		ModelAndView mv = new ModelAndView("redirect:login");
		user.setUserDetails(details);
		userService.addUser(user);
		return mv;
	}

	@RequestMapping(value = "/deleteUser")
	public ModelAndView deleteUser(@ModelAttribute("user") User user, HttpServletRequest req, HttpServletResponse resp)
			throws MyCustomException {
		ModelAndView mv = new ModelAndView("redirect:home");
		userService.deleteUser(user);

		req.getSession().removeAttribute("user");

		return mv;
	}

	@RequestMapping(value = "/profile")
	public ModelAndView userAccount(HttpServletRequest req) {

		ModelAndView mv = new ModelAndView("profilePage");

		HttpSession session = req.getSession();
		if (session != null && session.getAttribute("user") != null
				&& ((User) session.getAttribute("user")).getUserEmail() != null) {
			User user = (User) session.getAttribute("user");

			if (user.getUserDetails() == null || user.getUserDetails().getUserId() <= 0) {
				user = userService.getUser(user.getUserEmail());
			}
			mv.addObject("user", user);
		} else {
			mv.addObject("errorMsg", "Cannot access profile when not logged in.");
			mv.setViewName("errorPage");
		}

		return mv;
	}

	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public ModelAndView updateProfile(@ModelAttribute("user") User user,
			@Valid @ModelAttribute("userdetails") UserDetails details, @ModelAttribute("address") Address address,
			HttpServletRequest req, HttpServletResponse resp, BindingResult result) throws MyCustomException {

		if (result.hasErrors()) {
			req.setAttribute("formError", result.getAllErrors());
			try {
				req.getRequestDispatcher("/profile").forward(req, resp);
			} catch (ServletException | IOException e) {
				throw new MyCustomException(e.getMessage());
			}
		}

		String gender = req.getParameter("genders");
		if (user != null && user.getUserEmail() != null && user.getUserPassword() != null && user.getUserEmail() != ""
				&& user.getUserPassword() != "" && details != null) {
			details.setAddresses(new HashSet<>());
			details.addAddress(address);
			if (gender.equalsIgnoreCase("female")) {
				details.setGender(UserDetails.Gender.FEMALE);
			} else {
				details.setGender(UserDetails.Gender.MALE);
			}
			user.setUserDetails(details);
			userService.updateUser(user, address);
		}
		return new ModelAndView("redirect:profile");
	}

}
