package dk.livingcode.android.gamemaster.database;

import java.util.HashMap;

import dk.livingcode.android.gamemaster.R;

public class GameCovers {
	private static HashMap<String, Integer> map;
	
	private static void load() {
		if (map == null || map.size() == 0) {
			map = new HashMap<String, Integer>();
			map.put("ten_yard_fight_us", R.drawable.ten_yard_fight_us);
			map.put("ten_yard_fight_jp", R.drawable.ten_yard_fight_jp);
		}
	}
	
	public static final int getCoverResourceId(final String coverName) {
		load();
		
		if (map.containsKey(coverName)) {
			return map.get(coverName);
		}
		
		return 0;
	}
}