/*
 * Copyright (c) 2015. This product is a brain-product of Jacob Langholz, Jonathan Coons, and Caleb Jaeger. The collective content within was created by them and them alone to fulfill the requirements of the mobile gps application project for TCSS 450.
 */

package tcss450.gps_app_phase_i;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jake on 4/17/15.
 */
public class AuthTable
{

    public static final String db_name = "AUTH_DB";
    public static final String table_name = "AUTHENTICATION";
    public static final String key_user_id = "Email";
    public static final String key_user_pass = "Password";
    public static final String key_sec_question = "SecurityQuestion";
    public static final String key_sec_answer = "SecurityAnswer";

    private Context ctxt;
    private Cursor crs;
    private DatabaseHelper my_helper;
    private SQLiteDatabase my_db;

    public AuthTable(Context context)
    {
        ctxt = context;
    }

    private class DatabaseHelper extends SQLiteOpenHelper
    {

        DatabaseHelper(Context context)
        {
            super(context, db_name, null, 1);
        }

        public void onCreate(SQLiteDatabase db)
        {
            Log.i("Database", "Creating Database...");
            db.execSQL("CREATE TABLE IF NOT EXISTS AUTHENTICATION (" +
                    "Email TEXT PRIMARY KEY, " +
                    "Password TEXT, " +
                    "SecurityQuestion TEXT, " +
                    "SecurityAnswer TEXT)");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            //No changes to make so far...
        }
    }


    public boolean add_user(final String user, final String pass, final String question,
                            final String answer)
    {
        if (!user.contains("@") || pass.length() < 1 || question.length() < 1 ||
                answer.length() < 1)
            return false;
        my_helper = new DatabaseHelper(ctxt);
        my_db = my_helper.getWritableDatabase();

        ContentValues init_vals = new ContentValues();
        init_vals.put(key_user_id, user);
        init_vals.put(key_user_pass, pass);
        init_vals.put(key_sec_question, question);
        init_vals.put(key_sec_answer, answer);

        my_db.insert(table_name, null, init_vals);
        my_helper.close();
        return true;
    }

    protected boolean user_exist(final String user_id)
    {
        my_helper = new DatabaseHelper(ctxt);
        my_db = my_helper.getReadableDatabase();
        crs = my_db.rawQuery("SELECT Email FROM AUTHENTICATION WHERE Email = ?",
                new String[]{user_id});

        if (crs != null)
        {
            crs.close();
            my_helper.close();
            return true;
        }
        else
            return false;
    }

    protected boolean authenticate(final String user_id, final String pass)
    {
        if (this.user_exist(user_id))
        {
            my_helper = new DatabaseHelper(ctxt);
            my_db = my_helper.getReadableDatabase();
            crs = my_db.rawQuery("SELECT Password FROM AUTHENTICATION WHERE Email = ?",
                    new String[]{user_id});

            if (crs != null)
            {
                crs.close();
                my_helper.close();
                return true;
            }
        }

        crs.close();
        my_helper.close();
        return false;
    }

    protected String[] fetch_user(final String user_id, final String pass)
    {
        my_helper = new DatabaseHelper(ctxt);
        my_db = my_helper.getReadableDatabase();
        crs = my_db.rawQuery("SELECT Email, Password, SecurityQuestion, SecurityAnswer " +
                        "FROM AUTHENTICATION WHERE Email = ? AND Password = ?",
                new String[]{user_id, pass});

        if (crs == null)
            return null;

        String[] user_info = new String[4];
        for (int i = 0; i < 5; i++)
            user_info[i] = crs.getString(i + 1);

        crs.close();
        my_helper.close();
        return user_info;
    }
}
