package com.candy.android.candyapp.model;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Marcin
 */

public class ModelFriend {
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
}
