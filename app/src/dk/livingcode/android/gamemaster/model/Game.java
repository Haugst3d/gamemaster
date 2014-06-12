package dk.livingcode.android.gamemaster.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import dk.livingcode.android.gamemaster.utility.Strings;

public class Game implements Parcelable {
	private int id;
	private ArrayList<Release> releases;
	private String defaultReleaseCode;
	private Release defaultRelease;
	private Console console;
	private Company developer;
	private Genre genre;
	private Genre subGenre;
	private String shortSummary;
	private String summary;

	public Game() {
	}
	
	public Game(Parcel in){
		// id
		this.id = in.readInt();
		
		// Get releases
		this.releases = new ArrayList<Release>();
		in.readTypedList(this.releases, Release.CREATOR);
		
		// default release code
		this.defaultReleaseCode = in.readString();
		this.defaultRelease = in.readParcelable(Release.class.getClassLoader());
		
		// Get consoles
		this.console = in.readParcelable(Console.class.getClassLoader());
		
		// Get the developer and publisher
		this.developer = in.readParcelable(Company.class.getClassLoader());
		
		// Get genres
		String g = in.readString();
		String sg = in.readString();
		
		// short summary
		this.shortSummary = in.readString();
		
		// summary
		this.summary = in.readString();
	}
	
	public String getShortSummary() {
		return shortSummary;
	}

	public void setShortSummary(String shortSummary) {
		this.shortSummary = shortSummary;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	private String description;
	private Rarity rarity;

	public final Release getDefaultRelease() {
		return this.defaultRelease;
	}

	public final String getDefaultReleaseCode() {
		return this.defaultReleaseCode;
	}

	public void setDefaultRelease(final String code) {
		this.defaultReleaseCode = code;
		if (Strings.isNullOrEmpty(code) && releases != null && releases.size() > 0) {
			defaultRelease = releases.get(0);
			if (defaultRelease != null && defaultRelease.getRegion() != null) {
				defaultReleaseCode = defaultRelease.getRegion().getCode();	
			}

			return;
		}

		if (releases != null) {
			for (Release r : releases) {
				if (r.getRegion().getCode().equals(code)) {
					this.defaultRelease = r;
					break;
				}
			}	
		}
	}

	public final int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setGenre(final Genre g) {
		this.genre = g;
	}

	public void setGenre(final String genreName) {
		//this.genre = g;
	}

	public final Genre getGenre() {
		return this.genre;
	}

	public void setSubGenre(final Genre g) {
		this.subGenre = g;
	}

	public void setSubGenre(final String genreName) {
		//this.genre = g;
	}

	public final Genre getSubGenre() {
		return this.subGenre;
	}

	public void setDescription(final String d) {
		this.description = d;
	}

	public final String getDescription() {
		return this.description;
	}

	public void setRarity(final Rarity r) {
		this.rarity = r;
	}

	public void setRarity(final String r) {
		//this.rarity = r;
	}

	public final Rarity getRarity() {
		return this.rarity;
	}

	public Company getDeveloper() {
		return this.developer;
	}

	public void setDeveloper(final Company developer) {
		this.developer = developer;
	}

	public final Console getConsole() {
		return this.console;
	}

	public void setConsole(final Console console) {
		this.console = console;
	}

	public final Release getRelease(final int id) {
		for (Release r : releases) {
			if (r.getRegion().getId() == id) {
				return r;
			}
		}
		
		return null;
	}
	
	public final ArrayList<Release> getReleases() {
		return this.releases;
	}

	public void setReleases(final ArrayList<Release> releases) {
		this.releases = releases;
	}

	public final String getReleaseString() {
		String reg = "";
		for (Release r : releases) {
			reg += r.getRegion().getName() + ", ";
		}

		return reg;
	}

	@Override
	public String toString() {
		if (this.releases.get(0) != null && !Strings.isNullOrEmpty(this.releases.get(0).getTitle())) {
			return this.releases.get(0).getTitle();
		}

		return super.toString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeTypedList(this.releases);
		dest.writeString(this.defaultReleaseCode);
		dest.writeParcelable(this.defaultRelease, flags);
		dest.writeParcelable(this.console, flags);
		dest.writeParcelable(this.developer, flags);
		dest.writeString(String.valueOf(this.genre));
		dest.writeString(String.valueOf(this.subGenre));
		dest.writeString(this.shortSummary);
		dest.writeString(this.summary);
	}

	public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
		public Game createFromParcel(Parcel in) {
			return new Game(in); 
		}

		public Game[] newArray(int size) {
			return new Game[size];
		}
	};
}