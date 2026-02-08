package util;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.Universe;

public final class PlayerUtils {
    private PlayerUtils() {}

    /**
     * Sends a message to all players in the server, with color code support.
     *
     * @param message The message to send.
     */
    public void sendToAll(String message) {
        Universe.get().getPlayers().forEach(player
                -> player.sendMessage(ColorUtils.parseColorCodes(message)));
    }

    /**
     * Sends a message to all players in the server. This does not apply color codes.
     *
     * @param message A Hytale message object to send to all players
     */
    public void sendToAll(Message message) {
        Universe.get().getPlayers().forEach(player
                -> player.sendMessage(message));
    }
}
