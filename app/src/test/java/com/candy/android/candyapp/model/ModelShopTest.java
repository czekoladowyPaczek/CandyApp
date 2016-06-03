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
public class ModelShopTest {
    private static final String JSON = "{\"id\": \"573cc142757c3a08030aa993\",\"name\": \"List name 1\"," +
            "\"owner\": {\"id\": 115864548829752,\"name\": \"Joe Alabahfbbjejd Putnamwitz\",\"picture\": \"https://graph.facebook.com/115864548829752/picture?type=large\"}," +
            "\"users\": [{\"id\": 115864548829752,\"name\": \"Joe Alabahfbbjejd Putnamwitz\",\"picture\": \"https://graph.facebook.com/115864548829752/picture?type=large\"}]," +
            "\"modification_date\": \"2016-05-18T19:23:46.278Z\"}";

    @Test
    public void shouldBuildInstance() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, Calendar.MAY);
        cal.set(Calendar.DAY_OF_MONTH, 18);
        cal.set(Calendar.HOUR_OF_DAY, 19);
        cal.set(Calendar.MINUTE, 23);
        cal.set(Calendar.SECOND, 46);
        cal.set(Calendar.MILLISECOND, 278);
        ModelShop shop = getModelShop();

        assertEquals("573cc142757c3a08030aa993", shop.getId());
        assertEquals("List name 1", shop.getName());
        assertEquals(115864548829752L, shop.getOwner().getId());
        assertEquals(1, shop.getUsers().size());
        assertEquals(115864548829752L, shop.getUsers().get(0).getId());
        assertEquals(cal.getTime(), shop.getModificationDate());
    }

    public static ModelShop getModelShop() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();
        return gson.fromJson(JSON, ModelShop.class);
    }
}