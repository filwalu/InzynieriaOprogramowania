package com.essa.util.command;

import com.essa.model.User;

import java.util.logging.Logger;

public class DeactivateUserCommand implements UserCommand {
    private static final Logger logger = Logger.getLogger(DeactivateUserCommand.class.getName());
    private User user;
    
    public DeactivateUserCommand(User user) {
        this.user = user;
    }
    
    @Override
    public void execute() {
        logger.info("COMMAND: Deactivating user: " + user.getUsername());
        // No logic just logging
    }
    
    @Override
    public String getCommandName() {
        return "DEACTIVATE_USER";
    }
}
