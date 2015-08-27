package xyz.gghost.jskype.api.command;

import lombok.Getter;
import xyz.gghost.jskype.var.Conversation;
import xyz.gghost.jskype.var.Message;

public abstract class Command {
    @Getter
    private final String[] names;

    /**
     * @param names CommandTest name can be more than one word and must contain your command prefix.
     */
    public Command(String... names) {
        this.names = names;
    }

    /**
     * @param msg   The message
     * @param group The group this message was received from
     * @param args  Args will be the rest of the string after your command. ie: if someone did "#bot test argTest" and "#bot test" was the command name, "argTest" would be whats in the args string. If "#bot" was registered, everything after "#bot" would be in the args string. Don't get it? args = message.replaceFirst(commandName, "")
     */
    public abstract void called(Message msg, Conversation group, String args);

}
