package dk.livingcode.android.gamemaster.adapters;

import dk.livingcode.android.gamemaster.model.Game;

public interface IGameSelectedListener {
	void onGameCollectedClicked(Game g, int index);
	void onGameDetailsClicked(Game g, int index);
}