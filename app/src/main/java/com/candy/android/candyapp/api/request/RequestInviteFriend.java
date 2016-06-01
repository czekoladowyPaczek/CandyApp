package com.candy.android.candyapp.api.request;

import com.google.gson.annotations.SerializedName;

/**
 * @author Marcin
 */

public class RequestInviteFriend {
    @SerializedName("email")
    private String email;

    public RequestInviteFriend(String email) {
        this.email = email;
    }
}
