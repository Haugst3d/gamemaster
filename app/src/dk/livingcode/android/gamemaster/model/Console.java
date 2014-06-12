package dk.livingcode.android.gamemaster.model;

import dk.livingcode.android.gamemaster.utility.Strings;
import android.os.Parcel;
import android.os.Parcelable;

public class Console implements Parcelable {
	private int id;
	private String code;
	private String name;
	private ConsoleType type;
	
	public final int getId() {
		return this.id;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final String getCode() {
		return this.code;
	}
	
	public final ConsoleType getType() {
		return this.type;
	}
	
	public Console(final int id, final String code, final String name, final ConsoleType type) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.type = type;
	}
	
	public Console(Parcel in) {
		String[] data = new String[4];

		in.readStringArray(data);
		this.id = Integer.parseInt(data[0]);
		this.name = data[1];
		this.code = data[2];
		
		String t = data[3];
		if (t.equals("EightBit")) {
			this.type = ConsoleType.EightBit;
		} else if (t.equals("SixteenBit")) {
			this.type = ConsoleType.SixteenBit;
		} else if (t.equals("ThirtyTwoBit")) {
			this.type = ConsoleType.ThirtyTwoBit;
		}
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
				this.name,
				String.valueOf(this.type)
		});
	}
	
	public static final Parcelable.Creator<Console> CREATOR = new Parcelable.Creator<Console>() {
		public Console createFromParcel(Parcel in) {
			return new Console(in); 
		}

		public Console[] newArray(int size) {
			return new Console[size];
		}
	};
}