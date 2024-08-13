package com.voxelations.homes.platform.paper.feature;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.voxelations.common.config.ConfigService;
import com.voxelations.common.data.DataService;
import com.voxelations.common.event.EventBus;
import com.voxelations.common.platform.paper.config.types.ConfigurableMenu;
import com.voxelations.common.platform.paper.invs.MenuFactory;
import com.voxelations.common.platform.paper.util.BukkitAdapters;
import com.voxelations.common.platform.paper.util.Schedulers;
import com.voxelations.common.registrar.CloudCommand;
import com.voxelations.common.registrar.Container;
import com.voxelations.common.registrar.Registrable;
import com.voxelations.common.util.FinePosition;
import com.voxelations.homes.data.Home;
import com.voxelations.homes.data.HomeData;
import com.voxelations.homes.event.PreDeleteHomeEvent;
import com.voxelations.homes.event.PreSetHomeEvent;
import com.voxelations.homes.event.PreTeleportHomeEvent;
import com.voxelations.homes.platform.paper.data.HomeImpl;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@Container
@Singleton
public class CommandFeature implements Registrable, CloudCommand {

    private final ConfigService configService;
    private final EventBus eventBus;
    private final DataService<UUID, HomeData> homeService;
    private final MenuFactory menuFactory;
    private final Schedulers schedulers;
    private final LegacyPaperCommandManager<CommandSender> commandManager;

    @Inject
    public CommandFeature(ConfigService configService, EventBus eventBus, DataService<UUID, HomeData> homeService, MenuFactory menuFactory, Schedulers schedulers, LegacyPaperCommandManager<CommandSender> commandManager) {
        this.configService = configService;
        this.eventBus = eventBus;
        this.homeService = homeService;
        this.menuFactory = menuFactory;
        this.schedulers = schedulers;
        this.commandManager = commandManager;
    }

    @Override
    public void enable() {
        // TODO: figure out why we can't catch regular ArgumentParseExceptions
        commandManager.exceptionController()
                .registerHandler(Throwable.class, context -> configService.get(ConfigFeature.class).getMessages().getInvalidHome().send(context.context().sender()));
    }

    @Suggestions("home")
    public Stream<String> suggestHomes(CommandContext<CommandSender> context, String input) {
        return context.sender().get(Identity.UUID)
                .map(homeService::resolve)
                .stream()
                .map(HomeData::getHomes)
                .map(Map::keySet)
                .flatMap(Collection::stream);
    }

