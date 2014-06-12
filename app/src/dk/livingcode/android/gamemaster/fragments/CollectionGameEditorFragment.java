package dk.livingcode.android.gamemaster.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import dk.livingcode.android.gamemaster.R;
import dk.livingcode.android.gamemaster.database.GamesDataSource;
import dk.livingcode.android.gamemaster.model.CollectedGame;
import dk.livingcode.android.gamemaster.model.Console;
import dk.livingcode.android.gamemaster.model.Region;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class CollectionGameEditorFragment extends SherlockFragment {
	private CollectedGame currentGame;

	public CollectionGameEditorFragment() {
	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);

		final Bundle b = getArguments();
		if (b != null) {
			currentGame = b.getParcelable("CurrentCollectedGame");
		}

		// TODO: Set up fields
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		final View view = inflater.inflate(R.layout.collection_game_editor_fragment, container, false);

		// Display a message, if the game is already in the users collection
		// Fetch from database, based on game id
		ArrayList<CollectedGame> alreadyInCollection = new ArrayList<CollectedGame>();
		GamesDataSource database = new GamesDataSource(getActivity());
		try {
			database.open();
			alreadyInCollection = database.getCollectedGames(currentGame.getGame().getId());
		} catch (Exception e) {
		} finally {
			database.close();
		}

		if (alreadyInCollection != null && alreadyInCollection.size() > 0) {
			// Make the container visible
			final LinearLayout existingGamesContainer = (LinearLayout)view.findViewById(R.id.collection_game_editor_fragment_existing_copies);
			if (existingGamesContainer != null) {
				existingGamesContainer.setVisibility(LinearLayout.VISIBLE);
				// Create an entry for each game
				for (CollectedGame g : alreadyInCollection) {
					View gameView = inflater.inflate(R.layout.collection_game_activity_existing_row, null);
					// Populate view with game data

					// Region
					// Acquisation date
					// Score
					
					// Make clickable, and open popup non-editable
					
					existingGamesContainer.addView(gameView);
				}
			}
		}
		
		// Databind fields
		
		database = new GamesDataSource(getActivity());
		database.open();

		final Spinner region = (Spinner)view.findViewById(R.id.collection_game_editor_fragment_spinner_region);
		if (region != null) {
			ArrayList<Region> allValues = database.getAllRegions();
			
			Collections.sort(allValues, new Comparator<Region>() {
		        @Override
		        public int compare(Region r1, Region r2) {
					return r1.getName().compareTo(r2.getName());
		        }
		    });
			
			ArrayAdapter<Region> adapter = new ArrayAdapter<Region>(getActivity(), android.R.layout.simple_spinner_item, allValues);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			region.setPrompt("Select region...");
			region.setAdapter(adapter);

			// Set currently selected region based on selected title
			if (currentGame != null && currentGame.getRegion() != null) {
				for (int i = 0;i < adapter.getCount();i++) {
					final Region filterRegion = adapter.getItem(i);
					if (filterRegion.getId() == currentGame.getRegion().getId()) {
						region.setSelection(i);
					}
				}	
			}
		}

		final Spinner console = (Spinner)view.findViewById(R.id.collection_game_editor_fragment_spinner_console);
		if (console != null) {
			ArrayList<Console> allValues = database.getAllConsoles();
			
			Collections.sort(allValues, new Comparator<Console>() {
		        @Override
		        public int compare(Console c1, Console c2) {
					return c1.getName().compareTo(c2.getName());
		        }
		    });
			
			ArrayAdapter<Console> adapter = new ArrayAdapter<Console>(getActivity(), android.R.layout.simple_spinner_item, allValues);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			console.setPrompt("Select console");
			console.setAdapter(adapter);

			// Set currently selected console based on selected title
			if (currentGame != null && currentGame.getConsole() != null) {
				for (int i = 0;i < adapter.getCount();i++) {
					final Console filterConsole = adapter.getItem(i);
					if (filterConsole.getId() == currentGame.getConsole().getId()) {
						console.setSelection(i);
					}
				}	
			}
		}
		
		database.close();

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
		inflater.inflate(R.menu.collection_game_editor_menu, menu);
	}	

	@Override
	public void onPrepareOptionsMenu(Menu menu) {	
		final MenuItem cancelMenu = menu.findItem(R.id.collection_game_editor_menu_cancel);
		cancelMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				getActivity().finish();

				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.collection_game_editor_menu_cancel:

			break;
		}

		return false;
	}

	public CollectedGame getCollectedGame() {
		CollectedGame g = new CollectedGame();
		
		// TODO: Get data from fields
		
		//g.setId(currentGame.getId());
		//g.setGame(currentGame.getGame());
		
		

		return currentGame;
	}
}