/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import junit.framework.TestCase;

/**
 * Created by Jake on 5/28/2015.
 */
public class VerificationTest extends TestCase
{
    public void testEmail_blank()
    {
        int retVal = Verification.isEmailValid("");
        boolean testVal = false;
        if (retVal == 1) testVal = true;
        assertTrue("Blank email undetected.", testVal);
    }

    public void testEmail_symbols()
    {
        int retVal = Verification.isEmailValid("*");
        boolean testVal = false;
        if (retVal == 11) testVal = true;
        assertTrue("Invalid symbols undetected.", testVal);
    }

    public void testEmail_user()
    {
        int retVal = Verification.isEmailValid("@gmail.com");
        boolean testVal = false;
        if (retVal == 2) testVal = true;
        assertTrue("Missing user undetected.", testVal);
    }

    public void testEmail_at()
    {
        int retVal = Verification.isEmailValid("test.com");
        boolean testVal = false;
        if (retVal == 3) testVal = true;
        assertTrue("No website undetected.", testVal);
    }

    public void testEmail_domain()
    {
        int retVal = Verification.isEmailValid("test@.com");
        boolean testVal = false;
        if (retVal == 4) testVal = true;
        assertTrue("Absent domain undetected.", testVal);
    }

    public void testEmail_period()
    {
        int retVal = Verification.isEmailValid("test@gmailcom");
        boolean testVal = false;
        if (retVal == 5) testVal = true;
        assertTrue("Absent period undetected", testVal);
    }

    public void testEmail_top_domain()
    {
        int retVal = Verification.isEmailValid("test@gmail.");
        boolean testVal = false;
        if (retVal == 6) testVal = true;
        assertTrue("Absent top domain undetected.", testVal);
    }

    public void testPassword_short()
    {
        int retVal = Verification.isPasswordValid("T3$t");
        boolean testVal = false;
        if (retVal == 7) testVal = true;
        assertTrue("Short password undetected.", testVal);
    }

    public void testPassword_upper()
    {
        int retVal = Verification.isPasswordValid("t3$tpassword");
        boolean testVal = false;
        if (retVal == 8) testVal = true;
        assertTrue("No uppercase undetected.", testVal);
    }

    public void testPassword_lower()
    {
        int retVal = Verification.isPasswordValid("T3$TPASSWORD");
        boolean testVal = false;
        if (retVal == 9) testVal = true;
        assertTrue("No lowercase undetected.", testVal);
    }

    public void testPassword_special()
    {
        int retVal = Verification.isPasswordValid("T3sTpassword");
        boolean testVal = false;
        if (retVal == 10) testVal = true;
        assertTrue("Absent special char undetected.", testVal);
    }
}
