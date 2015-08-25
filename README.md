# jSkype
jSkype creation started when skype4web was released, however at the time I was making a private Skype client in Java, not an API. Samczsun, better known as super salter 9000 was creating an extremely limited api at the time of my client creation and still is today. In order to spare people from his limited api, I'm releasing jSkype.

#Downloads, Javadocs, etc
JavaDocs: http://gghost.xyz/JavaDocs/jSkype

Maven: http://maven.gghost.xyz OR http://ghosted.me/maven

Repository:
```
 <repository>
  <id>xyz.gghost</id>
  <url>http://gghost.xyz/maven/</url>
</repository>
```
Dependency:
```
<dependency>
  <groupId>xyz.gghost</groupId>
  <artifactId>jskype</artifactId>
  <version>2.4-BETA</version>
  <scope>compile</scope>
</dependency>
```
#Features
- Get contact requests
- Get recent groups
- Get contacts
- Add and remove users from groups
- Integrated command handler for bots
- User join/leave/chat/typing events
- Send messages
- Format messages
- Accept contact requests
- Send contact requests
- User information

#Creating a skype instance
Before creating a Skype instance, you'll need to confirm whether or not you login with an email/pass or user/pass. If you login with a username and password, you can create a new instance of SkypeAPI with the arguments (username, password), otherwise people with email logins should pass (email, username, password)

Example user/pass: 
```java
SkypeAPI skype = new SkypeAPI("NotGhostBot", "Password");
```
Example email/pass: 
```java
SkypeAPI skype = new SkypeAPI("ghost@ghosted.me", "NotGhostBot", "Password");
```

#Where are all the methods?
jSkype is split up into two main classes; LocalAccount and SkypeAPI. SkypeAPI is mainly useless, however it contains the LocalAccount instance, which is where the recent groups, contacts, send messages, etc is hold. Checking the JavaDocs would help out, but it's safe to assume most of what you'll want is in SkypeAPI#getUser (LocalAccount)

API Related (event listeners, command handlers, LocalAccount instance, etc): SkypeAPI

User related (contact requests, active groups, contacts, login, etc): LocalAccount
#Sending chat messages
Sending a message to all contacts example:
```java
for (User user : skype.getSkype().getContacts()){
  user.sendMessage(skype, "Hi");
}
```
Sending a message to all recent groups example:
```java
for (Conversation group : skype.getSkype().getRecent()){
  group.sendMessage(skype, "Hi");
}
```
Editing a message:
```java
Message message = group.sendMessage(skype, "Hi");
message.editMessage("");
```
## Formatting messages
The "Chat" class is a utilities class for formatting. To format "hi" in bold, you can do "Chat.bold("hi")", which will return "hi" with the html Skype tags. If you wanted "hi" to be in bold and blink, you can do "Chat.bold(Chat.blink("hi"))".

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
        System.out.println("Loaded Skype..."); //Tell the user that skype has fully initialized - getting contacts, recent, etc can take a few seconds

        skype.getEventManager().registerListener(new ExampleListener(skype)); //Register listener

        while (inRunning){} //This program is multithreaded and the main thread doesn't get used, so you'll want an (infinite) delay to keep the program open.
        skype.stop(); //Close Skype related threads - shutting it down

    }
}
```
#Example command handler usage:
To register a command:
```java
skype.getCommandManager().addCommand(new CommandTest(skype, skype.getUser()));
```
A very poor example of a command:
```java
public class CommandTest extends Command{

    SkypeAPI api;
    LocalAccount acc;
    public CommandTest(SkypeAPI api, LocalAccount acc){
        super("-info", "-i");                //Command prefix + command = comandname 
        this.api = api;
        this.acc = acc;
    }
  
    @Override 
    public void called(Message msg, Conversation group, String args){ 
    //msg = orignal message
    //group = group the message was received from 
    //args = everything after the comandname (prefix + command)
        if (args.equals("") || args.equals("info")) {
            StringBuilder builder = new StringBuilder();
            builder.append(Chat.bold("CommandTest info:") + "\n");
            builder.append("CommandTest: " + this.getName() + "\n");
            builder.append("Args passed: " + args + "\n");
            builder.append("Message: " + msg.getMessage() + "\n");
            builder.append(Chat.bold("Chat info:") + "\n");
            builder.append("Group ID: " + group.getChatId() + "\n");
            builder.append("Connected clients: " + group.getConnectedClients().size() + "\n");
            group.sendMessage(api,  builder.toString());
        }
    }

}
```

#Dependencies
- commons-lang 3
- org.json (repo contains fork)
- jsoup (repo contains sams authenticator)
- lombok
