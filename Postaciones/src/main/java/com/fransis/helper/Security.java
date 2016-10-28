package com.fransis.helper;

import android.app.Activity;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by francisco on 28/10/2016.
 */

public class Security {
    public static void exitAfterDate(Activity activity){

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Calendar timeLimit = Calendar.getInstance(TimeZone.getDefault());
        timeLimit.set(2099, 00, 01);

        if(timeLimit.before(calendar)){
            Log.v("TimeLimit", "time's up!.");
            activity.finish();
            System.exit(0);
        }else{
            Log.v("TimeLimit", "normal running.");
            long timeInMillis = timeLimit.getTimeInMillis();
            long timeInMillis1 = calendar.getTimeInMillis();
            int millisPerDay = 3600 * 24 * 1000;
            int days = (int)((timeInMillis - timeInMillis1)/ millisPerDay);
            Log.v("TimeLimit", days + " days remaining");
        }
    }
}
