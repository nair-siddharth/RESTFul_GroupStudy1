package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommonController {
    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.GET,  path = "/users/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") final String UUID,
                                                       @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = commonBusinessService.getUserDetails(authorization,UUID);

        UserDetailsResponse userDetails = new UserDetailsResponse();
        userDetails.setAboutMe(userEntity.getAboutMe());
        userDetails.setContactNumber(userEntity.getContactNumber());
        userDetails.setCountry(userEntity.getCountry());
        userDetails.setDob(userEntity.getDob());
        userDetails.setEmailAddress(userEntity.getEmail());
        userDetails.setFirstName(userEntity.getFirstName());
        userDetails.setLastName(userEntity.getLastName());
        userDetails.setUserName(userEntity.getUserName());

        return new ResponseEntity(userDetails, HttpStatus.OK);
    }


}
