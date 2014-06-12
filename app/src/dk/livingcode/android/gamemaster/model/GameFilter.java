package dk.livingcode.android.gamemaster.model;

import java.util.ArrayList;

import dk.livingcode.android.gamemaster.utility.Strings;

import android.os.Parcel;
import android.os.Parcelable;

public class GameFilter implements Parcelable {
	private String released;
	private Company developer;
	private Company publisher;
	private ArrayList<Region> regions;
	private ArrayList<Console> consoles;

	public GameFilter() {
		this.released = Strings.Empty;
		this.developer = null;
		this.publisher = null;
		this.regions = new ArrayList<Region>();
		this.consoles = new ArrayList<Console>();
	}

	public GameFilter(
			final String released, 
			final Company developer, 
			final Company publisher, 
			final ArrayList<Region> rs, 
			final ArrayList<Console> cs) {
		this.released = released;
		this.developer = developer;
		this.publisher = publisher;
		this.regions = rs;
		this.consoles = cs;
	}

	public GameFilter(Parcel in){
		this.released = in.readString();
		
		// Get the developer and publisher
		this.developer = in.readParcelable(Company.class.getClassLoader());
		this.publisher = in.readParcelable(Company.class.getClassLoader());

		// Get regions
		regions = new ArrayList<Region>();
		in.readTypedList(regions, Region.CREATOR);
		
		// Get consoles
		consoles = new ArrayList<Console>();
		in.readTypedList(consoles, Console.CREATOR);
	}
	
	public final String getReleased() {
		return this.released;
	}
	
	public void setReleased(final String r) {
		this.released = r;
	}
	
	public final Company getDeveloper() {
		return this.developer;
	}
	
	public void setDeveloper(final Company d) {
		this.developer = d;
	}

	public final Company getPublisher() {
		return this.publisher;
	}
	
	public void setPublisher(final Company p) {
		this.publisher = p;
	}
	
	public void setRegion(final Region r) {
		this.regions.add(r);
	}
	
	public final ArrayList<Region> getRegions() {
		return this.regions;
	}
	
	public final Region getRegion() {
		return (this.regions != null && this.regions.size() > 0) ? this.regions.get(0) : null;
	}
	
	public void setConsole(final Console c) {
		this.consoles.add(c);
	}
	
	public final ArrayList<Console> getConsoles() {
		return this.consoles;
	}
	
	public final Console getConsole() {
		return this.consoles.size() > 0 ? this.consoles.get(0) : null;
	}
	
	public boolean getIsEmpty() {
		return Strings.isNullOrEmpty(released) 
				&& publisher == null 
				&& developer == null 
				&& (regions == null	|| regions.size() == 0)
				&& (consoles == null || consoles.size() == 0);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(!Strings.isNullOrEmpty(this.released) ? this.released : Strings.Empty);
		dest.writeParcelable(developer, flags);
		dest.writeParcelable(publisher, flags);
		dest.writeTypedList(regions);
		dest.writeTypedList(consoles);
	}

	public static final Parcelable.Creator<GameFilter> CREATOR = new Parcelable.Creator<GameFilter>() {
		public GameFilter createFromParcel(Parcel in) {
			return new GameFilter(in); 
		}

		public GameFilter[] newArray(int size) {
			return new GameFilter[size];
		}
	};
}