package com.candy.android.candyapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by marcingawel on 02.06.2016.
 */

public class ModelShop implements Parcelable {
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

    public ModelShop() {

    }

    protected ModelShop(Parcel in) {
        id = in.readString();
        owner = (ModelShopUser) in.readValue(ModelShopUser.class.getClassLoader());
        if (in.readByte() == 0x01) {
            users = new ArrayList<ModelShopUser>();
            in.readList(users, ModelShopUser.class.getClassLoader());
        } else {
            users = null;
        }
        name = in.readString();
        long tmpModificationDate = in.readLong();
        modificationDate = tmpModificationDate != -1 ? new Date(tmpModificationDate) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeValue(owner);
        if (users == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(users);
        }
        dest.writeString(name);
        dest.writeLong(modificationDate != null ? modificationDate.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ModelShop> CREATOR = new Parcelable.Creator<ModelShop>() {
        @Override
        public ModelShop createFromParcel(Parcel in) {
            return new ModelShop(in);
        }

        @Override
        public ModelShop[] newArray(int size) {
            return new ModelShop[size];
        }
    };
}
