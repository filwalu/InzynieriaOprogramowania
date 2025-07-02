package com.essa.util.command;

import java.util.logging.Logger;

public class UserCommandInvoker {
    public void executeCommand(UserCommand command) {
        Logger.getLogger(UserCommandInvoker.class.getName()).info("COMMAND: Executing " + command.getCommandName());
        command.execute();
    }
}