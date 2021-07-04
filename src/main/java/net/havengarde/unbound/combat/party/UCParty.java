package net.havengarde.unbound.combat.party;

import com.google.gson.Gson;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class UCParty {
    public static final UCParty.UCPartyPersistentDataType PERSISTENT_DATA_TYPE = new UCParty.UCPartyPersistentDataType();

    public static final class UCPartyPersistentDataType implements PersistentDataType<String, UCParty> {
        private final Gson gson = new Gson();

        @Override
        public @NotNull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @NotNull Class<UCParty> getComplexType() {
            return UCParty.class;
        }

        @Override
        public @NotNull String toPrimitive(@NotNull UCParty ucItemData, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return gson.toJson(ucItemData);
        }

        @Override
        public @NotNull UCParty fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return gson.fromJson(s, UCParty.class);
        }
    }
}
