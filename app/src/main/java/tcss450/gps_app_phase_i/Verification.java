/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

/**
 * Created by caleb on 5/4/15.
 */
public class Verification {

    //TODO I think that these should return a string or result number. A boolean doesn't provide enough info.

    /**
     * Checks to see whether or not the email is valid and exists in the user database.
     *
     * @param email is the string representing the email to be input.
     * @return boolean indicating whether or not the email is valid.
     */
    public static boolean isEmailValid(final String email)
    {
        //pattern for valid password
        String pattern = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        //TODO: Replace this with your own logic
        boolean result = email.matches(pattern);
        return email.contains("@") ;//&& user_base.user_exist(email);
    }

    /**
     * Checks to see whether or not the password is valid.
     *
     * @param password is the string representing the password to be checked.
     * @return boolean representing whether or not the password is valid.
     */
    public static boolean isPasswordValid(final String password)
    {
        //TODO: Replace this with your own logic
        return password.length() >= 7;
    }
}
