# jSkype
jSkype creation started when skype4web was released, however at the time I was making a private Skype client in Java, not an API. Samczsun, better known as super salter 9000 was creating an extremely limited api at the time of my client creation and still is today. In order to spare people from his limited api, I'm releasing jSkype
#Links
JavaDocs: http://gghost.xyz/JavaDocs/jSkype

Maven: http://maven.gghost.xyz OR http://ghosted.me/maven

#Creating a skype instance
Before creating a Skype instance, you'll need to confirm whether or not you login with an email/pass or user/pass. If you login with a username and password, you can create a new instance of SkypeAPI with the arguments (username, password), otherwise people with email logins should pass (email, username, password)

Example user/pass: 
```java
SkypeAPI skype = new SkypeAPI("NotGhostBot", "{password here}");
```
Example email/pass: 
```java
SkypeAPI skype = new SkypeAPI("ghost@ghosted.me", "NotGhostBot", "{password here}");
```
#Sending chat messages

for (User user : skype.getUser().getContacts()){
  user.sendMessage(skype, Chat.blink(Chat.bold(":/")));
}
for (Group group : skype.getUser().getGroups()){
  group.sendMessage(skype, Chat.blink(Chat.bold(":/")));
}

## Formatting messages

#Where are all the methods?
jSkype is split up into two main classes; LocalAccount and SkypeAPI. SkypeAPI is mainly useless, however it contains the LocalAccount instance, which is where the recent groups, contacts, send messages, etc is hold. Checking the JavaDocs would help out, but it's safe to assume most of what you'll want is in SkypeAPI.getUser() (LocalAccount)

API Related (event listeners, command handlers, LocalAccount instance, etc): SkypeAPI

User related (contact requests, active groups, contacts, login, etc): LocalAccount
#Example event handler usage:
In order to listen for an event, create a class that implements EventListener, and register it by calling "api.getEventManager().registerListener(new YourListener(skype));" All event's can be found the "xyz.gghost.jskype.api" package

```java
public class ExampleListener implements EventListener {
    SkypeAPI api;
    public ExampleListener(SkypeAPI api){
        this.api = api;
    }
    public void join(UserJoinEvent e){ //If a method has the event as the only argument, it will get invoked once the events trigger(join/chat/leave/etc) has been called. 
        System.out.println(e.getUser().getDisplayName() + " has joined " + e.getGroup().getChatId());
    }
}

public class Test {
    static boolean inRunning = true;
    public static void main(String[] args) {
        SkypeAPI skype = new SkypeAPI("NotGhostBot", "{password here}"); //login
        System.out.println("Loaded skype..."); //Tell the user that skype has fully initialized - getting contacts, recent, etc can take a few seconds

        skype.getEventManager().registerListener(new ExampleListener(skype)); //Register listener

        while (inRunning){} //This program is multithreaded and the main thread doesn't get used, so you'll want an (infinite) delay to keep the program open.
        skype.stop(); //Close skype related threads - shutting it down

    }
}
```
#Example command handler usage:
#Dependancies
- commons-lang 3
- org.json (repo contains fork)
- jsoup (repo contains sams authenticator)
- lombok
