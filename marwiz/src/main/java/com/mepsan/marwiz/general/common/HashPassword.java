/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 27.01.2017 10:59:56
 */
package com.mepsan.marwiz.general.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashPassword {

    private BCryptPasswordEncoder encoder;

    public HashPassword() {
        encoder = new BCryptPasswordEncoder();
    }

    public boolean passwordMatches(String password, String hashPassword) {
        return encoder.matches(password, hashPassword);
    }

    public String encodePassword(String password) {
        return encoder.encode(password);
    }

}
