package dk.livingcode.android.gamemaster;

import android.os.Bundle;
import android.text.Html;

import com.actionbarsherlock.app.SherlockActivity;
import com.vessel.VesselSDK;

public class CollectionActivity extends SherlockActivity {
	public static final String Tag = CollectionActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialise Vessel 
	    VesselSDK.initialize(getApplicationContext(), "Nlg0ZGdMcGJ0SXdab2NSTUtTdmhlUW41" );
		
		setContentView(R.layout.collection_activity);
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#000099'>Collection</font>"));
		setProgressBarIndeterminateVisibility(true);
		
		
		
	}
}