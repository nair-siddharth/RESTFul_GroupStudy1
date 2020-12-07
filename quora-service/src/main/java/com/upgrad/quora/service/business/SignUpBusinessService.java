package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDAO;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignUpBusinessService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordCryptographyProvider cryptoProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity newUser) throws SignUpRestrictedException {
        String[] encryptedText = cryptoProvider.encrypt(newUser.getPassword());
        newUser.setSalt(encryptedText[0]);
        newUser.setPassword(encryptedText[1]);
        return userDAO.createUser(newUser);
    }

}
