package dk.livingcode.android.gamemaster.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class CollectedGame implements Parcelable {
	private int id;
	private String userId;
	private Game game;
	private Console console;
	private Region region;
	private String regionExtra;
	private boolean hasCassette;
	private int cassetteState;
	private boolean hasInstructions;
	private int instructionsState;
	private boolean hasBox;
	private int boxState;
	private boolean isSealed;
	private int score;
	private String notes;
	private Date dateAdded;
	private Date dateUpdated;
	private Date dateSynchronized;
	
	public CollectedGame() {		
	}
	
	public CollectedGame(Parcel in) {
		this.id = in.readInt();
		this.userId = in.readString();
		this.game = in.readParcelable(Game.class.getClassLoader());
		this.console = in.readParcelable(Console.class.getClassLoader());
		this.region = in.readParcelable(Region.class.getClassLoader());
		this.regionExtra = in.readString();
		this.hasCassette = in.readInt() == 0 ? false : true;
		this.cassetteState = in.readInt();
		this.hasInstructions = in.readInt() == 0 ? false : true;
		this.instructionsState = in.readInt();
		this.hasBox = in.readInt() == 0 ? false : true;
		this.boxState = in.readInt();
		this.isSealed = in.readInt() == 0 ? false : true;
		this.score = in.readInt();
		this.notes = in.readString();
		this.dateAdded = new Date(in.readLong());
		this.dateUpdated = new Date(in.readLong());
		this.dateSynchronized = new Date(in.readLong());
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Console getConsole() {
		return console;
	}
	
	public void setConsole(Console console) {
		this.console = console;
	}
	
	public Region getRegion() {
		return region;
	}
	
	public void setRegion(Region region) {
		this.region = region;
	}
	
	public String getRegionExtra() {
		return regionExtra;
	}
	
	public void setRegionExtra(String regionExtra) {
		this.regionExtra = regionExtra;
	}
	
	public boolean getHasCassette() {
		return hasCassette;
	}
	
	public void setHasCassette(boolean hasCassette) {
		this.hasCassette = hasCassette;
	}
	
	public int getCassetteState() {
		return cassetteState;
	}
	
	public void setCassetteState(int cassetteState) {
		this.cassetteState = cassetteState;
	}
	
	public boolean getHasInstructions() {
		return hasInstructions;
	}
	
	public void setHasInstructions(boolean hasInstructions) {
		this.hasInstructions = hasInstructions;
	}
	
	public int getInstructionsState() {
		return instructionsState;
	}
	
	public void setInstructionsState(int instructionsState) {
		this.instructionsState = instructionsState;
	}
	
	public boolean getHasBox() {
		return hasBox;
	}
	
	public void setHasBox(boolean hasBox) {
		this.hasBox = hasBox;
	}
	
	public int getBoxState() {
		return boxState;
	}
	
	public void setBoxState(int boxState) {
		this.boxState = boxState;
	}
	
	public boolean getIsSealed() {
		return isSealed;
	}
	
	public void setIsSealed(boolean isSealed) {
		this.isSealed = isSealed;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public Date getDateAdded() {
		return dateAdded;
	}
	
	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	
	public Date getDateUpdated() {
		return dateUpdated;
	}
	
	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	
	public Date getDateSynchronized() {
		return dateSynchronized;
	}
	
	public void setDateSynchronized(Date dateSynschronized) {
		this.dateSynchronized = dateSynschronized;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.userId);
		dest.writeParcelable(this.game, flags);
		dest.writeParcelable(this.console, flags);
		dest.writeParcelable(this.region, flags);
		dest.writeString(this.regionExtra);
		dest.writeInt(this.hasCassette ? 1 : 0);
		dest.writeInt(this.cassetteState);
		dest.writeInt(this.hasInstructions ? 1 : 0);
		dest.writeInt(this.instructionsState);
		dest.writeInt(this.hasBox ? 1 : 0);
		dest.writeInt(this.boxState);
		dest.writeInt(this.isSealed ? 1 : 0);
		dest.writeInt(this.score);
		dest.writeString(this.notes);
		dest.writeLong(this.dateAdded != null ? this.dateAdded.getTime() : 0);
		dest.writeLong(this.dateUpdated != null ? this.dateUpdated.getTime() : 0);
		dest.writeLong(this.dateSynchronized != null ? this.dateSynchronized.getTime() : 0);
	}

	public static final Parcelable.Creator<CollectedGame> CREATOR = new Parcelable.Creator<CollectedGame>() {
		public CollectedGame createFromParcel(Parcel in) {
			return new CollectedGame(in); 
		}

		public CollectedGame[] newArray(int size) {
			return new CollectedGame[size];
		}
	};
}