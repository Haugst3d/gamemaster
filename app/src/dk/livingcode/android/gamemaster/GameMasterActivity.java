package dk.livingcode.android.gamemaster;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.vessel.VesselSDK;

import dk.livingcode.android.gamemaster.database.GamesDataSource;
import dk.livingcode.android.gamemaster.model.Company;
import dk.livingcode.android.gamemaster.model.Console;
import dk.livingcode.android.gamemaster.model.ConsoleType;
import dk.livingcode.android.gamemaster.model.Game;
import dk.livingcode.android.gamemaster.model.InsertGameResult;
import dk.livingcode.android.gamemaster.model.Region;
import dk.livingcode.android.gamemaster.model.Release;
import dk.livingcode.android.gamemaster.utility.AsyncServiceTask;
import dk.livingcode.android.gamemaster.utility.Strings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameMasterActivity extends SherlockActivity {
	public static final String Tag = GameMasterActivity.class.getSimpleName();
	private static final String ns = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialise Vessel 
	    VesselSDK.initialize(getApplicationContext(), "Nlg0ZGdMcGJ0SXdab2NSTUtTdmhlUW41" );
		
		setContentView(R.layout.main);
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#000099'>GameMaster</font>"));
		setProgressBarIndeterminateVisibility(true);

		// Get or set user information
		SharedPreferences settings = getSharedPreferences("GameMaster_Preferences", Context.MODE_PRIVATE);

		//get the sharepref
		String id = settings.getString("UserIdentifier", Strings.Empty);
		if (Strings.isNullOrEmpty(id)) {
			// Create a guid
			UUID userGuid = java.util.UUID.randomUUID();;
			
			//set the sharedpref
			Editor editor = settings.edit();
			editor.putString("UserIdentifier", userGuid.toString());
			editor.commit();
		}
		
		ImageView wheel = (ImageView)findViewById(R.id.main_database_load_progress);

		AnimationSet animSet = new AnimationSet(true);
		//Rotate around center of Imageview
		RotateAnimation ranim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); //, 200, 200); // canvas.getWidth() / 2, canvas.getHeight() / 2);
		ranim.setRepeatMode(RotateAnimation.RESTART);
		ranim.setRepeatCount(RotateAnimation.INFINITE);
		ranim.setDuration(5000);
		ranim.setInterpolator(new LinearInterpolator());

		animSet.addAnimation(ranim);

		wheel.startAnimation(animSet);

		final Button myCollectionButton = (Button)this.findViewById(R.id.buttonCollection);
		final TextView myCollectionMessage = (TextView)this.findViewById(R.id.main_collection_no_collection_message);
		if (myCollectionMessage != null && myCollectionButton != null) {
			if (GamesDataSource.UserCollection.size() > 0) {
				myCollectionMessage.setVisibility(TextView.GONE);
				myCollectionButton.setVisibility(Button.VISIBLE);
			}	
		}

		if (myCollectionButton != null) {
			myCollectionButton.setEnabled(false);
			myCollectionButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					//Intent i = new Intent(GameMasterActivity.this, GameFilterActivity.class);
					//startActivity(i);
				}
			});	
		}

		final Button gamesButton = (Button)this.findViewById(R.id.buttonGamesList);
		if (gamesButton != null) {
			gamesButton.setEnabled(false);
			gamesButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(GameMasterActivity.this, GameListActivity.class);
					startActivity(i);
				}
			});
		}

		final Button hardwareButton = (Button)this.findViewById(R.id.buttonHardwareList);
		if (hardwareButton != null) {
			hardwareButton.setEnabled(false);
			hardwareButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(GameMasterActivity.this, ConsoleListActivity.class);
					startActivity(i);
				}
			});
		}

		finishedLoading(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		switch (itemId) {
		case R.id.main_menu_reload_all:
			// Perform async loading and parsing of games from xml file
			new LoadGamesRunner().execute();
			break;
		case R.id.main_menu_reset_collection:
			// Remove all games from the collection table
			new ResetCollectionRunner().execute();
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private class ResetCollectionRunner extends AsyncServiceTask<Void, Void, Void> {
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Void doInBackground(Void... args) {
			GamesDataSource database = new GamesDataSource(GameMasterActivity.this);
			try {
				database.openWrite();
				database.resetCollection();
			} catch (Exception e) {
			} finally {
				database.close();
			}
			
			return null;	
		}
		
		@Override
		protected void onPostExecute(Void result) {
			setSupportProgressBarIndeterminateVisibility(false);
		}
	}

	private class LoadGamesRunner extends AsyncServiceTask<Void, Void, Boolean> {
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Boolean doInBackground(Void... args) {
			ArrayList<Game> games = null;
			ArrayList<Region> regions = null;
			ArrayList<Company> companies = null;
			ArrayList<Console> consoles = null;

			InputStream regionsStream = null;
			InputStream companiesStream = null;
			InputStream consolesStream = null;
			InputStream gamesStream = null;

			// Insert all games into the database
			GamesDataSource database = new GamesDataSource(GameMasterActivity.this);

			try {
				database.openWrite();

				database.begin();
				regionsStream = getResources().openRawResource(R.raw.region_library);
				regions = parseRegions(regionsStream, database);
				for (Region r : regions) {
					database.addRegion(r);
				}

				companiesStream = getResources().openRawResource(R.raw.company_library);
				companies = parseCompanies(companiesStream, database);
				for (Company c : companies) {
					database.addCompany(c);
				}

				consolesStream = getResources().openRawResource(R.raw.console_library);
				consoles = parseConsoles(consolesStream, database);
				for (Console c : consoles) {
					database.addConsole(c);
				}

				gamesStream = getResources().openRawResource(R.raw.game_library);
				games = parseGames(gamesStream, database);
				final ArrayList<InsertGameResult> dbInsertResult = new ArrayList<InsertGameResult>();
				for (Game g : games) {
					try {
						database.addGame(g);	
					} catch (Exception e) {
						InsertGameResult error = new InsertGameResult();
						error.setGameId(g.getId());
						error.setMessage(e.getMessage());

						dbInsertResult.add(error);
					}
				}

				database.success();
				database.end();
			} catch (Exception e) {
				Log.e("LoadGamesRunner", "Error parsing games xml.", e);

				return false;
			} finally {
				try {
					regionsStream.close();
					companiesStream.close();
					consolesStream.close();
					gamesStream.close();
					database.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return true;
		}

		public ArrayList<Console> parseConsoles(InputStream in, final GamesDataSource database) throws XmlPullParserException, IOException {
			ArrayList<Console> consoles = new ArrayList<Console>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();

			parser.require(XmlPullParser.START_TAG, ns, "consoles");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}

				String name = parser.getName();
				if (name.equals("console")) {
					parser.require(XmlPullParser.START_TAG, ns, "console");
					int id = -1;
					String idValue = parser.getAttributeValue(null, "id");
					String codeValue = parser.getAttributeValue(null, "code");
					if (!Strings.isNullOrEmpty(idValue)) {
						id = Integer.parseInt(idValue);
					}

					String nameValue = Strings.Empty;
					ConsoleType typeValue = null;

					while (parser.next() != XmlPullParser.END_TAG) {
						if (parser.getEventType() != XmlPullParser.START_TAG) {
							continue;
						}

						String childName = parser.getName();
						if (childName.equals("name")) {
							nameValue = readText(parser);
						} else if (childName.equals("hardware")) {
							parser.require(XmlPullParser.START_TAG, ns, "hardware");
							while (parser.next() != XmlPullParser.END_TAG) {
								if (parser.getEventType() != XmlPullParser.START_TAG) {
									continue;
								}

								String hardwareName = parser.getName();
								if (hardwareName.equals("type")) {
									String tVal = readText(parser);
									if (tVal.equals("EIGHT_BIT")) {
										typeValue = ConsoleType.EightBit;
									} else if (tVal.equals("SIXTEEN_BIT")) {
										typeValue = ConsoleType.SixteenBit;
									} else if (tVal.equals("THIRTYTWO_BIT")) {
										typeValue = ConsoleType.ThirtyTwoBit;
									}
								}
							}
						} else {
							skip(parser);
						}
					}

					parser.require(XmlPullParser.END_TAG, ns, "console");

					Console r = new Console(id, codeValue, nameValue, typeValue);
					consoles.add(r);
				} else {
					skip(parser);
				}
			}

			return consoles;
		}

		public ArrayList<Company> parseCompanies(InputStream in, final GamesDataSource database) throws XmlPullParserException, IOException {
			ArrayList<Company> companies = new ArrayList<Company>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();

			parser.require(XmlPullParser.START_TAG, ns, "companies");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}

				String name = parser.getName();
				if (name.equals("company")) {
					parser.require(XmlPullParser.START_TAG, ns, "company");
					int id = -1;
					String idValue = parser.getAttributeValue(null, "id");
					String codeValue = parser.getAttributeValue(null, "code");
					String nameValue = readText(parser);
					if (!Strings.isNullOrEmpty(idValue)) {
						id = Integer.parseInt(idValue);
					}

					parser.require(XmlPullParser.END_TAG, ns, "company");

					Company r = new Company(id, codeValue, nameValue);
					companies.add(r);
				} else {
					skip(parser);
				}
			}

			return companies;
		}

		public ArrayList<Region> parseRegions(InputStream in, final GamesDataSource database) throws XmlPullParserException, IOException {
			ArrayList<Region> regions = new ArrayList<Region>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();

			parser.require(XmlPullParser.START_TAG, ns, "regions");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}

				String name = parser.getName();
				if (name.equals("region")) {
					parser.require(XmlPullParser.START_TAG, ns, "region");
					int id = -1;
					String idValue = parser.getAttributeValue(null, "id");
					String codeValue = parser.getAttributeValue(null, "code");
					String nameValue = readText(parser);
					if (!Strings.isNullOrEmpty(idValue)) {
						id = Integer.parseInt(idValue);
					}

					parser.require(XmlPullParser.END_TAG, ns, "region");

					Region r = new Region(id, codeValue, nameValue);
					regions.add(r);
				} else {
					skip(parser);
				}
			}

			return regions;
		}

		private ArrayList<Game> parseGames(InputStream in, final GamesDataSource database) throws Exception {
			ArrayList<Game> games = new ArrayList<Game>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();

			parser.require(XmlPullParser.START_TAG, ns, "games");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}

				String name = parser.getName();
				if (name.equals("game")) {
					parser.require(XmlPullParser.START_TAG, ns, "game");
					int id = -1;
					String idValue = parser.getAttributeValue(null, "id");
					if (!Strings.isNullOrEmpty(idValue)) {
						id = Integer.parseInt(idValue);
					}

					ArrayList<Release> releases = new ArrayList<Release>();
					String defaultRelease = Strings.Empty;
					String developerCode = null;
					Company developer = null;
					String consoleCode = null;
					Console console = null;
					String genre = Strings.Empty;
					String subgenre = Strings.Empty;
					String shortSummary = Strings.Empty;
					String summary = Strings.Empty;
					String description = Strings.Empty;

					while (parser.next() != XmlPullParser.END_TAG) {
						if (parser.getEventType() != XmlPullParser.START_TAG) {
							continue;
						}

						String gameChild = parser.getName();
						if (gameChild.equals("releases")) {
							parser.require(XmlPullParser.START_TAG, ns, "releases");

							defaultRelease = parser.getAttributeValue(null, "default");

							while (parser.next() != XmlPullParser.END_TAG) {
								final int eventType = parser.getEventType();
								if (eventType != XmlPullParser.START_TAG) {
									continue;
								}

								String releaseChild = parser.getName();
								if (releaseChild.equals("release")) {
									releases.add(readRelease(parser, id, database));
								} else {
									skip(parser);
								}
							}
						} else if (gameChild.equals("genre")) {
							parser.require(XmlPullParser.START_TAG, ns, "genre");
							genre = readText(parser);
							parser.require(XmlPullParser.END_TAG, ns, "genre");
						} else if (gameChild.equals("subgenre")) {
							parser.require(XmlPullParser.START_TAG, ns, "subgenre");
							subgenre = readText(parser);
							parser.require(XmlPullParser.END_TAG, ns, "subgenre");
						} else if (gameChild.equals("shortSummary")) {
							parser.require(XmlPullParser.START_TAG, ns, "shortSummary");
							shortSummary = readText(parser);
							parser.require(XmlPullParser.END_TAG, ns, "shortSummary");
						} else if (gameChild.equals("summary")) {
							parser.require(XmlPullParser.START_TAG, ns, "summary");
							summary = readText(parser);
							parser.require(XmlPullParser.END_TAG, ns, "summary");
						} else if (gameChild.equals("description")) {
							parser.require(XmlPullParser.START_TAG, ns, "description");
							description = readText(parser);
							parser.require(XmlPullParser.END_TAG, ns, "description");
						} else if (gameChild.equals("developer")) {
							developerCode = readDeveloper(parser);
							if (!Strings.isNullOrEmpty(developerCode)) {
								// Look up developer based on code	
								developer = database.getCompanyByCode(developerCode);
							}
						} else if (gameChild.equals("console")) {
							consoleCode = readConsole(parser);
							if (!Strings.isNullOrEmpty(consoleCode)) {
								// Look up console based on code
								console = database.getConsoleByCode(consoleCode);
							}
						} else {
							skip(parser);
						}
					}

					Game g = new Game();
					g.setId(id);
					g.setReleases(releases);
					g.setDefaultRelease(defaultRelease);
					g.setDeveloper(developer);
					g.setConsole(console);
					g.setGenre(genre);
					g.setSubGenre(subgenre);
					g.setSummary(summary);
					g.setShortSummary(shortSummary);
					g.setDescription(description);

					games.add(g);
				} else {
					skip(parser);
				}
			}

			return games;
		}

		private Release readRelease(XmlPullParser parser, final int gameId, final GamesDataSource database) throws Exception, XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, ns, "release");

			String regionCode = parser.getAttributeValue(null, "regionCode");
			Region r = null;
			if (!Strings.isNullOrEmpty(regionCode)) {
				r = database.getRegionByCode(regionCode);	
			}

			String publisherCode = parser.getAttributeValue(null, "publisher");
			Company publisher = null;
			if (!Strings.isNullOrEmpty(publisherCode)) {
				publisher = database.getCompanyByCode(publisherCode);
			}

			String released = parser.getAttributeValue(null, "released");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
			final TimeZone utcZone = TimeZone.getTimeZone("UTC");
			sdf.setTimeZone(utcZone);

			Date d = sdf.parse(released);

			String title = readText(parser);

			parser.require(XmlPullParser.END_TAG, ns, "release");

			return new Release(0, 0, r, publisher, title, d);
		}

		private String readDeveloper(XmlPullParser parser) throws IOException, XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, ns, "developer");
			String developer = readText(parser);
			parser.require(XmlPullParser.END_TAG, ns, "developer");

			return developer;
		}

		private String readConsole(XmlPullParser parser) throws IOException, XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, ns, "console");
			String console = readText(parser);
			parser.require(XmlPullParser.END_TAG, ns, "console");

			return console;
		}

		private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
			String result = "";
			if (parser.next() == XmlPullParser.TEXT) {
				result = parser.getText();
				parser.nextTag();
			}

			return result;
		}

		private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				throw new IllegalStateException();
			}

			int depth = 1;
			while (depth != 0) {
				switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
				}
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setSupportProgressBarIndeterminateVisibility(false);

			finishedLoading(result);
		}
	}

	private void finishedLoading(Boolean result) {
		if (!result) {
			Toast.makeText(GameMasterActivity.this, "Error inserting games into database", Toast.LENGTH_SHORT).show();

			return;
		}

		final Button myCollectionButton = (Button)findViewById(R.id.buttonCollection);
		if (myCollectionButton != null) {
			myCollectionButton.setEnabled(true);
		}

		final Button gamesButton = (Button)findViewById(R.id.buttonGamesList);
		if (gamesButton != null) {
			gamesButton.setEnabled(true);
		}

		final Button hardwareButton = (Button)findViewById(R.id.buttonHardwareList);
		if (hardwareButton != null) {
			hardwareButton.setEnabled(true);
		}
	}
}