    @Parser(suggestions = "home")
    public Home parseHome(CommandContext<CommandSender> context, CommandInput commandInput) {
        String input = commandInput.peekString();

        Home resolved = context.sender().get(Identity.UUID)
                .map(homeService::resolve)
                .stream()
                .map(HomeData::getHomes)
                .map(it -> it.get(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(input));

        commandInput.readString();

        return resolved;
    }

    @Suggestions("home-data")
    public Stream<String> suggestHomeData(CommandContext<CommandSender> context, String input) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName);
    }

    @Parser(suggestions = "home-data")
    public HomeData parseHomeData(CommandContext<CommandSender> context, CommandInput commandInput) {
        String input = commandInput.peekString();
        HomeData resolved = Objects.requireNonNull(homeService.resolve(Bukkit.getOfflinePlayer(input).getUniqueId()));
        commandInput.readString();

        return resolved;
    }

    @Command("homes")
    @Permission("voxelations.homes.use")
    public void homes(Player sender) {
        homesMenu(sender, sender.getUniqueId(), this::home);
    }

    @Command("home <home>")
    @Permission("voxelations.homes.use")
    public void home(Player sender, Home home) {
        HomeData homeData = Objects.requireNonNull(homeService.resolve(sender.getUniqueId()));

        // API
        PreTeleportHomeEvent event = eventBus.post(new PreTeleportHomeEvent(homeData, home));
        if (event.isCancelled()) return;

        // Teleport them
        sender.teleportAsync(BukkitAdapters.FINE_POSITION.toBukkit(home.getPosition())).thenAccept(result -> {
            if (!result) return;
            configService.get(ConfigFeature.class).getMessages().getTeleportedHome().send(sender);
        });
    }

    @Command("sethome <home>")
    @Permission("voxelations.homes.use")
    public void setHome(Player sender, String home) {
        HomeData homeData = Objects.requireNonNull(homeService.resolve(sender.getUniqueId()));

        // Make sure they can actually set this home
        if (homeData.getHomes().get(home) != null) {
            configService.get(ConfigFeature.class).getMessages().getDuplicateHome().send(sender);
            return;
        }

        int maxHomes = sender.getEffectivePermissions().stream()
                .filter(perm -> perm.getPermission().startsWith("voxelations.homes.max."))
                .map(perm -> perm.getPermission().substring("voxelations.homes.max.".length()))
                .map(Integer::parseInt)
                .reduce(0, Integer::max);
        if (homeData.getHomes().size() >= maxHomes) {
            configService.get(ConfigFeature.class).getMessages().getMaxHomes().send(sender);
            return;
        }

        // API
        Home setHome = new HomeImpl(home, BukkitAdapters.FINE_POSITION.toComplex(sender.getLocation()));
        PreSetHomeEvent event = eventBus.post(new PreSetHomeEvent(homeData, setHome));
        if (event.isCancelled()) return;

        // Set the home
        homeData.getHomes().put(home, setHome);
        homeData.markDirty();
        configService.get(ConfigFeature.class).getMessages().getSetHome().send(sender);
    }

    @Command("delhome <home>")
    @Permission("voxelations.homes.use")
    public void deleteHome(Player sender, Home home) {
        HomeData homeData = Objects.requireNonNull(homeService.resolve(sender.getUniqueId()));

        // API
        PreDeleteHomeEvent event = eventBus.post(new PreDeleteHomeEvent(homeData, home));
        if (event.isCancelled()) return;

        // Delete the home
        homeData.getHomes().remove(home.getName());
        homeData.markDirty();
        configService.get(ConfigFeature.class).getMessages().getDeleteHome().send(sender);
    }

    @Command("homeadmin <player> sethome <home>")
    @Permission("voxelations.homes.admin")
    public void homeAdminSetHome(Player sender, HomeData player, String home) {
        // Make sure there isn't already a home with this name
        if (player.getHomes().get(home) != null) {
            configService.get(ConfigFeature.class).getMessages().getDuplicateHome().send(sender);
            return;
        }

        // Set the home
        player.getHomes().put(home, new HomeImpl(home, BukkitAdapters.FINE_POSITION.toComplex(sender.getLocation())));
        player.markDirty();
        homeService.getRepository().save(player);
        configService.get(ConfigFeature.class).getMessages().getSetHome().send(sender);
    }

    @Command("homeadmin <player> home <home>")
    @Permission("voxelations.homes.admin")
    public void homeAdminHome(Player sender, HomeData player, String home) {
        // Find the home
        Home resolved = player.getHomes().get(home);
        if (resolved == null) {
            configService.get(ConfigFeature.class).getMessages().getInvalidHome().send(sender);
            return;
        }

        // and TP them to it
        sender.teleportAsync(BukkitAdapters.FINE_POSITION.toBukkit(resolved.getPosition())).thenAccept(result -> {
            if (!result) return;
            configService.get(ConfigFeature.class).getMessages().getTeleportedHome().send(sender);
        });
    }

    @Command("homeadmin <player> delhome <home>")
    @Permission("voxelations.homes.admin")
    public void homeAdminDeleteHome(Player sender, HomeData player, String home) {
        // Find the home
        Home resolved = player.getHomes().get(home);
        if (resolved == null) {
            configService.get(ConfigFeature.class).getMessages().getInvalidHome().send(sender);
            return;
        }

        // and delete it
        player.getHomes().remove(home);
        player.markDirty();
        homeService.getRepository().save(player);
        configService.get(ConfigFeature.class).getMessages().getDeleteHome().send(sender);
    }

    @Command("homeadmin <player> homes")
    @Permission("voxelations.homes.admin")
    public void homeAdminHomes(Player sender, HomeData player) {
        homesMenu(sender, player.getId(), (viewer, home) -> homeAdminHome(viewer, player, home.getName()));
    }

    /**
     * Opens the homes menu.
     *
     * @param viewer the viewer
     * @param playerContext the player data context
     * @param onClick the click handler
     */
    private void homesMenu(Player viewer, UUID playerContext, BiConsumer<Player, Home> onClick) {
        ConfigurableMenu configurable = configService.get(ConfigFeature.class).getMenu();
        SmartInventory inv = menuFactory.builder().configurableMenu(configurable)
                .pageElements(
                        Objects.requireNonNull(homeService.resolve(playerContext)).getHomes().entrySet().stream()
                                .map(home ->
                                        {
                                            FinePosition pos = home.getValue().getPosition();
                                            return ClickableItem.of(
                                                    configurable.paged().template().toItemStack(
                                                            Placeholder.unparsed("home", home.getKey()),
                                                            Placeholder.unparsed("world", pos.world()),
                                                            Formatter.number("x", pos.x()),
                                                            Formatter.number("y", pos.y()),
                                                            Formatter.number("z", pos.z())
                                                    ),
                                                    e -> {
                                                        onClick.accept(viewer, home.getValue());
                                                        viewer.closeInventory();
                                                    }
                                            );
                                        }
                                )
                                .toArray(ClickableItem[]::new)
                )
                .build()
                .build();

        schedulers.getSync().runTask(() -> inv.open(viewer));
    }
}
