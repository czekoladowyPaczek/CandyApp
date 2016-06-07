package com.candy.android.candyapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;

/**
 * @author Marcin
 */
public class ModelShopItemTest {
    private static final String JSON = "{\"id\": \"57430e7f4b7d033421d5cfad\", \"name\": \"Apple\", \"count\": 10, \"metric\": \"pcs\", \"modification_date\": \"2016-05-23T14:06:55.816Z\"}";

    @Test
    public void testInitialization() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, Calendar.MAY);
        cal.set(Calendar.DAY_OF_MONTH, 23);
        cal.set(Calendar.HOUR_OF_DAY, 14);
        cal.set(Calendar.MINUTE, 06);
        cal.set(Calendar.SECOND, 55);
        cal.set(Calendar.MILLISECOND, 816);

        ModelShopItem item = getModelShopItem();

        assertEquals("57430e7f4b7d033421d5cfad", item.getId());
        assertEquals("Apple", item.getName());
        assertEquals(10.0, item.getCount());
        assertEquals(ModelShopItem.PIECE, item.getMetric());
        assertEquals(cal.getTime(), item.getModificationDate());
    }

    public static ModelShopItem getModelShopItem() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();
        return gson.fromJson(JSON, ModelShopItem.class);
    }
}