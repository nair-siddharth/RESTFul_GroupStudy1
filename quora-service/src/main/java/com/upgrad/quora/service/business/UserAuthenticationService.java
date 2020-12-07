package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserAuthenticationService {
    @Autowired
    private UserDao userDAO;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity login(final String userName, final String password) throws AuthenticationFailedException {
        UserEntity checkUser = userDAO.getUserByUserName(userName);
        if(checkUser==null){
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }
        final   String encryptedPassword = cryptographyProvider.encrypt(password, checkUser.getSalt());

        if(encryptedPassword.equals(checkUser.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUser(checkUser);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(checkUser.getUuid(), now, expiresAt));

            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);
            userAuthToken.setUuid(UUID.randomUUID().toString());

            return userDAO.createAuthToken(userAuthToken);

        }else{
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity logout(final String userAuthToken) throws SignOutRestrictedException {
        UserAuthTokenEntity authTokenEntity = userDAO.getUserAuthToken(userAuthToken);

        if(authTokenEntity==null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }else{
            authTokenEntity.setLogoutAt(ZonedDateTime.now());
            authTokenEntity = userDAO.updateUserAuthToken(authTokenEntity);
            return authTokenEntity.getUser();
        }
    }


}
