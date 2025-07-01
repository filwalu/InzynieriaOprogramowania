package com.essa.util.command;

import com.essa.model.User;

import java.util.logging.Logger;

public class ActivateUserCommand implements UserCommand {
    private static final Logger logger = Logger.getLogger(ActivateUserCommand.class.getName());
    private User user;
    
    public ActivateUserCommand(User user) {
        this.user = user;
    }
    
    @Override
    public void execute() {
        logger.info("COMMAND: Activating user: " + user.getUsername());
        // No actual activation logic here, just logging
    }
    
    @Override
    public String getCommandName() {
        return "ACTIVATE_USER";
    }
}