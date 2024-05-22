package com.rainchat.rlib.commands.parse;

import com.rainchat.rlib.commands.CommandException;

/**
 * Converts a string into an object.
 *
 * @author Christopher Bishop
 */
@FunctionalInterface
public interface Parser {
    Object parseObject(String args) throws CommandException;

}
