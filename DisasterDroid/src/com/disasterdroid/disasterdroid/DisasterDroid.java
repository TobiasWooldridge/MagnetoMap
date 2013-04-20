package com.disasterdroid.disasterdroid;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

public class DisasterDroid extends Activity {
	PendingIntent pi;
	Intent i;
	AlarmManager mgr;
	
	TextView strength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_droid);
        strength = (TextView)findViewById(R.id.strength);
        
        mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        i = new Intent(this, OnAlarmReceiver.class);
        pi = PendingIntent.getBroadcast(this, 0, i, 0);
        
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000*10, pi);
    }
    
    protected void onStop() {
    	super.onStop();
    	Intent intent = new Intent(this, OnAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


    
}
