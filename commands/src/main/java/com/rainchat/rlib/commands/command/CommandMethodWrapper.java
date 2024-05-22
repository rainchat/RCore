package com.rainchat.rlib.commands.command;

import com.rainchat.rlib.commands.annotaion.Option;
import com.rainchat.rlib.commands.messages.MessageSender;
import com.rainchat.rlib.commands.parse.TypeParser;
import com.rainchat.rlib.commands.CommandException;
import com.rainchat.rlib.commands.annotaion.Command;
import com.rainchat.rlib.commands.messages.CommandResult;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandMethodWrapper implements CommandMethod {

    private Object commandable;
    private Method method;

    private String[] permissions;
    private String usage;

    private TypeParser parser;

    private int optionalOffset = -1;
    private Object[] defaults = {};

    private CommandMethodWrapper() {
    }

    private int minSize() {
        return (optionalOffset < 0) ? method.getParameterCount() - 1 : optionalOffset;
    }

    @Override
    public String getUsage() {
        return null;
    }

    public Method getMethod() {
        return method;
    }

    public Command getCommandAnnotation() throws CommandException {
        Command cmd = method.getAnnotation(Command.class);
        if (cmd == null) {
            throw new CommandException("Missing @Command annotation.");
        }
        return cmd;
    }

    @Override
    public Class<?>[] getArgumentTypes() {
        List<Class<?>> types = new ArrayList<>(Arrays.asList(method.getParameterTypes()));

        types.remove(0);

        return types.toArray(new Class<?>[0]);
    }

    public void invoke(CommandHandler handler, CommandSender sender, String[] rawArgs, MessageSender msgSender) throws CommandException {
        if (!method.getParameterTypes()[0].isInstance(sender)) {
            msgSender.error(sender, "Only " + method.getParameterTypes()[0].getSimpleName() + " can use this command.");
            return;
        }

        for (String perm : permissions) {
            if (!sender.hasPermission(perm)) {
                msgSender.error(sender, "You do not have permission to use this command.");
                return;
            }
        }

        if (rawArgs.length < minSize()) {
            msgSender.error(sender, usage);
            return;
        }

        Object[] args = new Object[method.getParameterCount()];
        args[0] = sender;

        try {
            for (int i = 1; i < args.length; i++) {
                if (i < rawArgs.length + 1) {
                    args[i] = parser.parseObject(method.getParameterTypes()[i], rawArgs[i - 1]);
                } else {
                    args[i] = defaults[i - optionalOffset - 1];
                }
            }
        } catch (CommandException | IllegalArgumentException e) {
            msgSender.error(sender, e.getMessage());
            msgSender.error(sender, usage);
            return;
        }

        executeCommandMethod(args, msgSender, sender);
    }

    private void executeCommandMethod(Object[] args, MessageSender msgSender, CommandSender sender) throws CommandException {
        try {
            CommandResult result = (CommandResult) method.invoke(commandable, args);
            if (result != null && result.getMessage() != null) {
                handleMessageResult(result, msgSender, sender);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new CommandException("Problem invoking method: " + e.getMessage(), e);
        }
    }

    private void handleMessageResult(CommandResult result, MessageSender msgSender, CommandSender sender) {
        if (result.usePrefix()) {
            switch (result.getStatus()) {
                case SUCCESS:
                    msgSender.info(sender, result.getMessage());
                    break;
                case FAILED:
                case USAGE:
                    msgSender.error(sender, result.getMessage());
                    msgSender.error(sender, usage);
                    break;
                default:
                    msgSender.send(sender, result.getMessage());
            }
        } else {
            msgSender.send(sender, result.getMessage());
        }
    }

    /**
     * Build the CommandMethod.
     *
     * @param object
     * @param m
     * @return
     * @throws CommandException
     */
    public static CommandMethodWrapper build(Object object, Method m, TypeParser parser) throws CommandException {
        validateMethod(m);
        Command cmd = getCommandAnnotation(m);

        // Build USAGE message and optional parameters.
        ParamParsingResult parsingResult = parseParameters(m, cmd, parser);

        // Build and return CommandMethod.
        return createCommandMethod(object, m, cmd, parsingResult, parser);
    }

    private static void validateMethod(Method m) throws CommandException {
        if (!Modifier.isPublic(m.getModifiers())) {
            throw new CommandException("Method must be public.");
        }
        if (m.getReturnType() != CommandResult.class && !m.getReturnType().equals(Void.TYPE)) {
            throw new CommandException("Must have CommandResult as the return type.");
        }
        if (m.getParameterCount() == 0 || !CommandSender.class.isAssignableFrom(m.getParameterTypes()[0])) {
            throw new CommandException("Invalid method signature.");
        }
    }

    private static Command getCommandAnnotation(Method m) throws CommandException {
        Command cmd = m.getAnnotation(Command.class);
        if (cmd == null) {
            throw new CommandException("Missing @Command annotation.");
        }
        return cmd;
    }

    private static ParamParsingResult parseParameters(Method m, Command cmd, TypeParser parser) throws CommandException {
        String usage = "/" + cmd.structure();
        List<Object> defaults = new ArrayList<>();
        boolean expectedEnd = false;
        int optionalOffset = -1, unknownParamCount = 0;

        for (int i = 1; i < m.getParameters().length; i++) {
            Parameter param = m.getParameters()[i];
            Class<?> paramType = param.getType();
            if (!parser.parserExistsFor(paramType) || expectedEnd) {
                throw new CommandException("Invalid parameter configuration.");
            }
            expectedEnd = paramType.isArray();

            Option option = param.getAnnotation(Option.class);
            // Simplify the parameter usage building process.
            String paramName = (i - 1 < cmd.params().length) ? cmd.params()[i - 1] : "arg" + (unknownParamCount++);
            String paramUsage = option == null ? "<" + paramName + ">" : "[" + paramName + "]";
            usage += " " + paramUsage;
            if (option != null) {
                defaults.add(option.value().length == 0 ? null : parser.parseObject(paramType, option.value()[0]));
                optionalOffset = optionalOffset == -1 ? i - 1 : optionalOffset;
            }
        }

        return new ParamParsingResult(usage, defaults.toArray(new Object[0]), optionalOffset, unknownParamCount);
    }

    private static CommandMethodWrapper createCommandMethod(Object commandable, Method m, Command cmd, ParamParsingResult parsingResult, TypeParser parser) {
        CommandMethodWrapper cm = new CommandMethodWrapper();
        cm.commandable = commandable;
        cm.method = m;
        cm.parser = parser;
        cm.permissions = cmd.perms();
        cm.usage = parsingResult.usage;
        if (parsingResult.optionalOffset > -1) {
            cm.optionalOffset = parsingResult.optionalOffset;
            cm.defaults = parsingResult.defaults;
        }
        return cm;
    }

    private static class ParamParsingResult {
        final String usage;
        final Object[] defaults;
        final int optionalOffset;
        final int unknownParamCount;

        ParamParsingResult(String usage, Object[] defaults, int optionalOffset, int unknownParamCount) {
            this.usage = usage;
            this.defaults = defaults;
            this.optionalOffset = optionalOffset;
            this.unknownParamCount = unknownParamCount;
        }
    }

}
