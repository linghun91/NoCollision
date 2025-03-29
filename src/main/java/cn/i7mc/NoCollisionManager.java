package cn.i7mc;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import me.devtec.shared.Ref;
import me.devtec.shared.components.Component;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.utils.TeamUtils;
public class NoCollisionManager implements Listener {
    private final JavaPlugin plugin;
    private final String teamName;
    private Team team;
    public NoCollisionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.teamName = "NoCollision_" + plugin.getName();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public void enableGlobalNoCollision() {
        createTeamWithBukkitAPI();
        createNoCollisionTeam();
        for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayerWithBukkitAPI(player);
            addPlayerToNoCollisionTeam(player);
            Team playerTeam = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
            if (playerTeam != null) {
            } else {
            }
        }
    }
    private void createTeamWithBukkitAPI() {
        try {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team existingTeam = scoreboard.getTeam(teamName);
            if (existingTeam != null) {
                existingTeam.unregister();
            }
            team = scoreboard.registerNewTeam(teamName);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setAllowFriendlyFire(true);
            team.setCanSeeFriendlyInvisibles(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addPlayerWithBukkitAPI(Player player) {
        try {
            if (team != null && !team.hasEntry(player.getName())) {
                team.addEntry(player.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createNoCollisionTeam() {
        try {
            Class<?> teamPacketClass = Ref.nms("network.protocol.game", "ClientboundSetPlayerTeamPacket");
            Class<?> parametersClass = Ref.nms("network.protocol.game", "ClientboundSetPlayerTeamPacket$Parameters");
            if (teamPacketClass == null || parametersClass == null) {
                return;
            }
            Object packet = Ref.newUnsafeInstance(teamPacketClass);
            Object parameters = Ref.newUnsafeInstance(parametersClass);
            Ref.set(parameters, TeamUtils.teamDisplayName, BukkitLoader.getNmsProvider().chatBase("{\"text\":\"NoCollision\"}"));
            Ref.set(parameters, TeamUtils.nametagVisibility, "ALWAYS");
            Ref.set(parameters, TeamUtils.collisionRule, "NEVER"); 
            Ref.set(parameters, TeamUtils.playerPrefix, BukkitLoader.getNmsProvider().toIChatBaseComponent(Component.EMPTY_COMPONENT));
            Ref.set(parameters, TeamUtils.playerSuffix, BukkitLoader.getNmsProvider().toIChatBaseComponent(Component.EMPTY_COMPONENT));
            Ref.set(parameters, TeamUtils.color, TeamUtils.white);
            Ref.set(parameters, TeamUtils.options, 0);
            Ref.set(packet, TeamUtils.name, teamName);
            Ref.set(packet, TeamUtils.teamMethod, 0);
            Ref.set(packet, TeamUtils.players, Collections.emptyList());
            Ref.set(packet, TeamUtils.parameters, Optional.of(parameters));
            BukkitLoader.getPacketHandler().send(BukkitLoader.getOnlinePlayers(), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addPlayerToNoCollisionTeam(Player player) {
        try {
            List<String> players = Collections.singletonList(player.getName());
            Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
            Ref.set(packet, TeamUtils.name, teamName);
            Ref.set(packet, TeamUtils.teamMethod, 3); 
            Ref.set(packet, TeamUtils.players, players);
            BukkitLoader.getPacketHandler().send(BukkitLoader.getOnlinePlayers(), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addEntityToNoCollisionTeam(Entity entity) {
        try {
            if (team != null) {
                team.addEntry(entity.getUniqueId().toString());
            }
            List<String> entities = Collections.singletonList(entity.getUniqueId().toString());
            Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
            Ref.set(packet, TeamUtils.name, teamName);
            Ref.set(packet, TeamUtils.teamMethod, 3);
            Ref.set(packet, TeamUtils.players, entities);
            BukkitLoader.getPacketHandler().send(BukkitLoader.getOnlinePlayers(), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void disableGlobalNoCollision() {
        try {
            if (team != null) {
                team.unregister();
            }
            Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
            Ref.set(packet, TeamUtils.name, teamName);
            Ref.set(packet, TeamUtils.teamMethod, 1);
            BukkitLoader.getPacketHandler().send(BukkitLoader.getOnlinePlayers(), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Player player = event.getPlayer();
            addPlayerWithBukkitAPI(player);
            addPlayerToNoCollisionTeam(player);
            Team playerTeam = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
            if (playerTeam != null) {
            } else {
            }
        }, 10L);
    }
} 