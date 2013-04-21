package com.disasterdroid.disasterdroid;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class DisasterDroid extends Activity implements SensorEventListener {
	PendingIntent pi;
	Intent i;
	AlarmManager mgr;
	SensorManager sensorManager;
	Sensor magnetometer;
	TextView strength;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disaster_droid);
		strength = (TextView) findViewById(R.id.strength);

		mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		i = new Intent(this, OnAlarmReceiver.class);

		boolean noAlarm = (PendingIntent.getBroadcast(this, 0, i,
				PendingIntent.FLAG_NO_CREATE) == null);
		if (noAlarm) {
			pi = PendingIntent.getBroadcast(this, 0, i, 0);
			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime(), 1000 * 60 * 10, pi);
		}

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		magnetometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(this, magnetometer,
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			strength.setText(Math.round(hypotenuse(event.values) * 100) / 100
					+ "uT");
		}
	}

	private double hypotenuse(float[] values) {
		double squareSum = 0;

		for (float value : values)
			squareSum += (value * value);

		return Math.sqrt(squareSum);
	}

}
