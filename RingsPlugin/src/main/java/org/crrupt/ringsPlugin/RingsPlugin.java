package org.crrupt.ringsPlugin;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;

public class RingsPlugin extends JavaPlugin implements Listener {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final List<String> rings = Arrays.asList("Wither Ring", "Shulker Ring", "Illusioner Ring", "Evoker Ring", "Elder Guardian Ring", "Sniffer Ring", "Piglin Brute Ring", "Enderman Ring", "Ender Dragon Ring", "Breeze Ring");
    private final Random random = new Random();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public ItemStack createRing(String name, Material material, int customModelData) {
        ItemStack ring = new ItemStack(material);
        ItemMeta meta = ring.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.getPersistentDataContainer().set(new NamespacedKey(this, "ring"), PersistentDataType.STRING, name);
        meta.setCustomModelData(customModelData);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        ring.setItemMeta(meta);
        return ring;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NamespacedKey key = new NamespacedKey(this, "received_ring");
        if (!player.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN)) {
            String ringName = rings.get(random.nextInt(rings.size()));
            ItemStack ring = createRing(ringName, Material.GOLD_INGOT, 1300 + rings.indexOf(ringName));
            player.getInventory().addItem(ring);
            player.sendTitle("§6You received", "§e" + ringName, 10, 70, 20);
            player.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(this, "ring"), PersistentDataType.STRING)) {
            return;
        }

        String ringType = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(this, "ring"), PersistentDataType.STRING);
        if (ringType == null) return;

        long currentTime = System.currentTimeMillis();
        long cooldown = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (currentTime < cooldown) {
            player.sendMessage("Ability is on cooldown!");
            return;
        }

        switch (ringType) {
            case "Wither Ring":
                player.launchProjectile(org.bukkit.entity.WitherSkull.class);
                cooldowns.put(player.getUniqueId(), currentTime + 90000);
                break;
            case "Shulker Ring":
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 3));
                cooldowns.put(player.getUniqueId(), currentTime + 180000);
                break;
            case "Illusioner Ring":
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getLocation().distance(player.getLocation()) <= 30) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 300, 1));
                    }
                }
                cooldowns.put(player.getUniqueId(), currentTime + 180000);
                break;
            case "Evoker Ring":
                player.getWorld().spawnEntity(player.getLocation(), org.bukkit.entity.EntityType.EVOKER_FANGS);
                cooldowns.put(player.getUniqueId(), currentTime + 90000);
                break;
            case "Elder Guardian Ring":
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 600, 1));
                cooldowns.put(player.getUniqueId(), currentTime + 120000);
                break;
            case "Sniffer Ring":
                player.teleport(player.getLocation().add(0, -1, 0));
                cooldowns.put(player.getUniqueId(), currentTime + 300000);
                break;
            case "Piglin Brute Ring":
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 100, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
                cooldowns.put(player.getUniqueId(), currentTime + 180000);
                break;
            case "Enderman Ring":
                player.teleport(player.getTargetBlockExact(30).getLocation());
                cooldowns.put(player.getUniqueId(), currentTime + 180000);
                break;
            case "Ender Dragon Ring":
                player.setVelocity(player.getVelocity().setY(1));
                cooldowns.put(player.getUniqueId(), currentTime + 180000);
                break;
            case "Breeze Ring":
                player.setVelocity(player.getLocation().getDirection().multiply(2).setY(1));
                cooldowns.put(player.getUniqueId(), currentTime + 120000);
                break;
        }
    }
}
