package io.owlcult.dev.login.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Player {

    public String nickname;
    public String password_hash;
    
    public String hashPassword(String password) {
    	try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			return Arrays.toString(
					messageDigest.digest(password.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException();
		}
    }
}
