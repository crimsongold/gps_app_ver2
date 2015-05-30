/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by Jake on 5/26/2015.
 */
public class LoginTest extends ActivityInstrumentationTestCase2<Login>
{


    private Solo solo;

    public LoginTest()
    {
        super(Login.class);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testReqFields()
    {
        solo.unlockScreen();

        solo.enterText(solo.getEditText("email"), "");
        solo.clickOnButton("Log in");
        boolean textFound = solo.searchText("Email") || solo.searchText("Password");
        assertTrue("Required fields not entered, failed", textFound);
    }

    public void testOrientation()
    {
        solo.enterText(solo.getEditText("email"), "test.email@gmail.com");
        solo.enterText(solo.getEditText("password"), "T3STp@ssword");

        solo.setActivityOrientation(Solo.LANDSCAPE);
        boolean textFound = solo.searchText("test.email@gmail.com");
        assertTrue("Orientation change failed", textFound);

        solo.setActivityOrientation(Solo.PORTRAIT);
        textFound = solo.searchText("test.email@gmail.com");
        assertTrue("Orientation change failed", textFound);
    }

    public void testResetButton()
    {
        solo.enterText(solo.getEditText("forgot_email_text"), "test.email@gmail.com");

        solo.clickOnButton("Reset password");
        boolean textFound = !solo.searchText("Email");
        assertTrue("Reset failed", textFound);
    }

    public void testLogInButton()
    {
        solo.clickOnButton("Log in");
        boolean textFound = solo.searchText("Logging in.");
        assertTrue("Login failed", textFound);
    }

    public void testRegisterButton()
    {
        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("Opening registraion.");
        assertTrue("Failed to switch intents to Register", textFound);
    }

    public void testResetPassButton()
    {
        solo.clickOnButton("Reset password");
        boolean textFound = solo.searchText("Opening password reset.");
        assertTrue("Failed to start password reset", textFound);
    }
}
