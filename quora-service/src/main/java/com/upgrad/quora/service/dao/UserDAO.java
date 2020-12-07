package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) throws SignUpRestrictedException {

        try{// Check if username already exists
            entityManager.createNamedQuery("userByUserName",UserEntity.class).setParameter("userName",
                    userEntity.getUserName()).getSingleResult();
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }catch (NoResultException e1){

            try{// Check if email already exists
                entityManager.createNamedQuery("userByEmail",UserEntity.class).setParameter("email",
                        userEntity.getEmail()).getSingleResult();
                throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
            }catch (NoResultException e2){
                // IF username and email id is new = persist user
                entityManager.persist(userEntity);
                return userEntity;

            }

        }

    }


    public UserEntity getUserByUserName(final String userName){
        try{
            return entityManager.createNamedQuery("userByUserName",UserEntity.class)
                    .setParameter("userName",userName).getSingleResult();

        }catch (NoResultException e){
            return null;
        }

    }

    public UserEntity getUserByEmail(final String email){
        try{
            return entityManager.createNamedQuery("userByEmail",UserEntity.class)
                    .setParameter("email",email).getSingleResult();

        }catch (NoResultException e){
            return null;
        }

    }

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity authTokenEntity){
        entityManager.persist(authTokenEntity);
        return authTokenEntity;
    }

    public UserAuthTokenEntity getUserAuthToken(final String jwToken){
        try{
            return entityManager.createNamedQuery("userAuthTokenByToken",UserAuthTokenEntity.class).
                    setParameter("accessToken",jwToken).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public UserAuthTokenEntity updateUserAuthToken(UserAuthTokenEntity authTokenEntity){
        authTokenEntity = entityManager.merge(authTokenEntity);
        return authTokenEntity;
    }

    public UserEntity getUserByUUID(String UUID){
        try{
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).
                    setParameter("uuid", UUID).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

}
