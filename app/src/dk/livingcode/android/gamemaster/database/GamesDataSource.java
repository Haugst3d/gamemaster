package dk.livingcode.android.gamemaster.database;

import java.util.ArrayList;
import java.util.Date;

import dk.livingcode.android.gamemaster.model.CollectedGame;
import dk.livingcode.android.gamemaster.model.Company;
import dk.livingcode.android.gamemaster.model.Console;
import dk.livingcode.android.gamemaster.model.ConsoleType;
import dk.livingcode.android.gamemaster.model.Game;
import dk.livingcode.android.gamemaster.model.Region;
import dk.livingcode.android.gamemaster.model.Release;
import dk.livingcode.android.gamemaster.utility.Strings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class GamesDataSource {
	private SQLiteDatabase database;
	private static DatabaseHelper dbHelper;
	public static final ArrayList<Game> UserCollection = new ArrayList<Game>();

	private String[] allRegionColumns = { 
			DatabaseHelper.COLUMN_REGIONS_ID, 
			DatabaseHelper.COLUMN_REGIONS_EXTERNAL_ID, 
			DatabaseHelper.COLUMN_REGIONS_CODE, 
			DatabaseHelper.COLUMN_REGIONS_NAME 
	};

	private String[] allCompanyColumns = { 
			DatabaseHelper.COLUMN_COMPANIES_ID, 
			DatabaseHelper.COLUMN_COMPANIES_EXTERNAL_ID, 
			DatabaseHelper.COLUMN_COMPANIES_CODE, 
			DatabaseHelper.COLUMN_COMPANIES_NAME 
	};

	private String[] allConsoleColumns = { 
			DatabaseHelper.COLUMN_CONSOLES_ID, 
			DatabaseHelper.COLUMN_CONSOLES_EXTERNAL_ID, 
			DatabaseHelper.COLUMN_CONSOLES_CODE, 
			DatabaseHelper.COLUMN_CONSOLES_NAME, 
			DatabaseHelper.COLUMN_CONSOLES_TYPE 
	};

	private String[] allReleaseColumns = { 
			DatabaseHelper.COLUMN_RELEASES_ID, 
			DatabaseHelper.COLUMN_RELEASES_GAME_ID, 
			DatabaseHelper.COLUMN_RELEASES_REGION_ID, 
			DatabaseHelper.COLUMN_RELEASES_TITLE, 
			DatabaseHelper.COLUMN_RELEASES_RELEASED,
			DatabaseHelper.COLUMN_RELEASES_PUBLISHER_ID
	};

	private String[] allGameColumns = { 
			DatabaseHelper.COLUMN_GAMES_ID, 
			DatabaseHelper.COLUMN_GAMES_EXTERNAL_ID,
			DatabaseHelper.COLUMN_GAMES_CONSOLE_ID,
			DatabaseHelper.COLUMN_GAMES_DEVELOPER_ID,
			DatabaseHelper.COLUMN_GAMES_GENRE,
			DatabaseHelper.COLUMN_GAMES_SUB_GENRE,
			DatabaseHelper.COLUMN_GAMES_SHORT_SUMMARY,
			DatabaseHelper.COLUMN_GAMES_SUMMARY,
			DatabaseHelper.COLUMN_GAMES_DESCRIPTION,
			DatabaseHelper.COLUMN_GAMES_RARITY,
			DatabaseHelper.COLUMN_GAMES_DEFAULT_RELEASE
	};

	private String[] allCollectionColumns = { 
			DatabaseHelper.COLUMN_COLLECTION_ID, 
			DatabaseHelper.COLUMN_COLLECTION_USER_ID,
			DatabaseHelper.COLUMN_COLLECTION_GAME_ID,
			DatabaseHelper.COLUMN_COLLECTION_CONSOLE_ID,
			DatabaseHelper.COLUMN_COLLECTION_REGION_ID,
			DatabaseHelper.COLUMN_COLLECTION_REGION_EXTRA,
			DatabaseHelper.COLUMN_COLLECTION_HAS_CASSETTE,
			DatabaseHelper.COLUMN_COLLECTION_CASSETTE_STATE,
			DatabaseHelper.COLUMN_COLLECTION_HAS_INSTRUCTIONS,
			DatabaseHelper.COLUMN_COLLECTION_INSTRUCTIONS_STATE,
			DatabaseHelper.COLUMN_COLLECTION_HAS_BOX,
			DatabaseHelper.COLUMN_COLLECTION_BOX_STATE,
			DatabaseHelper.COLUMN_COLLECTION_IS_SEALED,
			DatabaseHelper.COLUMN_COLLECTION_SCORE,
			DatabaseHelper.COLUMN_COLLECTION_NOTES,
			DatabaseHelper.COLUMN_COLLECTION_DATE_ADDED,
			DatabaseHelper.COLUMN_COLLECTION_DATE_UPDATED,
			DatabaseHelper.COLUMN_COLLECTION_DATE_SYNCHRONIZED
	};

	public GamesDataSource(Context context) {
		if (dbHelper == null) {
			dbHelper = new DatabaseHelper(context);	
		}
	}

	public void openWrite() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void open() throws SQLException {
		database = dbHelper.getReadableDatabase();
	}

	public void begin() {
		database.beginTransaction();
	}

	public void success() {
		database.setTransactionSuccessful();
	}

	public void end() {
		database.endTransaction();
	}

	public void close() {
		dbHelper.close();
	}

	public void resetCollection() throws Exception {
		// Remove the entire table
		database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_COLLECTION);
		// Re-create the collection table
		database.execSQL(DatabaseHelper.DATABASE_CREATE_COLLECTION);
	}
	
	public void addGameToCollection(final CollectedGame cg) throws Exception {
		// Look for an existing game with the current id
		CollectedGame existing = getCollectedGame(cg.getId());
		if (existing == null) {
			ContentValues values = new ContentValues();

			try {
				values.put(DatabaseHelper.COLUMN_COLLECTION_USER_ID, cg.getUserId());
				values.put(DatabaseHelper.COLUMN_COLLECTION_GAME_ID, cg.getGame().getId());
				values.put(DatabaseHelper.COLUMN_COLLECTION_CONSOLE_ID, cg.getConsole().getId());
				values.put(DatabaseHelper.COLUMN_COLLECTION_REGION_ID, cg.getRegion().getId());
				values.put(DatabaseHelper.COLUMN_COLLECTION_REGION_EXTRA, cg.getRegionExtra());
				values.put(DatabaseHelper.COLUMN_COLLECTION_HAS_CASSETTE, cg.getHasCassette() ? 1 : 0);
				values.put(DatabaseHelper.COLUMN_COLLECTION_CASSETTE_STATE, cg.getCassetteState());
				values.put(DatabaseHelper.COLUMN_COLLECTION_HAS_INSTRUCTIONS, cg.getHasInstructions() ? 1 : 0);
				values.put(DatabaseHelper.COLUMN_COLLECTION_INSTRUCTIONS_STATE, cg.getInstructionsState());
				values.put(DatabaseHelper.COLUMN_COLLECTION_HAS_BOX, cg.getHasBox() ? 1 : 0);
				values.put(DatabaseHelper.COLUMN_COLLECTION_BOX_STATE, cg.getBoxState());
				values.put(DatabaseHelper.COLUMN_COLLECTION_IS_SEALED, cg.getIsSealed() ? 1 : 0);
				values.put(DatabaseHelper.COLUMN_COLLECTION_SCORE, cg.getScore());
				values.put(DatabaseHelper.COLUMN_COLLECTION_NOTES, cg.getNotes());
				values.put(DatabaseHelper.COLUMN_COLLECTION_DATE_ADDED, new Date().getTime());
				values.put(DatabaseHelper.COLUMN_COLLECTION_DATE_SYNCHRONIZED, cg.getDateSynchronized().getTime());
				
				// Insert a new game into the database
				database.insert(DatabaseHelper.TABLE_COLLECTION, null, values);
			} catch (Exception e) {
				throw new Exception("An error occurred while adding game to collection");
			}
		} else {
			ContentValues values = new ContentValues();

			try {
				values.put(DatabaseHelper.COLUMN_COLLECTION_USER_ID, cg.getUserId());
				values.put(DatabaseHelper.COLUMN_COLLECTION_GAME_ID, cg.getGame().getId());
				values.put(DatabaseHelper.COLUMN_COLLECTION_CONSOLE_ID, cg.getConsole().getId());
				values.put(DatabaseHelper.COLUMN_COLLECTION_REGION_ID, cg.getRegion().getId());
				values.put(DatabaseHelper.COLUMN_COLLECTION_REGION_EXTRA, cg.getRegionExtra());
				values.put(DatabaseHelper.COLUMN_COLLECTION_HAS_CASSETTE, cg.getHasCassette() ? 1 : 0);
				values.put(DatabaseHelper.COLUMN_COLLECTION_CASSETTE_STATE, cg.getCassetteState());
				values.put(DatabaseHelper.COLUMN_COLLECTION_HAS_INSTRUCTIONS, cg.getHasInstructions() ? 1 : 0);
				values.put(DatabaseHelper.COLUMN_COLLECTION_INSTRUCTIONS_STATE, cg.getInstructionsState());
				values.put(DatabaseHelper.COLUMN_COLLECTION_HAS_BOX, cg.getHasBox() ? 1 : 0);
				values.put(DatabaseHelper.COLUMN_COLLECTION_BOX_STATE, cg.getBoxState());
				values.put(DatabaseHelper.COLUMN_COLLECTION_IS_SEALED, cg.getIsSealed() ? 1 : 0);
				values.put(DatabaseHelper.COLUMN_COLLECTION_SCORE, cg.getScore());
				values.put(DatabaseHelper.COLUMN_COLLECTION_NOTES, cg.getNotes());
				values.put(DatabaseHelper.COLUMN_COLLECTION_DATE_UPDATED, new Date().getTime());
				values.put(DatabaseHelper.COLUMN_COLLECTION_DATE_SYNCHRONIZED, cg.getDateSynchronized().getTime());
				
				// Update the existing game
				String where = DatabaseHelper.COLUMN_COLLECTION_ID + "=?";
				String[] whereArgs = new String[] { String.valueOf(cg.getId()) };

				database.update(DatabaseHelper.TABLE_COLLECTION, values, where, whereArgs);
			} catch (Exception e) {
				throw new Exception("An error occurred while updating game in collection");
			}
		}
	}

	public void deleteGameFromCollection(final int collectedGameId) throws Exception {
		database.delete(DatabaseHelper.TABLE_COLLECTION, DatabaseHelper.COLUMN_COLLECTION_ID + "=?", new String[] { String.valueOf(collectedGameId) });
	}

	public ArrayList<Integer> getCollectedGameIds() throws Exception {
		ArrayList<Integer> games = new ArrayList<Integer>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_COLLECTION, new String[]{ DatabaseHelper.COLUMN_COLLECTION_GAME_ID }, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(0);
			games.add(id);
			
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();

		return games;
	}
	
	public ArrayList<CollectedGame> getCollectedGames() throws Exception {
		ArrayList<CollectedGame> games = new ArrayList<CollectedGame>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_COLLECTION, allCollectionColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CollectedGame game = cursorToCollectedGame(cursor);
			games.add(game);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();

		return games;
	}

	/**
	 * Gets all collected games of a certain title
	 * @param gameId
	 * @return
	 * @throws Exception
	 */
	public ArrayList<CollectedGame> getCollectedGames(final int gameId) throws Exception {
		ArrayList<CollectedGame> games = new ArrayList<CollectedGame>();
		
		Cursor cursor = database.rawQuery("select * from " + DatabaseHelper.TABLE_COLLECTION + " where " + DatabaseHelper.COLUMN_COLLECTION_GAME_ID + "=?", new String [] { String.valueOf(gameId) });
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CollectedGame cg = cursorToCollectedGame(cursor); 
			games.add(cg);
			cursor.moveToNext();
		}

		return games;
	}
	
	public CollectedGame getCollectedGame(final int collectedGameId) throws Exception {
		Cursor cursor = database.rawQuery("select * from " + DatabaseHelper.TABLE_COLLECTION + " where " + DatabaseHelper.COLUMN_COLLECTION_ID + "=?", new String [] { String.valueOf(collectedGameId) });
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			CollectedGame cg = cursorToCollectedGame(cursor); 
			cursor.close();

			return cg;
		}

		return null;
	}

	public final int getCollectedGamesCount() {
		Cursor mCount = database.rawQuery("select count(*) from " + DatabaseHelper.TABLE_COLLECTION, null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();

		return count;
	}

	private CollectedGame cursorToCollectedGame(Cursor cursor) {
		CollectedGame cg = new CollectedGame();

		int id = cursor.getInt(0);
		cg.setId(id);

		String userId = cursor.getString(1);
		cg.setUserId(userId);

		int gameId = cursor.getInt(2);
		final Game g = getGame(gameId);
		cg.setGame(g);

		int consoleId = cursor.getInt(3);
		final Console c = getConsole(consoleId);
		cg.setConsole(c);

		int regionId = cursor.getInt(4);
		final Region r = getRegion(regionId);
		cg.setRegion(r);

		String regionExtra = cursor.getString(5);
		cg.setRegionExtra(regionExtra);

		int hasCasetteVal = cursor.getInt(6);
		boolean hasCassette = hasCasetteVal == 0 ? false : true;
		cg.setHasCassette(hasCassette);

		int casetteState = cursor.getInt(7);
		cg.setCassetteState(casetteState);

		int hasInstructionsVal = cursor.getInt(8);
		boolean hasInstructions = hasInstructionsVal == 0 ? false : true;
		cg.setHasInstructions(hasInstructions);

		int instructionsState = cursor.getInt(9);
		cg.setInstructionsState(instructionsState);

		int hasBoxVal = cursor.getInt(10);
		boolean hasBox = hasBoxVal == 0 ? false : true;
		cg.setHasBox(hasBox);

		int boxState = cursor.getInt(11);
		cg.setBoxState(boxState);

		int isSealedVal = cursor.getInt(12);
		boolean isSealed = isSealedVal == 0 ? false : true;
		cg.setIsSealed(isSealed);

		int score = cursor.getInt(13);
		cg.setScore(score);

		String notes = cursor.getString(14);
		cg.setNotes(notes);

		int dateAddedTicks = cursor.getInt(15);
		Date dateAdded = new Date(dateAddedTicks);
		cg.setDateAdded(dateAdded);

		int dateUpdatedTicks = cursor.getInt(16);
		Date dateUpdated = new Date(dateUpdatedTicks);
		cg.setDateUpdated(dateUpdated);

		int dateSyncedTicks = cursor.getInt(17);
		Date dateSynced = new Date(dateSyncedTicks);
		cg.setDateSynchronized(dateSynced);

		return cg;
	}

	public void addGame(final Game g) throws Exception {
		// Look for an existing game with the current id
		Game existing = getGame(g.getId());
		if (existing == null) {
			ContentValues values = new ContentValues();

			try {
				values.put(DatabaseHelper.COLUMN_GAMES_EXTERNAL_ID, g.getId());
				values.put(DatabaseHelper.COLUMN_GAMES_CONSOLE_ID, g.getConsole().getId());

				Company dev = g.getDeveloper();
				if (dev != null) {
					values.put(DatabaseHelper.COLUMN_GAMES_DEVELOPER_ID, dev.getId());	
				}

				values.put(DatabaseHelper.COLUMN_GAMES_GENRE, g.getGenre() != null ? String.valueOf(g.getGenre()) : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_SUB_GENRE, g.getSubGenre() != null ? String.valueOf(g.getSubGenre()) : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_SHORT_SUMMARY, !Strings.isNullOrEmpty(g.getShortSummary()) ? g.getShortSummary() : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_SUMMARY, !Strings.isNullOrEmpty(g.getSummary()) ? g.getSummary() : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_DESCRIPTION, !Strings.isNullOrEmpty(g.getDescription()) ? g.getDescription() : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_RARITY, g.getRarity() != null ? String.valueOf(g.getRarity()) : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_DEFAULT_RELEASE, g.getDefaultReleaseCode());

				// Insert a new game into the database
				database.insert(DatabaseHelper.TABLE_GAMES, null, values);

				for (Release r : g.getReleases()) {
					addRelease(g.getId(), r);
				}
			} catch (Exception e) {
				throw new Exception("An error occurred while adding game");
			}
		} else {
			ContentValues values = new ContentValues();

			try {
				values.put(DatabaseHelper.COLUMN_GAMES_CONSOLE_ID, g.getConsole().getId());

				Company dev = g.getDeveloper();
				if (dev != null) {
					values.put(DatabaseHelper.COLUMN_GAMES_DEVELOPER_ID, dev.getId());	
				}

				values.put(DatabaseHelper.COLUMN_GAMES_GENRE, g.getGenre() != null ? String.valueOf(g.getGenre()) : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_SUB_GENRE, g.getSubGenre() != null ? String.valueOf(g.getSubGenre()) : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_SHORT_SUMMARY, !Strings.isNullOrEmpty(g.getShortSummary()) ? g.getShortSummary() : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_SUMMARY, !Strings.isNullOrEmpty(g.getSummary()) ? g.getSummary() : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_DESCRIPTION, !Strings.isNullOrEmpty(g.getDescription()) ? g.getDescription() : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_RARITY, g.getRarity() != null ? String.valueOf(g.getRarity()) : Strings.Empty);
				values.put(DatabaseHelper.COLUMN_GAMES_DEFAULT_RELEASE, g.getDefaultReleaseCode());

				// Get existing releases and update them
				//	for (Release r : g.getReleases()) {
				//		addRelease(g.getId(), r);
				//	}

				// Update the existing game
				String where = DatabaseHelper.COLUMN_GAMES_EXTERNAL_ID + "=?";
				String[] whereArgs = new String[] { String.valueOf(g.getId()) };

				database.update(DatabaseHelper.TABLE_GAMES, values, where, whereArgs);
			} catch (Exception e) {
				throw new Exception("An error occurred while updating game");
			}
		}
	}

	public final int getGameCount() {
		Cursor mCount = database.rawQuery("select count(*) from " + DatabaseHelper.TABLE_GAMES, null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();

		return count;
	}

	public final Game getGame(final int id) {
		Cursor cur = database.rawQuery("select * from " + DatabaseHelper.TABLE_GAMES + " where " + DatabaseHelper.COLUMN_GAMES_EXTERNAL_ID + "=?", new String [] { String.valueOf(id) });
		if (cur != null && cur.getCount() > 0) {
			cur.moveToFirst();
			Game g = cursorToGame(cur); 
			cur.close();

			return g;
		}

		return null;
	}

	public ArrayList<Game> getAllGames() {
		ArrayList<Game> games = new ArrayList<Game>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_GAMES, allGameColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Game game = cursorToGame(cursor);
			games.add(game);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();

		return games;
	}

	private Game cursorToGame(Cursor cursor) {
		Game game = new Game();
		cursor.getLong(0);
		game.setId(cursor.getInt(1));
		int consoleId = cursor.getInt(2);
		final Console c = getConsole(consoleId);
		game.setConsole(c);
		int developerId = cursor.getInt(3);
		final Company developer = getCompany(developerId);
		game.setDeveloper(developer);		
		String genre = cursor.getString(4);
		game.setGenre(genre);
		String subGenre = cursor.getString(5);
		game.setSubGenre(subGenre);
		String shortSum = cursor.getString(6);
		game.setShortSummary(shortSum);
		String summary = cursor.getString(7);
		game.setSummary(summary);
		String description = cursor.getString(8);
		game.setDescription(description);
		String rarity = cursor.getString(9);
		game.setRarity(rarity);
		String defaultRelease = cursor.getString(10);

		// Get releases from releases table 
		ArrayList<Release> releases = getReleases(game.getId());
		game.setReleases(releases);

		game.setDefaultRelease(defaultRelease);

		return game;
	}

	private void addRelease(final int gameId, final Release r) {
		Release existing = getRelease(r.getId());
		if (existing == null) {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_RELEASES_GAME_ID, gameId);
			values.put(DatabaseHelper.COLUMN_RELEASES_REGION_ID, r.getRegion().getId());
			values.put(DatabaseHelper.COLUMN_RELEASES_TITLE, r.getTitle());
			values.put(DatabaseHelper.COLUMN_RELEASES_RELEASED, r.getReleased().getTime());

			Company pub = r.getPublisher();
			if (pub != null) {
				values.put(DatabaseHelper.COLUMN_RELEASES_PUBLISHER_ID, pub.getId());	
			}

			// Insert a new game into the database
			database.insert(DatabaseHelper.TABLE_RELEASES, null, values);
		} else {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_RELEASES_GAME_ID, gameId);
			values.put(DatabaseHelper.COLUMN_RELEASES_REGION_ID, r.getRegion().getId());
			values.put(DatabaseHelper.COLUMN_RELEASES_TITLE, r.getTitle());
			values.put(DatabaseHelper.COLUMN_RELEASES_RELEASED, r.getReleased().getTime());

			Company pub = r.getPublisher();
			if (pub != null) {
				values.put(DatabaseHelper.COLUMN_RELEASES_PUBLISHER_ID, pub.getId());	
			}

			// Update the existing game
			String where = DatabaseHelper.COLUMN_RELEASES_ID + "=?";
			String[] whereArgs = new String[] { String.valueOf(existing.getId()) };

			database.update(DatabaseHelper.TABLE_RELEASES, values, where, whereArgs);
		}
	}

	public final int getReleaseCount() {
		Cursor mCount = database.rawQuery("select count(*) from " + DatabaseHelper.TABLE_RELEASES, null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();

		return count;
	}

	public final Release getRelease(final int id) {
		Cursor cur = database.rawQuery("select * from " + DatabaseHelper.TABLE_RELEASES + " where " + DatabaseHelper.COLUMN_RELEASES_ID + "=?", new String [] { String.valueOf(id) });
		if (cur != null && cur.getCount() > 0) {
			cur.moveToFirst();
			Release r = cursorToRelease(cur); 
			cur.close();

			return r;
		}

		return null;
	}

	public final ArrayList<Release> getReleases(final int gameId) {
		ArrayList<Release> releases = new ArrayList<Release>();

		Cursor cursor = database.rawQuery("select * from " + DatabaseHelper.TABLE_RELEASES + " where " + DatabaseHelper.COLUMN_RELEASES_GAME_ID + "=?", new String [] { String.valueOf(gameId) });

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Release r = cursorToRelease(cursor);
			releases.add(r);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();

		return releases;
	}

	public ArrayList<Release> getAllReleases() {
		ArrayList<Release> releases = new ArrayList<Release>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_RELEASES, allReleaseColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Release r = cursorToRelease(cursor);
			releases.add(r);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();

		return releases;
	}

	private Release cursorToRelease(Cursor cursor) {
		int id = cursor.getInt(0);
		int gameId = cursor.getInt(1);
		int regionId = cursor.getInt(2);
		Region reg = getRegion(regionId);
		String title = cursor.getString(3);
		long releasedVal = Long.parseLong(cursor.getString(4));
		Date released = new Date(releasedVal);
		int publisherId = cursor.getInt(5);
		Company p = getCompany(publisherId);

		Release r = new Release(id, gameId, reg, p, title, released);

		return r;
	}

	public void addRegion(final Region r) {
		Region existing = getRegion(r.getId());
		if (existing == null) {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_REGIONS_EXTERNAL_ID, r.getId());
			values.put(DatabaseHelper.COLUMN_REGIONS_CODE, r.getCode());
			values.put(DatabaseHelper.COLUMN_REGIONS_NAME, r.getName());

			database.insert(DatabaseHelper.TABLE_REGIONS, null, values);	
		} else {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_REGIONS_CODE, r.getCode());
			values.put(DatabaseHelper.COLUMN_REGIONS_NAME, r.getName());

			String where = DatabaseHelper.COLUMN_REGIONS_EXTERNAL_ID + "=?";
			String[] whereArgs = new String[] {String.valueOf(existing.getId())};

			database.update(DatabaseHelper.TABLE_REGIONS, values, where, whereArgs);
		}
	}

	public final int getRegionCount() {
		Cursor mCount = database.rawQuery("select count(*) from " + DatabaseHelper.TABLE_REGIONS, null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();

		return count;
	}

	public final Region getRegion(final int id) {
		Cursor cur = database.rawQuery("select * from " + DatabaseHelper.TABLE_REGIONS + " where " + DatabaseHelper.COLUMN_REGIONS_EXTERNAL_ID + "=?", new String [] { String.valueOf(id) });
		if (cur != null && cur.getCount() > 0) {
			cur.moveToFirst();
			Region r = cursorToRegion(cur);
			cur.close();

			return r;
		}

		return null;
	}

	public final Region getRegionByCode(final String code) {
		Cursor cur = database.rawQuery("select * from " + DatabaseHelper.TABLE_REGIONS + " where " + DatabaseHelper.COLUMN_REGIONS_CODE + "=?", new String [] { code });
		if (cur != null && cur.getCount() > 0) {
			cur.moveToFirst();
			Region r = cursorToRegion(cur);
			cur.close();

			return r;
		}

		return null;
	}

	public ArrayList<Region> getAllRegions() {
		ArrayList<Region> regions = new ArrayList<Region>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_REGIONS, allRegionColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Region r = cursorToRegion(cursor);
			regions.add(r);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();

		return regions;
	}

	private Region cursorToRegion(Cursor cursor) {
		cursor.getLong(0);
		int id = cursor.getInt(1);
		String code = cursor.getString(2);
		String name = cursor.getString(3);

		Region r = new Region(id, code, name);

		return r;
	}

	public void addConsole(Console c) {
		Console existing = getConsole(c.getId());
		if (existing == null) {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_CONSOLES_EXTERNAL_ID, c.getId());
			values.put(DatabaseHelper.COLUMN_CONSOLES_CODE, c.getCode());
			values.put(DatabaseHelper.COLUMN_CONSOLES_NAME, c.getName());
			values.put(DatabaseHelper.COLUMN_CONSOLES_TYPE, String.valueOf(c.getType()));

			database.insert(DatabaseHelper.TABLE_CONSOLES, null, values);	
		} else {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_CONSOLES_CODE, c.getCode());
			values.put(DatabaseHelper.COLUMN_CONSOLES_NAME, c.getName());
			values.put(DatabaseHelper.COLUMN_CONSOLES_TYPE, String.valueOf(c.getType()));

			String where = DatabaseHelper.COLUMN_CONSOLES_EXTERNAL_ID + "=?";
			String[] whereArgs = new String[] {String.valueOf(existing.getId())};

			database.update(DatabaseHelper.TABLE_CONSOLES, values, where, whereArgs);
		}
	}

	public final int getConsoleCount() {
		Cursor mCount = database.rawQuery("select count(*) from " + DatabaseHelper.TABLE_CONSOLES, null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();

		return count;
	}

	public final Console getConsole(final int id) {
		Cursor cur = database.rawQuery("select * from " + DatabaseHelper.TABLE_CONSOLES + " where " + DatabaseHelper.COLUMN_CONSOLES_EXTERNAL_ID + "=?", new String [] { String.valueOf(id) });
		if (cur != null && cur.getCount() > 0) {
			cur.moveToFirst();
			Console c = cursorToConsole(cur);
			cur.close();

			return c;
		}

		return null;
	}

	public final Console getConsoleByCode(final String code) {
		Cursor cur = database.rawQuery("select * from " + DatabaseHelper.TABLE_CONSOLES + " where " + DatabaseHelper.COLUMN_CONSOLES_CODE + "=?", new String [] { code });
		if (cur != null && cur.getCount() > 0) {
			cur.moveToFirst();
			Console c = cursorToConsole(cur);
			cur.close();

			return c;
		}

		return null;
	}

	public ArrayList<Console> getAllConsoles() {
		ArrayList<Console> consoles = new ArrayList<Console>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_CONSOLES, allConsoleColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Console c = cursorToConsole(cursor);
			consoles.add(c);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();

		return consoles;
	}

	private Console cursorToConsole(Cursor cursor) {
		cursor.getLong(0);
		int id = cursor.getInt(1);
		String code = cursor.getString(2);
		String name = cursor.getString(3);
		String type = cursor.getString(4);
		ConsoleType ct = null;
		if (type.equals("EightBit")) {
			ct = ConsoleType.EightBit;
		} else if (type.equals("SixteenBit")) {
			ct = ConsoleType.SixteenBit;
		} else if (type.equals("ThirtyTwoBit")) {
			ct = ConsoleType.ThirtyTwoBit;
		}

		Console c = new Console(id, code, name, ct);

		return c;
	}

	public void addCompany(Company c) {
		Company existing = getCompany(c.getId());
		if (existing == null) {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_COMPANIES_EXTERNAL_ID, c.getId());
			values.put(DatabaseHelper.COLUMN_COMPANIES_CODE, c.getCode());
			values.put(DatabaseHelper.COLUMN_COMPANIES_NAME, c.getName());

			database.insert(DatabaseHelper.TABLE_COMPANIES, null, values);	
		} else {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_COMPANIES_CODE, c.getCode());
			values.put(DatabaseHelper.COLUMN_COMPANIES_NAME, c.getName());

			String where = DatabaseHelper.COLUMN_COMPANIES_EXTERNAL_ID + "=?";
			String[] whereArgs = new String[] {String.valueOf(existing.getId())};

			database.update(DatabaseHelper.TABLE_COMPANIES, values, where, whereArgs);
		}
	}

	public final int getCompanyCount() {
		Cursor mCount = database.rawQuery("select count(*) from " + DatabaseHelper.TABLE_COMPANIES, null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();

		return count;
	}

	public final Company getCompany(final int id) {
		Cursor cur = database.rawQuery("select * from " + DatabaseHelper.TABLE_COMPANIES + " where " + DatabaseHelper.COLUMN_COMPANIES_EXTERNAL_ID + "=?", new String [] { String.valueOf(id) });
		if (cur != null && cur.getCount() > 0) {
			cur.moveToFirst();
			Company c = cursorToCompany(cur);
			cur.close();

			return c;
		}

		return null;
	}

	public final Company getCompanyByCode(final String code) {
		Cursor cur = database.rawQuery("select * from " + DatabaseHelper.TABLE_COMPANIES + " where " + DatabaseHelper.COLUMN_COMPANIES_CODE + "=?", new String [] { code });
		if (cur != null && cur.getCount() > 0) {
			cur.moveToFirst();
			Company c = cursorToCompany(cur);
			cur.close();

			return c;
		}

		return null;
	}

	public ArrayList<Company> getAllCompanies() {
		ArrayList<Company> companies = new ArrayList<Company>();

		Cursor cursor = database.query(DatabaseHelper.TABLE_COMPANIES, allCompanyColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Company c = cursorToCompany(cursor);
			companies.add(c);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();

		return companies;
	}

	private Company cursorToCompany(Cursor cursor) {
		cursor.getLong(0);
		int id = cursor.getInt(1);
		String code = cursor.getString(2);
		String name = cursor.getString(3);

		Company c = new Company(id, code, name);

		return c;
	}
}