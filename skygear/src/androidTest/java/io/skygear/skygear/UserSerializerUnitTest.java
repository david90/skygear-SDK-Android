package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UserSerializerUnitTest {
    @Test
    public void testUserSerializationNormalFlow() throws Exception {
        User user = new User("user_id_001", "my-token", "user_001", "user001@skygear.dev");
        user.addRole(new Role("Programmer"));
        user.addRole(new Role("Citizen"));

        JSONObject json = UserSerializer.serialize(user);

        assertEquals("user_id_001", json.getString("_id"));
        assertEquals("my-token", json.getString("access_token"));
        assertEquals("user_001", json.getString("username"));
        assertEquals("user001@skygear.dev", json.getString("email"));

        JSONArray roleArray = json.getJSONArray("roles");

        int roleCount = roleArray.length();
        assertEquals(2, roleCount);

        List<String> roleNameList = new LinkedList<>();
        for (int idx = 0; idx < roleCount; idx++) {
            roleNameList.add(roleArray.getString(idx));
        }

        assertTrue(roleNameList.contains("Programmer"));
        assertTrue(roleNameList.contains("Citizen"));
    }

    @Test
    public void testUserDeserializationNormalFlow() throws Exception {
        JSONObject userObject = new JSONObject(
                "{" +
                "  \"_id\": \"456\"," +
                "  \"access_token\": \"token_456\"," +
                "  \"username\": \"user_456\"," +
                "  \"email\": \"user456@skygear.dev\"," +
                "  \"roles\": [\"Programmer\", \"Citizen\"]" +
                "}"
        );

        User user = UserSerializer.deserialize(userObject);

        assertEquals("456", user.id);
        assertEquals("token_456", user.accessToken);
        assertEquals("user_456", user.username);
        assertEquals("user456@skygear.dev", user.email);

        assertTrue(user.hasRole(new Role("Programmer")));
        assertTrue(user.hasRole(new Role("Citizen")));
    }

    @Test(expected = InvalidParameterException.class)
    public void testUserSerializationNotAllowNoUserId() throws Exception {
        User nullUser = new User(null, "token_456");
        UserSerializer.serialize(nullUser);
    }

    @Test
    public void testUserDeserializationCompatibleWithAnotherUserIdFormat() throws Exception {
        JSONObject userObject = new JSONObject("{\"user_id\": \"456\"}");
        assertEquals("456", UserSerializer.deserialize(userObject).id);
    }

    @Test(expected = JSONException.class)
    public void testUserDeserializationNotAllowNoUserId() throws Exception {
        JSONObject userObject = new JSONObject(
                "{" +
                "  \"access_token\": \"token_456\"," +
                "  \"username\": \"user_456\"," +
                "  \"email\": \"user456@skygear.dev\"" +
                "}"
        );
        UserSerializer.deserialize(userObject);
    }
}
