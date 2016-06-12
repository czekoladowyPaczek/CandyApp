package com.candy.android.candyapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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

    @Test
    public void isOwner() {
        ModelShop shop = new ModelShop("1", new ModelShopUser(1, "", ""), new ArrayList<>(), "", Calendar.getInstance().getTime());

        assertTrue(shop.isOwner(1));
        assertFalse(shop.isOwner(2));
    }

    @Test
    public void isInvited() {
        List<ModelShopUser> users = new ArrayList<>(2);
        users.add(new ModelShopUser(1, "", ""));
        users.add(new ModelShopUser(2, "", ""));
        ModelShop shop = new ModelShop("1", new ModelShopUser(1, "", ""), users, "", Calendar.getInstance().getTime());

        assertTrue(shop.isInvited(1));
        assertTrue(shop.isInvited(2));
        assertFalse(shop.isInvited(3));
    }

    @Test
    public void removeUser_shouldRemoveIfNotOwner() {
        List<ModelShopUser> users = new ArrayList<>(2);
        users.add(new ModelShopUser(1, "", ""));
        users.add(new ModelShopUser(2, "", ""));
        ModelShop shop = new ModelShop("1", new ModelShopUser(1, "", ""), users, "", Calendar.getInstance().getTime());

        shop.removeUser(users.get(1).getId());

        assertEquals(1, shop.getUsers().size());
    }

    @Test
    public void removeUser_shouldNotRemoveIfOwner() {
        List<ModelShopUser> users = new ArrayList<>(2);
        users.add(new ModelShopUser(1, "", ""));
        users.add(new ModelShopUser(2, "", ""));
        ModelShop shop = new ModelShop("1", new ModelShopUser(1, "", ""), users, "", Calendar.getInstance().getTime());

        shop.removeUser(users.get(0).getId());

        assertEquals(2, shop.getUsers().size());
    }
}