package dk.livingcode.android.gamemaster;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockListActivity;
import com.vessel.VesselSDK;

import dk.livingcode.android.gamemaster.adapters.ConsoleListActivityAdapter;
import dk.livingcode.android.gamemaster.database.GamesDataSource;
import dk.livingcode.android.gamemaster.model.Console;

import android.os.Bundle;
import android.text.Html;

public class ConsoleListActivity extends SherlockListActivity {
	private ArrayList<Console> allConsoles;
	private ConsoleListActivityAdapter activityAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialise Vessel 
	    VesselSDK.initialize(getApplicationContext(), "Nlg0ZGdMcGJ0SXdab2NSTUtTdmhlUW41" );
		
		setProgressBarIndeterminateVisibility(false);
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#000099'>Consoles</font>"));
		setContentView(R.layout.console_list_activity);

		GamesDataSource database = new GamesDataSource(this);
		database.open();
		
		this.allConsoles = database.getAllConsoles();

		database.close();
		
		this.updateActivityList();
	}

	private void updateActivityList() {
		if (allConsoles != null) {
			activityAdapter = new ConsoleListActivityAdapter(this, R.layout.console_list_activity_row, allConsoles);
			setListAdapter(activityAdapter);
		}
	}
}