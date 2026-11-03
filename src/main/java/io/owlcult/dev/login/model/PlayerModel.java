package io.owlcult.dev.login.model;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import net.minecraft.world.level.GameType;

import com.github.owl_cult_devteam.owlboost.Option;

public class PlayerModel {
    public String nickname;
    public Option<String> password_hash;
    public GameType gameMode;
    
    public static String hashPassword(String password) {
    	try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			return Arrays.toString(
					messageDigest.digest(password.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException();
		}
    }
}
