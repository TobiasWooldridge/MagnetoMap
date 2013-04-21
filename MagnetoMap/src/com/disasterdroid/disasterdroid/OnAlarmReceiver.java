package com.disasterdroid.disasterdroid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.GsonBuilder;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;

public class OnAlarmReceiver extends BroadcastReceiver implements
		SensorEventListener {
	SensorManager sensorManager;
	Sensor magnetometer;
	private int count = 0;
	private float average = 0;
	private double latitude;
	private double longitude;
	Context c;
	private PowerManager.WakeLock wl;

	@Override
	public void onReceive(Context context, Intent intent) {
		c = context;
		final LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
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
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		magnetometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(this, magnetometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		PowerManager pm = (PowerManager) c
				.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DisasterDroid");
		wl.acquire();
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
		double time = (double) System.currentTimeMillis();

		Map<String, Double> comment = new HashMap<String, Double>();
		comment.put("lat", latitude);
		comment.put("long", longitude);
		comment.put("mag", (double) average);
		comment.put("time", time);
		String json = new GsonBuilder().create().toJson(comment, Map.class);
		MakeRequest task = new MakeRequest();
		task.execute("http://magnetapp.wooldridge.id.au/readings/new", json);
	}

	public class MakeRequest extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... data) {
			try {
				HttpPost httpPost = new HttpPost(data[0]);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				nameValuePairs.add(new BasicNameValuePair("samples", data[1]));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				new DefaultHttpClient().execute(httpPost);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute() {
			wl.release();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}
}