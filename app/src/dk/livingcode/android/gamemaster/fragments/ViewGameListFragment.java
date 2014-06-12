package dk.livingcode.android.gamemaster.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;

import dk.livingcode.android.gamemaster.CollectionGameActivity;
import dk.livingcode.android.gamemaster.GameViewActivity;
import dk.livingcode.android.gamemaster.R;
import dk.livingcode.android.gamemaster.adapters.GameListActivityAdapter;
import dk.livingcode.android.gamemaster.adapters.IGameSelectedListener;
import dk.livingcode.android.gamemaster.database.GamesDataSource;
import dk.livingcode.android.gamemaster.model.CollectedGame;
import dk.livingcode.android.gamemaster.model.Company;
import dk.livingcode.android.gamemaster.model.Console;
import dk.livingcode.android.gamemaster.model.Game;
import dk.livingcode.android.gamemaster.model.GameFilter;
import dk.livingcode.android.gamemaster.model.GameFilterResult;
import dk.livingcode.android.gamemaster.model.Region;
import dk.livingcode.android.gamemaster.model.Release;
import dk.livingcode.android.gamemaster.utility.AsyncServiceResult;
import dk.livingcode.android.gamemaster.utility.AsyncServiceTask;

public class ViewGameListFragment extends SherlockListFragment implements IGameSelectedListener {
	public static final int ADD_GAME_TO_COLLECTION = 234;
	public static final int ADDED_GAME_TO_COLLECTION = 235;
	public static final int CANCELLED_ADD_GAME_TO_COLLECTION = 236;
	
