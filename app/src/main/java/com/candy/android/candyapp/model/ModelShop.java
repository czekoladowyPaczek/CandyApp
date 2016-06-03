package com.candy.android.candyapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by marcingawel on 02.06.2016.
 */

public class ModelShop {
    private String id;
    private ModelShopUser owner;
    private List<ModelShopUser> users;
    private String name;
    @SerializedName("modification_date")
    private Date modificationDate;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ModelShopUser getOwner() {
        return owner;
    }

    public List<ModelShopUser> getUsers() {
        return users;
    }

    public Date getModificationDate() {
        return modificationDate;
    }
}
