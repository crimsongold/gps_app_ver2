/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;

/**
 * Created by Jake on 5/26/2015.
 */
public class ForgotPassTest extends ActivityInstrumentationTestCase2<ForgotPass>
{

    private Solo solo;

    public ForgotPassTest()
    {
        super(ForgotPass.class);
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

    public void testOrientation()
    {
        EditText t_edit = (EditText) solo.getView(R.id.forgot_email_text);
        solo.enterText(t_edit, "test.email@gmail.com");

        solo.setActivityOrientation(Solo.LANDSCAPE);
        boolean textFound = solo.searchText("test.email@gmail.com");
        assertTrue("Orientation change failed", textFound);

        solo.setActivityOrientation(Solo.PORTRAIT);
        textFound = solo.searchText("test.email@gmail.com");
        assertTrue("Orientation change failed", textFound);
    }

    public void testResetButton()
    {
        solo.clickOnButton(solo.getString(R.string.forgot_password_reset));
        boolean textFound = solo.searchText(solo.getString(R.string.validate_email_blank));
        assertTrue("Reset failed", textFound);
    }

    public void testCancelButton()
    {
        solo.clickOnButton(solo.getString(R.string.forgot_password_cancel));
        solo.assertCurrentActivity("Activity cancel failed.", Login.class);
    }
}
