package xyz.gghost.jskype.api.command;

import lombok.Getter;
import lombok.Setter;
import xyz.gghost.jskype.api.command.Command;
import xyz.gghost.jskype.var.Group;
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

    public void runCommand(Message message, Group group) {
        for (Command command : commands) {
            if (message.getMessage().startsWith(command.getName())){
                if (message.getMessage().contains(command.getName() + " ")){
                    command.called(message, group, message.getMessage().replaceFirst(command.getName() + " ", ""));
                }else{
                    command.called(message, group, message.getMessage().replaceFirst(command.getName(), ""));
                }
            }
        }
    }

}
