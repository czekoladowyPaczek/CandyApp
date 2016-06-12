package com.candy.android.candyapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.candy.android.candyapp.ui.ModelViewHolder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by marcingawel on 02.06.2016.
 */

public class ModelShop implements Parcelable, ModelViewHolder {
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
        this("1", new ModelShopUser(1, "", ""), new ArrayList<>(0), "", Calendar.getInstance().getTime());
    }

    public ModelShop(String id, ModelShopUser owner, List<ModelShopUser> users, String name, Date modificationDate) {
        this.id = id;
        this.owner = owner;
        this.users = users;
        this.name = name;
        this.modificationDate = modificationDate;
    }

    protected ModelShop(Parcel in) {
        id = in.readString();
        owner = (ModelShopUser) in.readValue(ModelShopUser.class.getClassLoader());
        if (in.readByte() == 0x01) {
            users = new ArrayList<>();
            in.readList(users, ModelShopUser.class.getClassLoader());
        } else {
            users = null;
        }
        name = in.readString();
        long tmpModificationDate = in.readLong();
        modificationDate = tmpModificationDate != -1 ? new Date(tmpModificationDate) : null;
    }

    public boolean isOwner(long userId) {
        return owner.getId() == userId;
    }

    public boolean isInvited(long userId) {
        for (ModelShopUser user : users) {
            if (user.getId() == userId) {
                return true;
            }
        }
        return false;
    }

    public void removeUser(long userId) {
        if (owner.getId() != userId) {
            Iterator<ModelShopUser> userIterator = users.iterator();
            while(userIterator.hasNext()) {
                if (userIterator.next().getId() == userId) {
                    userIterator.remove();
                    break;
                }
            }
        }
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
