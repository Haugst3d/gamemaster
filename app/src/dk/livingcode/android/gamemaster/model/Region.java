package dk.livingcode.android.gamemaster.model;

import dk.livingcode.android.gamemaster.utility.Strings;
import android.os.Parcel;
import android.os.Parcelable;

public class Region implements Parcelable {
	private int id;
	private String code;
	private String name;
	
	public final int getId() {
		return this.id;
	}
	
	public final String getCode() {
		return this.code;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public Region(final int id, final String code, final String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}
	
	public Region(Parcel in){
		String[] data = new String[3];

		in.readStringArray(data);
		this.id = Integer.parseInt(data[0]);
		this.code = data[1];
		this.name = data[2];
	}	

	@Override
	public String toString() {
		if (!Strings.isNullOrEmpty(name)) {
			return name;
		}
		
		return super.toString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {
				String.valueOf(this.id),
				this.code,
				this.name
		});
	}
	
	public static final Parcelable.Creator<Region> CREATOR = new Parcelable.Creator<Region>() {
		public Region createFromParcel(Parcel in) {
			return new Region(in); 
		}

		public Region[] newArray(int size) {
			return new Region[size];
		}
	};
}