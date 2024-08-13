package com.voxelations.homes.platform.paper.feature;

import com.voxelations.common.config.types.ConfigurableMessage;
import com.voxelations.common.platform.paper.config.types.ConfigurableItemStack;
import com.voxelations.common.platform.paper.config.types.ConfigurableMenu;
import com.voxelations.common.registrar.Container;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@Container
@ConfigSerializable
@Data
public class ConfigFeature {

    private final Messages messages = new Messages();

    private final ConfigurableMenu menu = ConfigurableMenu.builder()
            .title("Homes")
            .rows(6)
            .type(InventoryType.CHEST)
            .items(Map.of())
            .paged(
                    ConfigurableMenu.Paged.builder()
                            .elementsPerPage(45)
                            .firstSlot(0)
                            .previousPage(
                                    new ConfigurableMenu.Drawable(
                                            List.of(48),
                                            ConfigurableItemStack.builder()
                                                    .type(Material.ARROW)
                                                    .displayName("Previous Page")
                                                    .build()
                                    )
                            )
                            .nextPage(
                                    new ConfigurableMenu.Drawable(
                                            List.of(50),
                                            ConfigurableItemStack.builder()
                                                    .type(Material.ARROW)
                                                    .displayName("Next Page")
                                                    .build()
                                    )
                            )
                            .template(
                                    ConfigurableItemStack.builder()
                                            .type(Material.GRASS_BLOCK)
                                            .displayName("<gold><home>")
                                            .lore(List.of(
                                                    "<gold>World: <world>",
                                                    "<gold>Coords: <x> <y> <z>",
                                                    "",
                                                    "<white>Click to teleport."
                                            ))
                                            .build()
                            )
                            .build()
            )
            .build();

    @ConfigSerializable
    @Data
    public static class Messages {
        private final ConfigurableMessage teleportedHome = ConfigurableMessage.builder()
                .message("<green>[!] Teleported home.")
                .build();
        private final ConfigurableMessage setHome = ConfigurableMessage.builder()
                .message("<green>[!] Home set to current location.")
                .build();
        private final ConfigurableMessage maxHomes = ConfigurableMessage.builder()
                .message("<red>[!] You cannot create more homes.")
                .build();
        private final ConfigurableMessage duplicateHome = ConfigurableMessage.builder()
                .message("<red>[!] Home already exists.")
                .build();
        private final ConfigurableMessage deleteHome = ConfigurableMessage.builder()
                .message("<gold>[!] Home deleted.")
                .build();
        private final ConfigurableMessage invalidHome = ConfigurableMessage.builder()
                .message("<red>[!] Invalid home.")
                .build();
    }
}
