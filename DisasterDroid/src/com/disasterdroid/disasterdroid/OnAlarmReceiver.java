package com.disasterdroid.disasterdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class OnAlarmReceiver extends BroadcastReceiver implements
		SensorEventListener {
	SensorManager sensorManager;
	Sensor magnetometer;
	private int count = 0;
	private float average = 0;
	private double latitude;
	private double longitude;
	private Context c;

	@Override
	public void onReceive(Context context, Intent intent) {
		c = context;
		final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				locationManager.removeUpdates(this);
				returnData(location);
			}

			public void onProviderDisabled(String provider) {
			}
			public void onProviderEnabled(String provider) {
			}
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		};
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		magnetometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(this, magnetometer,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			if (count < 50) {
				float[] values = event.values;
				double mag = hypotenuse(values);
				average += mag;
				count++;
			} else {
				sensorManager.unregisterListener(this);
			}
		}
	}

	private double hypotenuse(float[] values) {
		double squareSum = 0;

		for (float value : values)
			squareSum += (value * value);

		return Math.sqrt(squareSum);
	}
	
	private void returnData(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		average = average / 50.0f;
		System.currentTimeMillis();
		Toast.makeText(c, latitude + "\n" + longitude + "\n" + average, Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}
}