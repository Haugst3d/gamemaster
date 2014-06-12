package dk.livingcode.android.gamemaster.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String TABLE_REGIONS = "regions";
	public static final String TABLE_COMPANIES = "companies";
	public static final String TABLE_CONSOLES = "consoles";
	public static final String TABLE_RELEASES = "releases";
	public static final String TABLE_GAMES = "games";
	public static final String TABLE_COLLECTION = "collection";

	public static final String COLUMN_REGIONS_ID = "_id";
	public static final String COLUMN_REGIONS_EXTERNAL_ID = "external_id";
	public static final String COLUMN_REGIONS_CODE = "code";
	public static final String COLUMN_REGIONS_NAME = "name";

	public static final String COLUMN_COMPANIES_ID = "_id";
	public static final String COLUMN_COMPANIES_EXTERNAL_ID = "external_id";
	public static final String COLUMN_COMPANIES_CODE = "code";
	public static final String COLUMN_COMPANIES_NAME = "name";

	public static final String COLUMN_CONSOLES_ID = "_id";
	public static final String COLUMN_CONSOLES_EXTERNAL_ID = "external_id";
	public static final String COLUMN_CONSOLES_CODE = "code";
	public static final String COLUMN_CONSOLES_NAME = "name";
	public static final String COLUMN_CONSOLES_TYPE = "type";

	public static final String COLUMN_RELEASES_ID = "_id";
	public static final String COLUMN_RELEASES_GAME_ID = "game_id";
	public static final String COLUMN_RELEASES_REGION_ID = "region_id";
	public static final String COLUMN_RELEASES_TITLE = "title";
	public static final String COLUMN_RELEASES_RELEASED = "released";
	public static final String COLUMN_RELEASES_PUBLISHER_ID = "publisher_id";

	public static final String COLUMN_GAMES_ID = "_id";
	public static final String COLUMN_GAMES_EXTERNAL_ID = "external_id";
	public static final String COLUMN_GAMES_CONSOLE_ID = "console_id";
	public static final String COLUMN_GAMES_DEVELOPER_ID = "developer_id";
	public static final String COLUMN_GAMES_GENRE = "genre";
	public static final String COLUMN_GAMES_SUB_GENRE = "sub_genre";
	public static final String COLUMN_GAMES_SHORT_SUMMARY = "short_summary";
	public static final String COLUMN_GAMES_SUMMARY = "summary";
	public static final String COLUMN_GAMES_DESCRIPTION = "description";
	public static final String COLUMN_GAMES_RARITY = "rarity";
	public static final String COLUMN_GAMES_DEFAULT_RELEASE = "default_release";
	
	public static final String COLUMN_COLLECTION_ID = "_id";
	public static final String COLUMN_COLLECTION_USER_ID = "user_id";
	public static final String COLUMN_COLLECTION_GAME_ID = "game_id";
	public static final String COLUMN_COLLECTION_CONSOLE_ID = "console_id";
	public static final String COLUMN_COLLECTION_REGION_ID = "region_id";
	public static final String COLUMN_COLLECTION_REGION_EXTRA = "region_extra";
	public static final String COLUMN_COLLECTION_HAS_CASSETTE = "has_cassette";
	public static final String COLUMN_COLLECTION_CASSETTE_STATE = "cassette_state";
	public static final String COLUMN_COLLECTION_HAS_INSTRUCTIONS = "has_instructions";
	public static final String COLUMN_COLLECTION_INSTRUCTIONS_STATE = "instructions_state";
	public static final String COLUMN_COLLECTION_HAS_BOX = "has_box";
	public static final String COLUMN_COLLECTION_BOX_STATE = "box_state";
	public static final String COLUMN_COLLECTION_IS_SEALED = "is_sealed";
	public static final String COLUMN_COLLECTION_SCORE = "score";
	public static final String COLUMN_COLLECTION_NOTES = "notes";
	public static final String COLUMN_COLLECTION_DATE_ADDED = "date_added";
	public static final String COLUMN_COLLECTION_DATE_UPDATED = "date_updated";
	public static final String COLUMN_COLLECTION_DATE_SYNCHRONIZED = "date_synchronized";
	

	private static final String DATABASE_NAME = "gamemaster.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE_REGIONS = "CREATE TABLE "
			+ TABLE_REGIONS + "(" 
			+ COLUMN_REGIONS_ID + " integer primary key autoincrement, " 
			+ COLUMN_REGIONS_EXTERNAL_ID + " integer not null, "
			+ COLUMN_REGIONS_CODE + " text not null, "
			+ COLUMN_REGIONS_NAME + " text not null);";

	private static final String DATABASE_CREATE_REGIONS_ID_INDEX = "CREATE INDEX " + TABLE_REGIONS + "_external_id_idx ON " + TABLE_REGIONS + "(" + COLUMN_REGIONS_EXTERNAL_ID + ");";
	private static final String DATABASE_CREATE_REGIONS_CODE_INDEX = "CREATE INDEX " + TABLE_REGIONS + "_code_idx ON " + TABLE_REGIONS + "(" + COLUMN_REGIONS_CODE + ");";
	
	private static final String DATABASE_CREATE_COMPANIES = "CREATE TABLE "
			+ TABLE_COMPANIES + "(" 
			+ COLUMN_COMPANIES_ID + " integer primary key autoincrement, " 
			+ COLUMN_COMPANIES_EXTERNAL_ID + " integer not null, "
			+ COLUMN_COMPANIES_CODE + " text not null, "
			+ COLUMN_COMPANIES_NAME + " text);";

	private static final String DATABASE_CREATE_COMPANIES_ID_INDEX = "CREATE INDEX " + TABLE_COMPANIES + "_external_id_idx ON " + TABLE_COMPANIES + "(" + COLUMN_COMPANIES_EXTERNAL_ID + ");";
	private static final String DATABASE_CREATE_COMPANIES_CODE_INDEX = "CREATE INDEX " + TABLE_COMPANIES + "_code_idx ON " + TABLE_COMPANIES + "(" + COLUMN_COMPANIES_CODE + ");";
	
	private static final String DATABASE_CREATE_CONSOLES = "CREATE TABLE "
			+ TABLE_CONSOLES + "(" 
			+ COLUMN_CONSOLES_ID + " integer primary key autoincrement, " 
			+ COLUMN_CONSOLES_EXTERNAL_ID + " integer not null, "
			+ COLUMN_CONSOLES_CODE + " text not null, "
			+ COLUMN_CONSOLES_NAME + " text not null, "
			+ COLUMN_CONSOLES_TYPE + " text not null);";

	private static final String DATABASE_CREATE_CONSOLES_ID_INDEX = "CREATE INDEX " + TABLE_CONSOLES + "_external_id_idx ON " + TABLE_CONSOLES + "(" + COLUMN_CONSOLES_EXTERNAL_ID + ");";
	private static final String DATABASE_CREATE_CONSOLES_CODE_INDEX = "CREATE INDEX " + TABLE_CONSOLES + "_code_idx ON " + TABLE_CONSOLES + "(" + COLUMN_CONSOLES_CODE + ");";
	
	private static final String DATABASE_CREATE_RELEASES = "CREATE TABLE "
			+ TABLE_RELEASES + "(" 
			+ COLUMN_RELEASES_ID + " integer primary key autoincrement, "
			+ COLUMN_RELEASES_GAME_ID + " integer not null, "
			+ COLUMN_RELEASES_REGION_ID + " integer not null, "
			+ COLUMN_RELEASES_TITLE + " text not null, "
			+ COLUMN_RELEASES_RELEASED + " integer, "
			+ COLUMN_RELEASES_PUBLISHER_ID + " integer);";

	private static final String DATABASE_CREATE_RELEASES_ID_INDEX = "CREATE INDEX " + TABLE_RELEASES + "_game_id_idx ON " + TABLE_RELEASES + "(" + COLUMN_RELEASES_GAME_ID + ");";
	
	private static final String DATABASE_CREATE_GAMES = "CREATE TABLE "
			+ TABLE_GAMES + "(" 
			+ COLUMN_GAMES_ID + " integer primary key autoincrement, " 
			+ COLUMN_GAMES_EXTERNAL_ID + " integer not null, "
			+ COLUMN_GAMES_CONSOLE_ID + " integer not null, "
			+ COLUMN_GAMES_DEVELOPER_ID + " integer, "
			+ COLUMN_GAMES_GENRE + " text, "
			+ COLUMN_GAMES_SUB_GENRE + " text, "
			+ COLUMN_GAMES_SHORT_SUMMARY + " text, "
			+ COLUMN_GAMES_SUMMARY + " text, "
			+ COLUMN_GAMES_DESCRIPTION + " text, "
			+ COLUMN_GAMES_RARITY + " text, "
			+ COLUMN_GAMES_DEFAULT_RELEASE + " text not null);";

	private static final String DATABASE_CREATE_GAMES_ID_INDEX = "CREATE INDEX " + TABLE_GAMES + "_external_id_idx ON " + TABLE_GAMES + "(" + COLUMN_GAMES_EXTERNAL_ID + ");";
	
	public static final String DATABASE_CREATE_COLLECTION = "CREATE TABLE "
			+ TABLE_COLLECTION + "(" 
			+ COLUMN_COLLECTION_ID + " integer primary key autoincrement, " 
			+ COLUMN_COLLECTION_USER_ID + " text not null, "
			+ COLUMN_COLLECTION_GAME_ID + " integer not null, "
			+ COLUMN_COLLECTION_CONSOLE_ID + " integer not null, "
			+ COLUMN_COLLECTION_REGION_ID + " integer not null, "
			+ COLUMN_COLLECTION_REGION_EXTRA + " text, "
			+ COLUMN_COLLECTION_HAS_CASSETTE + " integer default 0, "
			+ COLUMN_COLLECTION_CASSETTE_STATE + " integer default 0, "
			+ COLUMN_COLLECTION_HAS_INSTRUCTIONS + " integer default 0, "
			+ COLUMN_COLLECTION_INSTRUCTIONS_STATE + " integer default 0, "
			+ COLUMN_COLLECTION_HAS_BOX + " integer default 0, "
			+ COLUMN_COLLECTION_BOX_STATE + " integer default 0, "
			+ COLUMN_COLLECTION_IS_SEALED + " integer default 0, "
			+ COLUMN_COLLECTION_SCORE + " integer default 0, "
			+ COLUMN_COLLECTION_NOTES + " text, "
			+ COLUMN_COLLECTION_DATE_ADDED + " integer not null, "
			+ COLUMN_COLLECTION_DATE_UPDATED + " integer, "
			+ COLUMN_COLLECTION_DATE_SYNCHRONIZED + " integer);";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_REGIONS);
		database.execSQL(DATABASE_CREATE_REGIONS_ID_INDEX);
		database.execSQL(DATABASE_CREATE_REGIONS_CODE_INDEX);
		
		database.execSQL(DATABASE_CREATE_COMPANIES);
		database.execSQL(DATABASE_CREATE_COMPANIES_ID_INDEX);
		database.execSQL(DATABASE_CREATE_COMPANIES_CODE_INDEX);
		
		database.execSQL(DATABASE_CREATE_CONSOLES);
		database.execSQL(DATABASE_CREATE_CONSOLES_ID_INDEX);
		database.execSQL(DATABASE_CREATE_CONSOLES_CODE_INDEX);
		
		database.execSQL(DATABASE_CREATE_RELEASES);
		database.execSQL(DATABASE_CREATE_RELEASES_ID_INDEX);
		
		database.execSQL(DATABASE_CREATE_GAMES);
		database.execSQL(DATABASE_CREATE_GAMES_ID_INDEX);
		
		database.execSQL(DATABASE_CREATE_COLLECTION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < newVersion) {
			Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECTION);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGIONS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANIES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSOLES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_RELEASES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);

			onCreate(db);
		}
	}
}