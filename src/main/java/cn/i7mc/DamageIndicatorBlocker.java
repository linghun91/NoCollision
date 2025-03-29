package cn.i7mc;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedParticle;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
public class DamageIndicatorBlocker {
    private final Plugin plugin;
    private PacketAdapter packetAdapter;
    public DamageIndicatorBlocker(Plugin plugin) {
        this.plugin = plugin;
    }
    public void register() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();    
        packetAdapter = new PacketAdapter(plugin, PacketType.Play.Server.WORLD_PARTICLES) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();              
                try {
                    WrappedParticle<?> particleType = packet.getNewParticles().read(0);
                    if (particleType != null && particleType.getParticle() == Particle.DAMAGE_INDICATOR) {
                        int count = packet.getIntegers().read(0);
                        double x = packet.getDoubles().read(0);
                        double y = packet.getDoubles().read(1);
                        double z = packet.getDoubles().read(2);
                        if (count > 0) {
                            event.setCancelled(true);
                        }
                    }
                } catch (Exception e) {
                }
            }
        };    
        protocolManager.addPacketListener(packetAdapter);
    }
    public void unregister() {
        if (packetAdapter != null) {
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            protocolManager.removePacketListener(packetAdapter);
        }
    }
    private boolean isEntityDamageParticle(float count, float offsetX, float offsetY, float offsetZ, float speed) {
        boolean validCount = count >= 1;
        boolean validOffsetY = offsetY > 0;
        boolean validOffset = offsetX < 0.5f && offsetZ < 0.5f;
        boolean validSpeed = speed < 0.5f;
        return validCount && validOffsetY && validOffset && validSpeed;
    }
} 
