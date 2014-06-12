package dk.livingcode.android.gamemaster;

import com.vessel.VesselSDK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class GameMasterSplash extends Activity {
	//how long until we go to the next activity
	protected int _splashTime = 5000; 

	private volatile Thread splashTread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialise Vessel 
	    VesselSDK.initialize(getApplicationContext(), "Nlg0ZGdMcGJ0SXdab2NSTUtTdmhlUW41" );
		
		setContentView(R.layout.splash);

		startThread();
	}

	public synchronized void startThread() {
		if (splashTread == null) {
			splashTread = new Thread() { 
				@Override
				public void run() {
					runWork();

					super.run();
				}
			};

			splashTread.start();
		}
	}

	public synchronized void stopThread() {
		if (splashTread != null) {
			Thread moribund = splashTread;
			splashTread = null;
			moribund.interrupt();
		}
	}

	public void runWork() {
		while (Thread.currentThread() == splashTread) {
			// do stuff which can be interrupted if necessary

			try {
				synchronized (splashTread) {
					splashTread.wait(_splashTime);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				finish();

				//start a new activity
				Intent i = new Intent();
				i.setClass(this, GameMasterActivity.class);
				startActivity(i);

				stopThread();
			}
		}
	}

	//Function that will handle the touch
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (splashTread) {
				splashTread.notifyAll();
			}
		}

		return true;
	}
}