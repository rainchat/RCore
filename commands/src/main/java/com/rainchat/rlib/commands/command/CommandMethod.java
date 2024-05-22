package com.rainchat.rlib.commands.command;

import com.rainchat.rlib.commands.CommandException;
import com.rainchat.rlib.commands.command.CommandHandler;
import com.rainchat.rlib.commands.messages.MessageSender;
import org.bukkit.command.CommandSender;

public interface CommandMethod {

    /**
     * @return how the command should be used in Command Line Syntax format.
     */
    String getUsage();

    /**
     * @return the argument types, in the order that they are entered.
     */
    Class<?>[] getArgumentTypes();

    /**
     * Execute the CommandMethod.
     *
     * @param sender  - The CommandSender who executed the command.
     * @param rawArgs - The arguments being passed into the method, in string form.
     * @throws CommandException
     */
    void invoke(CommandHandler handler, CommandSender sender, String[] rawArgs, MessageSender msgSender) throws CommandException;

}
