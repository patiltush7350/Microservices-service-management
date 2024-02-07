package com.lcwd.user.service.services.impl;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exceptions.ResourceNotFoundException;
import com.lcwd.user.service.external.service.HotelService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;




@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private HotelService hotelservice;
	
	private Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);
			
	
	@Override
	public User saveUser(User user) {
		String randomUserId = UUID.randomUUID().toString();
		user.setUserId(randomUserId);
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUser() {
		
		//implement RATING SERVICE CALL: USING REST TEMPLATE
		return userRepository.findAll();
	}

	//get single user
	@Override
	public User getUser(String userId) {
		//get user from database with the help of user repository
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id is not found on server !! : "+userId));
		//fetch rating of the above user from RATING SERVICE
		//http://localhost:8083/ratings/users/698cba39-7899-4816-a9ca-738b98dab2c1
		
		Rating[] ratingsOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/" +user.getUserId(), Rating[].class);
		logger.info("{}", ratingsOfUser);
		List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();
		List<Rating> ratingList = ratings.stream().map(rating -> {
			       
			        Rating transformedRating = new Rating();
			        return transformedRating;
			    })
			    .collect(Collectors.toList());

			user.setRatings(ratingList);
			return user;

	}

}

