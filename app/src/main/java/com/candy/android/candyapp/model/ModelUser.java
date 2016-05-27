package com.candy.android.candyapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Marcin
 */

public class ModelUser {
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("picture")
    private String picture;
    @SerializedName("email")
    private String email;
    @SerializedName("friends")
    private List<ModelFriend> friends;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getEmail() {
        return email;
    }

    public List<ModelFriend> getFriends() {
        return friends;
    }
}
