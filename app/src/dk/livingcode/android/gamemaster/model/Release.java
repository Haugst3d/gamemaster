package dk.livingcode.android.gamemaster.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Release implements Parcelable {
	private int id;
	private int gameId;
	private Region region;
	private String title;
	private Date released;
	private Company publisher;
	private String coverIdentifier = "ten_yard_fight_us";
	
	public Release(final int id, final int gameId, final Region r, final Company publisher, final String title, final Date released) {
		this.id = id;
		this.gameId = gameId;
		this.region = r;
		this.title = title;
		this.released = released;
		this.publisher = publisher;
	}
	
	public Release(Parcel in) {
		this.id = in.readInt();
		this.gameId = in.readInt();
		this.region = in.readParcelable(Region.class.getClassLoader());
		this.title = in.readString();
		this.released = new Date(in.readLong());
		this.publisher = in.readParcelable(Company.class.getClassLoader());
		this.coverIdentifier = in.readString();
	}
	
	public void setRegion(final Region region) {
		this.region = region;
	}
	
	public final int getId() {
		return this.id;
	}
	
	public final int getGameId() {
		return this.gameId;
	}
	
	public final Region getRegion() {
		return this.region;
	}
	
	public final Company getPublisher() {
		return this.publisher;
	}
	
	public void setTitle(final String title) {
		this.title = title;
	}
	
	public final String getTitle() {
		return this.title;
	}
	
	public void setReleased(final Date released) {
		this.released = released;
	}
	
	public final Date getReleased() {
		return this.released;
	}
	
	public final String getCoverIdentifier() {
		return this.coverIdentifier;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeInt(this.gameId);
		dest.writeParcelable(region, flags);
		dest.writeString(title);
		dest.writeLong(released.getTime());
		dest.writeParcelable(publisher, flags);
		dest.writeString(coverIdentifier);
	}

	public static final Parcelable.Creator<Release> CREATOR = new Parcelable.Creator<Release>() {
		public Release createFromParcel(Parcel in) {
			return new Release(in); 
		}

		public Release[] newArray(int size) {
			return new Release[size];
		}
	};
}