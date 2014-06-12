package dk.livingcode.android.gamemaster;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.vessel.VesselSDK;

import dk.livingcode.android.gamemaster.database.GamesDataSource;
import dk.livingcode.android.gamemaster.fragments.ViewGameDialogListener;
import dk.livingcode.android.gamemaster.fragments.ViewGameListFragment;
import dk.livingcode.android.gamemaster.model.Company;
import dk.livingcode.android.gamemaster.model.Console;
import dk.livingcode.android.gamemaster.model.Game;
import dk.livingcode.android.gamemaster.model.GameFilter;
import dk.livingcode.android.gamemaster.model.Region;
import dk.livingcode.android.gamemaster.utility.Strings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.SearchView;
import android.widget.Toast;

public class GameListActivity extends SherlockFragmentActivity implements ViewGameDialogListener {
	public static int GAME_FILTER_REQUEST = 255;
	private ViewGameListFragment frag;
	private GameFilter currentFilter;
	private ArrayList<Integer> collection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialise Vessel 
	    VesselSDK.initialize(getApplicationContext(), "Nlg0ZGdMcGJ0SXdab2NSTUtTdmhlUW41" );
		
		setProgressBarIndeterminateVisibility(false);
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#000099'>Games</font>"));
		currentFilter = tryGetFilter();

		// Get collection from database
		GamesDataSource database = new GamesDataSource(this);
		database.open();
		try {
			collection = database.getCollectedGameIds();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
		}
		
		frag = (ViewGameListFragment)getSupportFragmentManager().findFragmentById(android.R.id.content);
		if (frag == null) {
			// Create a bundle for passing the current filter
			final Bundle b = new Bundle();
			b.putParcelable("CurrentFilter", currentFilter);
			b.putIntegerArrayList("CurrentCollection", collection);

			// Create a new fragment
			frag = new ViewGameListFragment();
			frag.setArguments(b);

			getSupportFragmentManager().beginTransaction().add(android.R.id.content, frag).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		switch (itemId) {
		case R.id.menu_game_list_sort_title:
			frag.sortGames("title");
			break;
		case R.id.menu_game_list_sort_region:
			frag.sortGames("region");
			break;
		case R.id.menu_game_list_sort_published:
			frag.sortGames("published");
			break;
		case R.id.menu_game_list_sort_genre:
			frag.sortGames("genre");
			break;
		case R.id.menu_game_list_filter:
			final Intent i = new Intent(GameListActivity.this, GameFilterActivity.class);
			i.putExtra("CurrentGameFilter", currentFilter);

			// Set the request code to any code you like, you can identify the
			// callback via this code
			startActivityForResult(i, GAME_FILTER_REQUEST);
			break;
		}

		return super.onOptionsItemSelected(item);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.game_list_menu, menu);

		final SearchView searchView = (SearchView) menu.findItem(R.id.menu_game_list_search).getActionView();
		if (searchView != null) {
			final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextChange(String newText) {
					frag.setTextFilter(newText);

					return true;
				}

				@Override
				public boolean onQueryTextSubmit(String query) {
					frag.setTextFilter(query);

					return true;
				}
			};

			searchView.setQueryHint("Search in games");
			searchView.setOnQueryTextListener(queryTextListener);
		}

		return true;
	}

	@Override
	public void onFinishViewing(final int id, final String action) {
		GamesDataSource database = new GamesDataSource(this);
		database.open();

		Game selected = database.getGame(id);

		database.close();

		if (selected == null) {
			return;
		}
		
		if (action.equals("Add")) {
			addToCollection(selected);
			Toast.makeText(this, selected.getDefaultRelease().getTitle() + " was added to your collection", Toast.LENGTH_SHORT).show();
		} else {
			removeFromCollection(selected);
			Toast.makeText(this, selected.getDefaultRelease().getTitle() + " was removed from your collection", Toast.LENGTH_SHORT).show();
		}

		frag.updateGames(collection);
	}
	
	private void addToCollection(final Game g) {
		collection.add(g.getId());
	}
	
	private void removeFromCollection(final Game g) {
		for (int i = collection.size() -1 ; i >= 0; i--) {
			final Integer cg = collection.get(i);
			if (cg == g.getId()) {
				collection.remove(i);
				break;
			}
		}
	}

	@Override
	public void onViewDetails(final int id) {
		final Intent gameDetailsIntent = new Intent(GameListActivity.this, GameViewActivity.class);
		gameDetailsIntent.putExtra("SelectedGameId", id);

		startActivity(gameDetailsIntent);
	}

	public GameFilter tryGetFilter() {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs != null) {
			int consoleId = prefs.getInt("game_filter_pref_console_id", -1);
			int regionId = prefs.getInt("game_filter_pref_region_id", -1);
			int publisherId = prefs.getInt("game_filter_pref_publisher_id", -1);
			int developerId = prefs.getInt("game_filter_pref_developer_id", -1);
			String releaseDate = prefs.getString("game_filter_pref_release_date", Strings.Empty);

			GamesDataSource database = new GamesDataSource(this);
			database.open();
			
			final GameFilter gf = new GameFilter(
					!Strings.isNullOrEmpty(releaseDate) ? releaseDate : null, 
							developerId > -1 ? database.getCompany(developerId) : null, 
									publisherId > -1 ? database.getCompany(publisherId) : null,
											regionId > -1 ? new ArrayList<Region>() : null,
													new ArrayList<Console>());

			final Region r = database.getRegion(regionId);
			if (r != null) {
				gf.setRegion(r);
			}
			
			final Console c = database.getConsole(consoleId);
			if (c != null) {
				gf.setConsole(c);
			} else {
				final Console nes = database.getConsoleByCode("NES");
				gf.setConsole(nes);
			}	

			database.close();

			return gf;
		}

		return new GameFilter();
	}

	public void persistFilter() {
		if (currentFilter != null) {
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			if (prefs != null) {
				Editor e = prefs.edit();
				final Console c = currentFilter.getConsole();
				e.putInt("game_filter_pref_console_id", c != null ? c.getId() : -1);

				final Region r = currentFilter.getRegion();
				e.putInt("game_filter_pref_region_id", r != null ? r.getId() : -1);

				final Company p = currentFilter.getPublisher();
				e.putInt("game_filter_pref_publisher_id", p != null ? p.getId() : -1);

				final Company d = currentFilter.getDeveloper();
				e.putInt("game_filter_pref_developer_id", d != null ? d.getId() : -1);

				final String rd = currentFilter.getReleased();
				e.putString("game_filter_pref_release_date", !Strings.isNullOrEmpty(rd) ? rd : Strings.Empty);

				e.commit();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == GAME_FILTER_REQUEST) {
			if (data.hasExtra("NewGameFilter")) {
				currentFilter = data.getParcelableExtra("NewGameFilter");
				persistFilter();
				frag.setGameFilter(currentFilter);
			}
		} else if (resultCode == ViewGameListFragment.ADDED_GAME_TO_COLLECTION) {
			if (data.hasExtra("AddedGameId")) {
				int id = data.getIntExtra("AddedGameId", -1);
				if (id != -1) {
					onFinishViewing(id, "Add");
				}
			}
		}
	}
}
