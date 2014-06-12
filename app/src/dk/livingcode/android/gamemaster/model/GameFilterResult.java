package dk.livingcode.android.gamemaster.model;

import java.util.ArrayList;

public class GameFilterResult {
	private ArrayList<Game> games;
	private int totalGames;
	private int totalGamesAfterFilter;
	private int totalReleases;
	private int totalReleasesAfterFilter;
	private String message;
	
	public ArrayList<Game> getGames() {
		return games;
	}
	
	public int getTotalReleases() {
		return totalReleases;
	}

	public void setTotalReleases(int totalReleases) {
		this.totalReleases = totalReleases;
	}

	public int getTotalReleasesAfterFilter() {
		return totalReleasesAfterFilter;
	}

	public void setTotalReleasesAfterFilter(int totalReleasesAfterFilter) {
		this.totalReleasesAfterFilter = totalReleasesAfterFilter;
	}
	
	public void setGames(ArrayList<Game> games) {
		this.games = games;
	}
	
	public int getTotalGames() {
		return totalGames;
	}
	
	public void setTotalGames(int totalGames) {
		this.totalGames = totalGames;
	}
	
	public int getTotalGamesAfterFilter() {
		return totalGamesAfterFilter;
	}
	
	public void setTotalGamesAfterFilter(int totalGamesAfterFilter) {
		this.totalGamesAfterFilter = totalGamesAfterFilter;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}