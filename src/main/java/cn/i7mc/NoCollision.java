package cn.i7mc;
import org.bukkit.plugin.java.JavaPlugin;
public class NoCollision extends JavaPlugin {
    private NoCollisionManager noCollisionManager;
    private DamageIndicatorBlocker damageIndicatorBlocker;
    @Override
    public void onEnable() {
        noCollisionManager = new NoCollisionManager(this);
        noCollisionManager.enableGlobalNoCollision();
        damageIndicatorBlocker = new DamageIndicatorBlocker(this);
        damageIndicatorBlocker.register();
        getLogger().info("NoCollision插件已启用");
    }
    @Override
    public void onDisable() {
        if (damageIndicatorBlocker != null) {
            damageIndicatorBlocker.unregister();
        }
        if (noCollisionManager != null) {
            noCollisionManager.disableGlobalNoCollision();
        }
        getLogger().info("NoCollision插件已关闭!");
    }
} 
