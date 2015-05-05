/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

/**
 * Created by caleb on 5/4/15.
 */
public class Verification {

    //TODO I think that these should return a string or result number. A boolean doesn't provide enough info.
    public static final int VALID_EMAIL = 0;
    public static final int VALID_PASSWORD = 0;

    public static final int BLANK = 1;
    public static final int NO_USER = 2;
    public static final int NO_AT = 3;
    public static final int NO_DOMAIN_NAME = 4;
    public static final int NO_DOT = 5;
    public static final int NO_TOP_DOMAIN = 6;
    public static final int INVALID_SYMBOLS = 11;

    public static final int SHORT_PASSWORD = 7;
    public static final int NO_UPPER = 8;
    public static final int NO_LOWER = 9;
    public static final int NO_SPECIAL = 10;


    /**
     * Checks to see whether or not the email is valid and exists in the user database.
     *
     * @param email is the string representing the email to be input.
     * @return boolean indicating whether or not the email is valid.
     */
    public static int isEmailValid(final String email) {
        //pattern for valid password
        //String pattern = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        //TODO: Replace this with your own logic
        if(email.isEmpty()){
            return BLANK;
        }else if(email.matches(".*[+\\-,!#$%^&*();\\\\/|<>\"']+.*")){
            return INVALID_SYMBOLS;
        }else if(!email.matches("[a-zA-Z0-9._%+-]+.*")){
            return NO_USER;
        }else if(!email.matches("^[a-zA-Z0-9._%+-]+@.*")){
            return NO_AT;
        }else if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-].*")){
            return NO_DOMAIN_NAME;
        }else if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\..*")){
            return NO_DOT;
        }else if(!email.matches("^[a-zA-Z0-9._%+-]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
            return NO_TOP_DOMAIN;
        }
        return VALID_EMAIL;
    }

    /**
     * Checks to see whether or not the password is valid.
     *
     * @param password is the string representing the password to be checked.
     * @return boolean representing whether or not the password is valid.
     */
    public static int isPasswordValid(final String password)
    {
        if (!password.matches(".{6,}")){
            return SHORT_PASSWORD;
        }else if(!password.matches(".*[A-Z]+.*")){
            return NO_UPPER;
        }else if(!password.matches(".*[a-z]+.*")){
            return NO_LOWER;
        }else if(!password.matches(".*[+\\-.,!@#$%^&*();\\\\/|<>\"']+.*")){
            return NO_SPECIAL;
        }
        return VALID_PASSWORD;
    }
}
