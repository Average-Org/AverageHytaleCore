package util;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import exceptions.UniversePlayersNotFoundException;
import exceptions.UniversePlayersUnexpectedTypeException;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

/**
 * Simple utilities for Hytale's Universe class
 */
public final class UniverseUtils {
    private static final Field PLAYERS_FIELD;

    static {
        try {
            PLAYERS_FIELD = Universe.class.getDeclaredField("players");
            PLAYERS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(new UniversePlayersNotFoundException(e));
        }
    }

    private UniverseUtils() {}

    /**
     * Gets a map of all players in the server. UUID -> PlayerRef
     * @return A map of all players in the server
     */
    public static Map<UUID, PlayerRef> getPlayerRefs() {
        Object playerFieldValue;

        try {
            playerFieldValue = PLAYERS_FIELD.get(Universe.get());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (!(playerFieldValue instanceof Map<?, ?>)) {
            throw new UniversePlayersUnexpectedTypeException(new IllegalStateException("Players field is not a Map"));
        }

        @SuppressWarnings("unchecked")
        Map<UUID, PlayerRef> playerRefs = (Map<UUID, PlayerRef>) playerFieldValue;
        return playerRefs;
    }
}

