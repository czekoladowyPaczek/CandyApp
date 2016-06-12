package com.candy.android.candyapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringDef;

import com.candy.android.candyapp.ui.ModelViewHolder;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Marcin
 */

public class ModelFriend implements Parcelable, ModelViewHolder {
    @StringDef({STATUS_ACCEPTED, STATUS_INVITED, STATUS_WAITING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FriendStatus {}
    public static final String STATUS_WAITING = "W";
    public static final String STATUS_ACCEPTED = "A";
    public static final String STATUS_INVITED = "I";

    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("picture")
    private String picture;

    @FriendStatus
    @SerializedName("status")
    private String status;

    public ModelFriend(long id, String name, String picture, @FriendStatus String status) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public @FriendStatus String getStatus() {
        return status;
    }

    protected ModelFriend(Parcel in) {
        id = in.readLong();
        name = in.readString();
        picture = in.readString();
        @FriendStatus String status = in.readString();
        this.status = status;
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
        dest.writeString(status);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ModelFriend> CREATOR = new Parcelable.Creator<ModelFriend>() {
        @Override
        public ModelFriend createFromParcel(Parcel in) {
            return new ModelFriend(in);
        }

        @Override
        public ModelFriend[] newArray(int size) {
            return new ModelFriend[size];
        }
    };
}
