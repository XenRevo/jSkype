package xyz.gghost.jskype.api.command;

import lombok.Getter;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.Message;

public class Command {
    @Getter
    private String name;

    /**
     * @param name CommandTest name can be more than one word and must contain your command prefix.
     */
    public Command(String name) {
        this.name = name;
    }

    /**
     * @param msg   The message
     * @param group The group this message was received from
     * @param args  Args will be the rest of the string after your command. ie: if someone did "#bot test argTest" and "#bot test" was the command name, "argTest" would be whats in the args string. If "#bot" was registered, everything after "#bot" would be in the args string. Don't get it? args = message.replaceFirst(commandName, "")
     */
    public void called(Message msg, Group group, String args) {
    }

}
