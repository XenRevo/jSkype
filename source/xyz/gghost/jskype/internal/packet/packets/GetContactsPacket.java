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
        BasePacket packet = new BasePacket(api);
        packet.setUrl("https://contacts.skype.com/contacts/v1/users/" + usr.getUsername().toLowerCase() + "/contacts?filter=contacts");
        packet.setType(RequestType.OPTIONS);
        packet.getData();
        packet.setType(RequestType.GET);
        String a = packet.makeRequest(usr);
        if (a == null) {
            System.out.println("Failed to request Skype for your contacts.");
            contacts.add(api.getUser());
            return contacts;
        }

        try {
            //TODO:
            /**
             * Exception in thread "Thread-3" org.json.JSONException: A JSONObject text must begin with '{' at 1 [character 2 line 1]
             at org.json.JSONTokener.syntaxError(JSONTokener.java:433)
             at org.json.JSONObject.<init>(JSONObject.java:197)
             at org.json.JSONObject.<init>(JSONObject.java:324)
             at xyz.gghost.jskype.internal.packet.packets.GetContactsPacket.getContacts(GetContactsPacket.java:39)
             at xyz.gghost.jskype.internal.threading.ContactUpdater.run(ContactUpdater.java:27)
             */
            JSONObject jsonObject = new JSONObject(a);
            JSONArray lineItems = jsonObject.getJSONArray("contacts");
            for (Object o : lineItems) {
                JSONObject jsonLineItem = (JSONObject) o;
                GetProfilePacket profilePacket = new GetProfilePacket(api, usr);
                User user = profilePacket.getUser(jsonLineItem.getString("id"));
                contacts.add(user);
            }
        } catch (NullPointerException e) {
            throw new FailedToGetContactsException();
        } catch (JSONException e) {
            throw new BadResponseException();
        }
        return contacts;
    }

}

