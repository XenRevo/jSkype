package xyz.gghost.jskype.internal.packet.packets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.exception.BadResponseException;
import xyz.gghost.jskype.exception.FailedToGetContactsException;
import xyz.gghost.jskype.internal.packet.BasePacket;
import xyz.gghost.jskype.internal.packet.RequestType;
import xyz.gghost.jskype.var.User;

import java.util.ArrayList;

public class GetContactsPacket {

    private SkypeAPI api;
    private LocalAccount usr;

    public GetContactsPacket(SkypeAPI api, LocalAccount usr) {
        this.api = api;
        this.usr = usr;
    }
    public ArrayList<User> getContacts() throws FailedToGetContactsException, BadResponseException {
        ArrayList<User> contacts = new ArrayList<User>();
        ArrayList<String> usernames = new ArrayList<String>();
        BasePacket packet = new BasePacket(api);
        packet.setUrl("https://contacts.skype.com/contacts/v1/users/" + usr.getUsername().toLowerCase() + "/contacts?filter=contacts");
        packet.setType(RequestType.OPTIONS);
        packet.getData();
        packet.setType(RequestType.GET);
        String a = packet.makeRequest(usr);
        if (a == null) {
            System.out.println("Failed to request Skype for your contacts.");
            contacts.add(usr);
            return contacts;
        }
        try {
            JSONObject jsonObject = new JSONObject(a);
            JSONArray lineItems = jsonObject.getJSONArray("contacts");
            for (Object o : lineItems) {
                JSONObject jsonLineItem = (JSONObject) o;
                usernames.add(jsonLineItem.getString("id"));
            }
            contacts = new GetProfilePacket(api, usr).getUsers(usernames);
            if (contacts == null) {
                return new ArrayList<User>();
            } else {
                ArrayList<User> value = new ArrayList<User>();
                for (User user : contacts) {
                    user.setContact(true);
                    value.add(user);
                }
                return value;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }
}

