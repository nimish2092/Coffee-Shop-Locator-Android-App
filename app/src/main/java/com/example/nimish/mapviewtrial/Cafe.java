package com.example.nimish.mapviewtrial;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NIMISH on 25-07-2016.
 */
public class Cafe implements Parcelable {

    public String name = "";
    public double rating = 0;

    public double getRating() {
        return rating;
    }

    public static final Creator<Cafe> CREATOR = new Creator<Cafe>() {
        @Override
        public Cafe createFromParcel(Parcel in) {
            return new Cafe(in);
        }

        @Override
        public Cafe[] newArray(int size) {
            return new Cafe[size];
        }
    };

    protected Cafe(Parcel in) {
        this.name = in.readString();
        this.rating = in.readDouble();

    }

    public Cafe() {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeDouble(rating);

    }
}
