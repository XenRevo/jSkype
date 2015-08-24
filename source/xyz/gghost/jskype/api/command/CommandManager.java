package xyz.gghost.jskype.api.command;

import lombok.Getter;
import xyz.gghost.jskype.var.Conversation;
import xyz.gghost.jskype.var.Message;

import java.util.ArrayList;

public class CommandManager {
    @Getter

    private ArrayList<Command> commands = new ArrayList<Command>();

    public void addCommand(Command cmd) {
        commands.add(cmd);
    }

    public void removeCmd(Command cmd) {
        commands.remove(cmd);
    }

    public void runCommand(Message message, Conversation group) {
        for (Command command : commands) {
            for (String cmdName : command.getNames()) {
                if (message.getMessage().startsWith(cmdName)){
                    if (message.getMessage().contains(cmdName + " ")){
                        command.called(message, group, message.getMessage().replaceFirst(cmdName + " ", ""));
                    }else{
                        command.called(message, group, message.getMessage().replaceFirst(cmdName, ""));
                    }
                    break;
                }
            }
        }
    }

}
