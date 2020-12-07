package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignUpBusinessService;
import com.upgrad.quora.service.business.UserAuthenticationService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    private SignUpBusinessService signUpService;
    @Autowired
    private UserAuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        // create user entity object
        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setSalt("1234abc");
        userEntity.setPassword(signupUserRequest.getPassword());

        //userEntity1 is persisted
        final UserEntity userEntity1 = signUpService.createUser(userEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse();
        signupUserResponse.setId(userEntity1.getUuid());
        signupUserResponse.setStatus("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity(signupUserResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signin",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> login(@RequestHeader("authorization") final String authorizationHeader) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorizationHeader.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthTokenEntity userAuthTokenEntity= authenticationService.login(decodedArray[0],decodedArray[1]);
        UserEntity user = userAuthTokenEntity.getUser();

        SigninResponse loggedInUser = new SigninResponse();
        loggedInUser.setId(UUID.fromString(user.getUuid()).toString());
        loggedInUser.setMessage("SIGNED IN SUCCESSFULLY");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access-token",userAuthTokenEntity.getAccessToken());

        return new ResponseEntity<SigninResponse>(loggedInUser,httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> logout(@RequestHeader("authorization") final String authorizationHeader) throws SignOutRestrictedException {
        String jwToken = authorizationHeader.split("Bearer ")[1];
        UserEntity user = authenticationService.logout(jwToken);
        SignoutResponse loggedOut = new SignoutResponse();
        loggedOut.setId(UUID.fromString(user.getUuid()).toString());
        loggedOut.setMessage("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(loggedOut,HttpStatus.OK);
    }

}
