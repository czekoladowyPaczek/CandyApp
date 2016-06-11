package com.candy.android.candyapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Marcin
 */

public class ModelShopUser implements Parcelable {
    private long id;
    private String name;
    private String picture;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public ModelShopUser() {
        this(1, "name", "");
    }

    public ModelShopUser(long id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    protected ModelShopUser(Parcel in) {
        id = in.readLong();
        name = in.readString();
        picture = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(picture);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ModelShopUser> CREATOR = new Parcelable.Creator<ModelShopUser>() {
        @Override
        public ModelShopUser createFromParcel(Parcel in) {
            return new ModelShopUser(in);
        }

        @Override
        public ModelShopUser[] newArray(int size) {
            return new ModelShopUser[size];
        }
    };
}
