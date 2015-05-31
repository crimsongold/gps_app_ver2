/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by Jake on 5/27/2015.
 */
public class MyAccountTest extends ActivityInstrumentationTestCase2<MyAccount> {


    private Solo solo;

    public MyAccountTest() {
        super(MyAccount.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testStartDateButton() {
        solo.clickOnButton("Start date");
        boolean textFound = solo.searchText("Selecting start date.");
        assertTrue("Start date picker failed", textFound);
    }

    public void testEndDateButton() {
        solo.clickOnButton("End date");
        boolean textFound = solo.searchText("Selecting end date.");
        assertTrue("End date picker failed", textFound);
    }

    public void testTrackingButton() {
        solo.clickOnButton("View tracking data");
        boolean textFound = solo.searchText("Displaying data.");
        assertTrue("Tracking data display failed", textFound);
    }

    public void testLogoutButton() {
        solo.clickOnButton("Logout");
        boolean textFound = solo.searchText("Email");
        assertTrue("Reset failed", textFound);
    }

    /*
    I feel like I need a test to figure out if the datepickers are actually properly
    setting the date fields, but I am not sure how to do this with Robotium.
     */
}