package cn.i7mc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
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
        createNoCollisionTeam();
        for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayerToNoCollisionTeam(player);
        }
    }
    private void createNoCollisionTeam() {
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
    public void addPlayerToNoCollisionTeam(Player player) {
        try {
            if (team != null && !team.hasEntry(player.getName())) {
                team.addEntry(player.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addEntityToNoCollisionTeam(Entity entity) {
        try {
            if (team != null && !team.hasEntry(entity.getUniqueId().toString())) {
                team.addEntry(entity.getUniqueId().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void disableGlobalNoCollision() {
        try {
            if (team != null) {
                team.unregister();
                team = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Player player = event.getPlayer();
            addPlayerToNoCollisionTeam(player);
        }, 10L);
    }
} 
