import com.hypixel.hytale.server.core.universe.Universe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class UniverseUtilsTests {
    @Test
    public void canGetPlayersUuidMapFromUniverse() throws NoSuchFieldException {
        var playerUuidMapField = Universe.class.getDeclaredField("playersByUuid");
        playerUuidMapField.setAccessible(true);

        Assertions.assertEquals(Map.class, playerUuidMapField.getType());
    }
}
