package dk.livingcode.android.gamemaster;

import android.os.Bundle;
import android.text.Html;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vessel.VesselSDK;

import dk.livingcode.android.gamemaster.database.GamesDataSource;
import dk.livingcode.android.gamemaster.model.Game;

public class GameViewActivity extends SherlockFragmentActivity {
	private Game selectedGame;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialise Vessel 
	    VesselSDK.initialize(getApplicationContext(), "Nlg0ZGdMcGJ0SXdab2NSTUtTdmhlUW41" );
		
		setProgressBarIndeterminateVisibility(false);
		
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#000099'>Game details</font>"));
		
		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			final int selectedGameId = extras.getInt("SelectedGameId");
			
			GamesDataSource database = new GamesDataSource(this);
			database.open();
			
			selectedGame = database.getGame(selectedGameId);
			
			database.close();
		}
		
	}
}