package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDAO;

    public UserEntity getUserDetails(String authtoken, String uuid) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity authTokenEntity = userDAO.getUserAuthToken(authtoken);
        if (authTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            if (authTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details");
            } else {
                UserEntity userEntity = userDAO.getUserByUUID(uuid);
                if (userEntity == null) {
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
                } else {
                    return userEntity;
                }
            }
        }

    }

    /**
     * delete the answer
     *
     * @param userId id of the user to be deleted.
     * @throws UserNotFoundException if the user with id doesn't exist.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String userId)
            throws UserNotFoundException{

        UserEntity userEntity = userDAO.getUserById(userId);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }
        return userDAO.deleteUser(userId);
    }

    public void isAdminUser(String authorization) throws AuthorizationFailedException {
        UserAuthTokenEntity authTokenEntity = userDAO.getUserAuthToken(authorization);
        if (authTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (authTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out. Sign in first to get user details");
        }
        UserEntity userEntity = userDAO.getUserByUUID(authTokenEntity.getUuid());
        if (!userEntity.getRole().equals("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }
    }
}
