package dk.livingcode.android.gamemaster.model;

import android.os.Parcel;
import android.os.Parcelable;
import dk.livingcode.android.gamemaster.utility.Strings;

public class Company implements Parcelable {
	private int id;
	private String code;
	private String name;
	
	public Company() {
		this.id = -1;
		this.name = Strings.Empty;
	}
	
	public Company(final int id, final String code, final String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}
	
	public final int getId() {
		return this.id;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final String getCode() {
		return this.code;
	}
	
	@Override
	public String toString() {
		if (!Strings.isNullOrEmpty(name)) {
			return name;
		}
		
		return super.toString();
	}
	
	public Company(Parcel in){
		String[] data = new String[3];

		in.readStringArray(data);
		this.id = Integer.parseInt(data[0]);
		this.name = data[1];
		this.code = data[2];
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {
				String.valueOf(this.id),
				this.name,
				this.code
		});
	}

	public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
		public Company createFromParcel(Parcel in) {
			return new Company(in); 
		}

		public Company[] newArray(int size) {
			return new Company[size];
		}
	};
}