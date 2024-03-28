package com.cooksys.socialmedia.services;

public interface ValidateService {


    boolean usernameIsAvailable(String username);

    boolean doesUsernameExist(String username);

}
