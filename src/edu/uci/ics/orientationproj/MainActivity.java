package edu.uci.ics.orientationproj;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {

	private TextView xAxisLabel;
	private TextView yAxisLabel;
	private TextView zAxisLabel;
	private SensorManager mSensorManager;
	private SensorEventListener mEventListenerOrientation;
	private float xAxis;
	private float yAxis;
	private float zAxis;
	private MediaPlayer mp;
	private AssetFileDescriptor afd1;
	private AssetFileDescriptor afd2;
	private AssetFileDescriptor afd3;
	private AssetFileDescriptor afd4;
	private AssetFileDescriptor afd5;
	
	private void updateUI()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				xAxisLabel.setText(""+xAxis);
				yAxisLabel.setText(""+yAxis);
				zAxisLabel.setText(""+zAxis);
			}
		});
	}
	
	synchronized void playAudio(AssetFileDescriptor afd)
	{
		if (mp.isPlaying())
			return;
		else
		{
			try {
				mp.reset();
				mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				Log.d("playAudio:", e+"\n"+"afd:"+afd.toString());
				e.printStackTrace();
			} catch (IllegalStateException e) {
				Log.d("playAudio:", e+"\n"+"afd:"+afd.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("playAudio:", e+"\n"+"afd:"+afd.toString());
				e.printStackTrace();
			}
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        xAxisLabel = (TextView) findViewById(R.id.editText1);
        yAxisLabel = (TextView) findViewById(R.id.editText2);
        zAxisLabel = (TextView) findViewById(R.id.editText3);
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        mEventListenerOrientation = new SensorEventListener(){
        	
        	@Override
        	public void onAccuracyChanged(Sensor sensor, int accuracy)
        	{
        		
        	}

			@Override
			public void onSensorChanged(SensorEvent event)
			{
				float[] values = event.values;
				xAxis = values[0];
				yAxis = values[1];
				zAxis = values[2];
				updateUI();
				if (yAxis >= 9f) // normal position
				{
					playAudio(afd1);
				}
				else if (yAxis <= -9f) // upside down position
				{
					playAudio(afd2);
				}
				else if(xAxis >= 9f) // left position
				{
					playAudio(afd3);
				}
				else if(xAxis <= -9f) // right position
				{
					playAudio(afd4);
				}
				else if (zAxis >= 9f) // laying down position (like on a table or something)
				{
					playAudio(afd5);
				}
			}
        };
        
        mp = new MediaPlayer();
        afd1 = getApplicationContext().getResources().openRawResourceFd(R.raw.catmeow);
        afd2 = getApplicationContext().getResources().openRawResourceFd(R.raw.bark);
        afd3 = getApplicationContext().getResources().openRawResourceFd(R.raw.eagle);
        afd4 = getApplicationContext().getResources().openRawResourceFd(R.raw.whale);
        afd5 = getApplicationContext().getResources().openRawResourceFd(R.raw.wolfhowl);
    }
    
    

    @Override
    public void onResume()
    {
    	super.onResume();
    	mSensorManager.registerListener(mEventListenerOrientation, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	mSensorManager.unregisterListener(mEventListenerOrientation);
    	if (mp.isPlaying())
    	{
    		mp.stop();
    	}
    }
    
    @Override
    public void onStop()
    {
    	super.onStop();
    	mSensorManager.unregisterListener(mEventListenerOrientation);
    	if (mp.isPlaying())
    	{
    		mp.stop();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
