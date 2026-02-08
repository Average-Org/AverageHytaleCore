package util;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public final class CommandUtils {
    private CommandUtils() {
    }

    /**
     * Adds a list of subcommands to a command collection.
     *
     * @param collection  The collection to add the subcommands to
     * @param subcommands The subcommands to add
     */
    public static void addSubcommands(AbstractCommandCollection collection, AbstractCommand... subcommands) {
        for (AbstractCommand subcommand : subcommands) {
            collection.addSubCommand(subcommand);
        }
    }
}
