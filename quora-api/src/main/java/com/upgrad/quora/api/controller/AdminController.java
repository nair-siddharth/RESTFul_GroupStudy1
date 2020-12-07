package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    CommonBusinessService commonBusinessService;

    @DeleteMapping(path = "/users/{userId}")
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId") final String userId,
                                                         @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {

        commonBusinessService.isAdminUser(authorization);
        commonBusinessService.deleteUser(userId);

        return new ResponseEntity<>(new UserDeleteResponse().id(userId).status("USER SUCCESSFULLY DELETED"), HttpStatus.OK);
    }
}
