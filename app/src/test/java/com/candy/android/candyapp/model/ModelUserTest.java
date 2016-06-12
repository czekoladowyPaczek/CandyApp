package com.candy.android.candyapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Marcin
 */
public class ModelUserTest {
    public static final String TEST_USER_LOGIN = "{" +
            "\"id\": 106795866405632,\n" +
            "\"name\": \"Dorothy Alabbefefeefi Wisemanson\"," +
            "\"email\": \"jpodjwv_wisemanson_1463582102@tfbnw.net\"," +
            "\"picture\": \"https://graph.facebook.com/106795866405632/picture?type=large\"," +
            "\"friends\": [" +
            "{" +
            "\"id\": 109370069480913," +
            "\"name\": \"Will Alabbcgfjgjfe Liwitz\",\n" +
            "\"picture\": \"https://graph.facebook.com/109370069480913/picture?type=large\"," +
            "\"status\": \"W\"" +
            "}," +
            "{" +
            "\"id\": 115864548829752," +
            "\"name\": \"Joe Alabahfbbjejd Putnamwitz\"," +
            "\"picture\": \"https://graph.facebook.com/115864548829752/picture?type=large\"," +
            "\"status\": \"A\"" +
            "}" +
            "]" +
            "}";

    @Test
    public void shouldBuildObject() {
        Gson gson = new GsonBuilder()
                .create();

        ModelUser user = gson.fromJson(TEST_USER_LOGIN, ModelUser.class);

        assertEquals(106795866405632L, user.getId());
        assertEquals("Dorothy Alabbefefeefi Wisemanson", user.getName());
        assertEquals("jpodjwv_wisemanson_1463582102@tfbnw.net", user.getEmail());
        assertEquals("https://graph.facebook.com/106795866405632/picture?type=large", user.getPicture());
        assertEquals(2, user.getFriends().size());
        assertEquals(109370069480913L, user.getFriends().get(0).getId());
        assertEquals(115864548829752L, user.getFriends().get(1).getId());
    }

    @Test
    public void isFriend_shouldBeFriend() {
        ModelUser user = getTestUser();

        assertTrue(user.isFriend(2));
    }

    @Test
    public void isFriend_shouldNotBeFriendWhenNotOnFriendList() {
        assertFalse(getTestUser().isFriend(5));
    }

    @Test
    public void isFriend_shouldNotBeFriendWhenWaitingAcceptance() {
        assertFalse(getTestUser().isFriend(4));
    }

    @Test
    public void isFriend_shouldNotBeFriendWhenInvitedButNotAccepted() {
        assertFalse(getTestUser().isFriend(3));
    }

    private ModelUser getTestUser() {
        List<ModelFriend> friends = new ArrayList<>(2);
        friends.add(new ModelFriend(2, "", "", ModelFriend.STATUS_ACCEPTED));
        friends.add(new ModelFriend(3, "", "", ModelFriend.STATUS_INVITED));
        friends.add(new ModelFriend(4, "", "", ModelFriend.STATUS_WAITING));
        return new ModelUser(1, "", "", "", friends);
    }
}