package com.candy.android.candyapp.model;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;


/**
 * @author Marcin
 */

public class ModelShopItem {
    @StringDef({PIECE, GRAM, KILOGRAM, MILLILITER, LITER})
    @Retention(RetentionPolicy.SOURCE)
    @interface ItemMetric {}
    public static final String PIECE = "pcs";
    public static final String GRAM = "g";
    public static final String KILOGRAM = "kg";
    public static final String MILLILITER = "ml";
    public static final String LITER = "l";

    private String id;
    private String name;
    private double count;
    @ItemMetric private String metric;
    @SerializedName("modification_date")
    private Date modificationDate;

    public ModelShopItem(String id, String name, double count, @ItemMetric String metric, Date modificationDate) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.metric = metric;
        this.modificationDate = modificationDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCount() {
        return count;
    }

    public @ItemMetric String getMetric() {
        return metric;
    }

    public Date getModificationDate() {
        return modificationDate;
    }
}
