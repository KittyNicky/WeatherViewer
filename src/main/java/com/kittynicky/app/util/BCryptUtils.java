package com.kittynicky.app.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class BCryptUtils {

    public static String getHash(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean isVerified(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }
}
