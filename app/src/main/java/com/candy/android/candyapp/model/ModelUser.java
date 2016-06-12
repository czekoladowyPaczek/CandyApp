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

    public ModelUser(long id, String name, String picture, String email, List<ModelFriend> friends) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.email = email;
        this.friends = friends;
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

    public String getEmail() {
        return email;
    }

    public List<ModelFriend> getFriends() {
        return friends;
    }

    public void setFriends(List<ModelFriend> friends) {
        this.friends = friends;
    }

    public boolean isFriend(long userId) {
        for (ModelFriend friend : friends) {
            if (friend.getId() == userId && friend.getStatus().equals(ModelFriend.STATUS_ACCEPTED)) {
                return true;
            }
        }
        return false;
    }
}
