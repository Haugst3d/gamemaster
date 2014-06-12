package dk.livingcode.android.gamemaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.actionbarsherlock.app.SherlockActivity;
import com.vessel.VesselSDK;

import dk.livingcode.android.gamemaster.database.GamesDataSource;
import dk.livingcode.android.gamemaster.model.Company;
import dk.livingcode.android.gamemaster.model.Console;
import dk.livingcode.android.gamemaster.model.GameFilter;
import dk.livingcode.android.gamemaster.model.Region;
import dk.livingcode.android.gamemaster.utility.Strings;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class GameFilterActivity extends SherlockActivity {
	private GameFilter currentFilter;
	private boolean isResetting = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialise Vessel 
	    VesselSDK.initialize(getApplicationContext(), "Nlg0ZGdMcGJ0SXdab2NSTUtTdmhlUW41" );
		
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#000099'>Set filter</font>"));
		setProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.game_filter_activity);

		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			// Get data via the key
			currentFilter = extras.getParcelable("CurrentGameFilter");
		}

		GamesDataSource database = new GamesDataSource(this);
		database.open();

		final Spinner region = (Spinner)findViewById(R.id.spinnerRegion);
		if (region != null) {
			ArrayList<Region> allValues = database.getAllRegions();
			
			Collections.sort(allValues, new Comparator<Region>() {
		        @Override
		        public int compare(Region r1, Region r2) {
					return r1.getName().compareTo(r2.getName());
		        }
		    });
			
			allValues.add(0, new Region(-1, "", "Filter by region..."));
			ArrayAdapter<Region> adapter = new ArrayAdapter<Region>(this, android.R.layout.simple_spinner_item, allValues);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			region.setPrompt("Filter by region...");
			region.setAdapter(adapter);

			if (currentFilter != null && currentFilter.getRegion() != null) {
				for (int i = 0;i < adapter.getCount();i++) {
					final Region filterRegion = adapter.getItem(i);
					if (filterRegion.getId() == currentFilter.getRegion().getId()) {
						region.setSelection(i);
					}
				}	
			}
		}

		final Spinner console = (Spinner)findViewById(R.id.spinnerConsole);
		if (console != null) {
			console.setEnabled(false);
			ArrayList<Console> allValues = database.getAllConsoles();
			
			Collections.sort(allValues, new Comparator<Console>() {
		        @Override
		        public int compare(Console c1, Console c2) {
					return c1.getName().compareTo(c2.getName());
		        }
		    });
			
			ArrayAdapter<Console> adapter = new ArrayAdapter<Console>(this, android.R.layout.simple_spinner_item, allValues);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			console.setPrompt("Filter by console");
			console.setAdapter(adapter);

			if (currentFilter != null && currentFilter.getConsole() != null) {
				for (int i = 0;i < adapter.getCount();i++) {
					final Console filterConsole = adapter.getItem(i);
					if (filterConsole.getId() == currentFilter.getConsole().getId()) {
						console.setSelection(i);
					}
				}	
			}
		}

		final EditText released = (EditText)findViewById(R.id.editReleased);
		if (released != null) {
			released.setText(currentFilter != null ? currentFilter.getReleased() : Strings.Empty);
		}

		final Spinner developer = (Spinner)findViewById(R.id.spinnerDeveloper);
		if (developer != null) {
			ArrayList<Company> allValues = database.getAllCompanies();
			
			Collections.sort(allValues, new Comparator<Company>() {
		        @Override
		        public int compare(Company c1, Company c2) {
					return c1.getName().compareTo(c2.getName());
		        }
		    });
			
			allValues.add(0, new Company(-1, Strings.Empty, "Filter by developer..."));
			ArrayAdapter<Company> adapter = new ArrayAdapter<Company>(this, android.R.layout.simple_spinner_item, allValues);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			developer.setPrompt("Filter by developer");
			developer.setAdapter(adapter);

			if (currentFilter != null && currentFilter.getDeveloper() != null) {
				for (int i = 0;i < adapter.getCount();i++) {
					final Company filterDeveloper = adapter.getItem(i);
					if (filterDeveloper.getId() == currentFilter.getDeveloper().getId()) {
						developer.setSelection(i);
					}
				}	
			}
		}

		final Spinner publisher = (Spinner)findViewById(R.id.spinnerPublisher);
		if (publisher != null) {
			ArrayList<Company> allValues = database.getAllCompanies();
			
			Collections.sort(allValues, new Comparator<Company>() {
		        @Override
		        public int compare(Company c1, Company c2) {
					return c1.getName().compareTo(c2.getName());
		        }
		    });
			
			allValues.add(0, new Company(-1, Strings.Empty, "Filter by publisher..."));
			ArrayAdapter<Company> adapter = new ArrayAdapter<Company>(this, android.R.layout.simple_spinner_item, allValues);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			publisher.setPrompt("Filter by publisher");
			publisher.setAdapter(adapter);

			if (currentFilter != null && currentFilter.getPublisher() != null) {
				for (int i = 0;i < adapter.getCount();i++) {
					final Company filterPublisher = adapter.getItem(i);
					if (filterPublisher.getId() == currentFilter.getPublisher().getId()) {
						publisher.setSelection(i);
					}
				}	
			}
		}
		
		database.close();

		final Button apply = (Button)findViewById(R.id.game_filter_btnApply);
		if (apply != null) {
			apply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isResetting = false;
					finish();
				}
			});
		}

		final Button reset = (Button)findViewById(R.id.game_filter_btnReset);
		if (reset != null) {
			reset.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isResetting = true;
					finish();
				}
			});
		}
	}

	@Override
	public void finish() {
		// Prepare data intent 
		final Intent data = new Intent();

		// Setup filter based on selected values
		final GameFilter newFilter = new GameFilter();
		newFilter.setConsole(currentFilter.getConsole());
		
		if (!isResetting) {
			final Spinner regions = (Spinner)findViewById(R.id.spinnerRegion);
			if (regions != null) {
				final Region r = (Region)regions.getSelectedItem();
				if (r.getId() != -1) {
					newFilter.setRegion(r);	
				}
			}

			final Spinner consoles = (Spinner)findViewById(R.id.spinnerConsole);
			if (consoles != null) {
				final Console c = (Console)consoles.getSelectedItem();
				if (c.getId() != -1) {
					newFilter.setConsole(c);	
				}
			}

			final EditText released = (EditText)findViewById(R.id.editReleased);
			if (released != null) {
				final String r = released.getText().toString();
				if (!Strings.isNullOrEmpty(r))
					newFilter.setReleased(r);
			}

			final Spinner developer = (Spinner)findViewById(R.id.spinnerDeveloper);
			if (developer != null) {
				final Company d = (Company)developer.getSelectedItem();
				if (d.getId() != -1) {
					newFilter.setDeveloper(d);	
				}
			}

			final Spinner publisher = (Spinner)findViewById(R.id.spinnerPublisher);
			if (publisher != null) {
				final Company p = (Company)publisher.getSelectedItem();
				if (p.getId() != -1) {
					newFilter.setPublisher(p);	
				}
			}	
		}

		data.putExtra("NewGameFilter", newFilter);

		// Activity finished ok, return the data
		setResult(RESULT_OK, data);

		super.finish();
	}
}