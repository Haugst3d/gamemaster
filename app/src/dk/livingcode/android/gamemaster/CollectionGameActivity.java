package dk.livingcode.android.gamemaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vessel.VesselSDK;

import dk.livingcode.android.gamemaster.database.GamesDataSource;
import dk.livingcode.android.gamemaster.fragments.CollectionGameEditorFragment;
import dk.livingcode.android.gamemaster.fragments.CollectionGameViewFragment;
import dk.livingcode.android.gamemaster.fragments.ViewGameListFragment;
import dk.livingcode.android.gamemaster.model.CollectedGame;
import dk.livingcode.android.gamemaster.utility.Strings;

public class CollectionGameActivity extends SherlockFragmentActivity {
	public static final String Tag = GameMasterActivity.class.getSimpleName();
	private CollectionGameEditorFragment editorFragment;
	private CollectionGameViewFragment viewFragment;
	private CollectedGame currentGame;
	private boolean isEditing;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialise Vessel 
	    VesselSDK.initialize(getApplicationContext(), "Nlg0ZGdMcGJ0SXdab2NSTUtTdmhlUW41" );
		
		setContentView(R.layout.collection_game_activity);
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#000099'>Collection Game</font>"));
		setProgressBarIndeterminateVisibility(true);

		// Get game from intent data
		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			// Get data via the key
			currentGame = extras.getParcelable("CurrentCollectedGame");
			isEditing = extras.getBoolean("IsEditingGame");
		}

		if (isEditing) {
			ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// Inflate a custom action bar that contains the "done" button for saving changes to the contact
				View customActionBarView = inflater.inflate(R.layout.collection_game_editor_custom_action_bar, null);
				View saveMenuItem = customActionBarView.findViewById(R.id.collection_game_editor_custom_actionbar_save_menu_item);
				saveMenuItem.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// Save information in database
						CollectedGame cg = editorFragment.getCollectedGame();
						final SharedPreferences settings = getSharedPreferences("GameMaster_Preferences", Context.MODE_PRIVATE);

						String id = settings.getString("UserIdentifier", Strings.Empty);
						// Set the owner of the collected game
						cg.setUserId(id);
						
						saveGameToCollection(cg);
						
						// Pass the id of the game as a result
						Intent resultIntent = new Intent();
						resultIntent.putExtra("AddedGameId", cg.getGame().getId());
						setResult(ViewGameListFragment.ADDED_GAME_TO_COLLECTION, resultIntent);
						finish();
					}
				});

				// Show the custom action bar but hide the home icon and title
				actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
						ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME |
						ActionBar.DISPLAY_SHOW_TITLE);
				actionBar.setCustomView(customActionBarView);
			}

			editorFragment = (CollectionGameEditorFragment)getSupportFragmentManager().findFragmentById(android.R.id.content);
			if (editorFragment == null) {
				// Create a new fragment
				editorFragment = new CollectionGameEditorFragment();
				Bundle args = new Bundle();
				args.putParcelable("CurrentCollectedGame", currentGame);
				editorFragment.setArguments(args);
				getSupportFragmentManager().beginTransaction().add(android.R.id.content, editorFragment).commit();
			}	
		} else {
			// Set up fragment for just viewing
			viewFragment = (CollectionGameViewFragment)getSupportFragmentManager().findFragmentById(android.R.id.content);
			if (viewFragment == null) {
				// Create a new fragment
				viewFragment = new CollectionGameViewFragment();
				Bundle args = new Bundle();
				args.putParcelable("CurrentCollectedGame", currentGame);
				viewFragment.setArguments(args);
				getSupportFragmentManager().beginTransaction().add(android.R.id.content, viewFragment).commit();
			}
		}
	}

	private void saveGameToCollection(final CollectedGame cg) {
		if (cg == null) {
			return;
		}
		
		// TODO: Validate game data
		
		GamesDataSource database = new GamesDataSource(this);
		try {
			database.openWrite();
			database.addGameToCollection(cg);
		} catch (Exception e) {
		} finally {
			database.close();
		}
	}
}