	private ArrayList<Integer> collection;
	private ArrayList<Game> allGames;
	private GameListActivityAdapter gla;
	private GameFilter currentFilter;
	private TextView topText;
	private TextView bottomText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle extras = getArguments();
		if (extras != null) {
			currentFilter = (GameFilter)extras.getParcelable("CurrentFilter");
			collection = extras.getIntegerArrayList("CurrentCollection");
		}		
	}
	
	public void onGameCollectedClicked(Game g, int index) {
		final Intent gameCollectIntent = new Intent(getActivity(), CollectionGameActivity.class);
		
		// Pass console, region and game to the activity
		final CollectedGame cg = new CollectedGame();
		cg.setGame(g);
		
		// User current filter setting to get the rest of the properties
		cg.setConsole(currentFilter.getConsole());
		cg.setRegion(currentFilter.getRegion());
							
		gameCollectIntent.putExtra("CurrentCollectedGame", cg);
		gameCollectIntent.putExtra("IsEditingGame", true);

		startActivityForResult(gameCollectIntent, ADDED_GAME_TO_COLLECTION);
	}
	
	public void onGameDetailsClicked(Game g, int index) {
		final Intent gameDetailsIntent = new Intent(getActivity(), GameViewActivity.class);
		// Get real game id from the button view
		gameDetailsIntent.putExtra("SelectedGame", g);

		startActivity(gameDetailsIntent);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.game_list_activity, container, false);

		final SwipeListView swipeListView = (SwipeListView)v.findViewById(android.R.id.list);
		swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
            	swipeListView.closeOpenedItems(position);
            }

            @Override
            public void onStartClose(int position, boolean right) {
            }

            @Override
            public void onClickFrontView(int position) {
            	swipeListView.closeOpenedItems(position);
            	
            	swipeListView.openAnimate(position);
            }

            @Override
            public void onClickBackView(int position) {
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            }
        });
		
		topText = (TextView)v.findViewById(R.id.game_list_activity_bottom_top_text);
		bottomText = (TextView)v.findViewById(R.id.game_list_activity_bottom_bottom_text);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle saveState) {
		new AsyncTaskRunner().execute(currentFilter);

		super.onActivityCreated(saveState);
	}

	public void setTextFilter(CharSequence filter) {
		if (gla != null) {
			gla.getFilter().filter(filter);
		}
	}

	public void sortGames(final String field) {
		gla.sortGamesList(field);
	}

	public void updateGames(final ArrayList<Integer> collection) {
		// Use collection to color
		gla.setSelections(collection);
		gla.notifyDataSetChanged();
	}

	public void setGameFilter(final GameFilter gf) {
		this.currentFilter = gf;
		
		gla.setGameFilter(this.currentFilter);

		applyFilter();
	}

	private void applyFilter() {
		if (currentFilter == null) {
			return;
		}

		// From an async call, retrieve games using the filter
		new AsyncTaskRunner().execute(currentFilter);
	}

	private class AsyncTaskRunner extends AsyncServiceTask<GameFilter, Void, GameFilterResult> {
		protected void onPreExecute() {
			//getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected GameFilterResult doInBackground(GameFilter... args) {
			GameFilterResult finalResult = new GameFilterResult();

			try {
				if (args != null && args.length > 0) {
					final GameFilter filter = args[0];

					GamesDataSource database = new GamesDataSource(getActivity());
					database.open();

					final ArrayList<Game> games = database.getAllGames();
					int totalReleases = database.getReleaseCount();

					database.close();

					// If no filter is applied, just return all games
					if (filter == null || filter.getIsEmpty()) {
						for (Game g : games) {
							if (g.getDefaultRelease().getPublisher() == null) {
								finalResult.setMessage("No publisher set for: " + g.getDefaultRelease().getTitle());	
							}
						}

						finalResult.setTotalGames(games.size());
						finalResult.setTotalGamesAfterFilter(games.size());
						finalResult.setTotalReleases(totalReleases);
						finalResult.setTotalReleasesAfterFilter(totalReleases);
						finalResult.setGames(games);

						return finalResult;
					}

					final ArrayList<Game> result = new ArrayList<Game>();
					for (Game g : games) {
						boolean matchDeveloper = false;
						final Company dev = filter.getDeveloper();
						if (dev != null && dev.getId() != -1) {
							final Company gd = g.getDeveloper();

							if (gd != null && dev.getId() == gd.getId()) {
								matchDeveloper = true;
							}
						} else {
							matchDeveloper = true;
						}

						if (g.getDefaultRelease().getPublisher() == null) {
							finalResult.setMessage("No publisher set for: " + g.getDefaultRelease().getTitle());	
						}

						boolean matchPublisher = false;
						final Company pub = filter.getPublisher();
						if (pub != null && pub.getId() != -1) {
							final Release r = g.getDefaultRelease();
							final Company rp = r.getPublisher();

							if (rp != null && pub.getId() == rp.getId()) {
								matchPublisher = true;
							}
						} else {
							matchPublisher = true;
						}

						boolean matchRegion = false;
						final ArrayList<Region> regs = filter.getRegions();
						if (regs != null && regs.size() > 0) {
							for (Region r : regs) {
								for (Release r2 : g.getReleases()) {
									if (r.getId() == r2.getRegion().getId()) {
										matchRegion = true;
									}
								}
							}
						} else {
							matchRegion = true;
						}

						boolean matchConsole = false;
						final ArrayList<Console> consoles = filter.getConsoles();
						if (consoles != null && consoles.size() > 0) {
							for (Console c : consoles) {
								if (c.getId() == g.getConsole().getId()) {
									matchConsole = true;
								}
							}
						} else {
							matchConsole = true;
						}

						boolean match = matchDeveloper & matchPublisher & matchRegion & matchConsole;

						if (match) {
							result.add(g);
						}
					}

					// Count releases
					int rs = 0;
					for (Game g : result) {
						if (g.getReleases() != null) {
							rs += g.getReleases().size();	
						}
					}
					
					finalResult.setTotalGames(games.size());
					finalResult.setTotalGamesAfterFilter(result.size());
					finalResult.setTotalReleases(totalReleases);
					finalResult.setTotalReleasesAfterFilter(rs);
					finalResult.setGames(result);

					return finalResult;
				}
			} catch (Exception ex) {
				setConnectionStatus(ex);
			}

			return null;
		}

		@Override
		protected void onPostExecute(GameFilterResult result) {
			if (getConnectionStatus() == AsyncServiceResult.SUCCESS) {
				allGames = result.getGames();

				if (allGames != null) {
					if (currentFilter.getRegion() == null) {
						topText.setText(result.getTotalGamesAfterFilter() + " titles, " + result.getTotalReleasesAfterFilter() + " releases, out of " + result.getTotalGames() + " and " + result.getTotalReleases() + " worldwide");	
					} else {
						topText.setText(result.getTotalGamesAfterFilter() + " " + currentFilter.getRegion().getCode() + " releases, out of " + result.getTotalReleases() + " worldwide");
					}
					
					bottomText.setText(result.getMessage());

					gla = new GameListActivityAdapter(getActivity(), ViewGameListFragment.this, R.layout.game_list_activity_row, allGames);
					gla.setSelections(collection);
					gla.setGameFilter(currentFilter);
					gla.sortGamesList("title");
					setListAdapter(gla);
				}
			} else {
				// If the result was not successful, handle it and notify the user
				super.handleStatus(getSherlockActivity());
			}
		}
	}
}