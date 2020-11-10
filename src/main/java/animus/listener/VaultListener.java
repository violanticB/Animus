package animus.listener;

import animus.AnimusPlugin;
import animus.user.AnimusUser;
import animus.vault.Vault;
import animus.vault.VaultManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Ethan Borawski
 */
public class VaultListener implements Listener {

    private VaultManager manager;

    public VaultListener(VaultManager manager) {
        this.manager = manager;
    }

    public void saveVault(Vault vault, AnimusUser user) {
        // SQL query save all items in vault


    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();
        for (Vault vault : manager.getVaults()) {
            assert clicked != null;
            if(vault.getDoorGeometry().isInside(clicked.getLocation()) && vault.getCurrentViewer() == null) {

                manager.invokeAnimation(vault, true);
                vault.setCurrentViewer(player.getUniqueId());
                vault.getDoorGeometry().getCenter().getWorld()
                        .playSound(vault.getDoorGeometry().getCenter(), Sound.ENTITY_ITEM_PICKUP, 1, 1);

                new BukkitRunnable() {
                    public void run() {

                        if(!vault.getRegion().isInside(player.getLocation())) {
                            player.sendMessage(ChatColor.RED + "The vault has closed because you did not enter");
                            vault.removeViewer();
                            manager.invokeAnimation(vault, false);
                        }
                    }
                }.runTaskLater(AnimusPlugin.instance(), 20 * 5);

            } else if() {

            }

        }

    }

    @EventHandler
    public void onRegionEnter(PlayerMoveEvent event) {

        Location to = event.getTo();
        Location from = event.getFrom();

        if(to.getX() != from.getX() || to.getZ() != from.getZ()) {

            for (Vault vault : manager.getVaults()) {
                if ((!vault.getRegion().isInside(from) && vault.getRegion().isInside(to))
                        && (vault.getCurrentViewer() == null || !vault.getCurrentViewer().equals(event.getPlayer().getUniqueId()))) {
                    assert to != null;
                    event.getPlayer().setVelocity(
                            from.toVector().subtract(to.toVector()).normalize().multiply(1.05)
                    );

                    event.getPlayer().sendMessage(ChatColor.RED + "You can't enter that vault!");
                    event.getPlayer().getWorld().playSound(from, Sound.BLOCK_ANVIL_LAND, 1, 1);
                } else if (vault.getRegion().isInside(from) && !vault.getRegion().isInside(to)
                        && (vault.getCurrentViewer().equals(event.getPlayer().getUniqueId()))) {

                    vault.removeViewer();
                    manager.invokeAnimation(vault, false);
                    event.getPlayer().sendMessage(ChatColor.GOLD + "You have left your bank vault...");

                } else if ((!vault.getRegion().isInside(from) && vault.getRegion().isInside(to))
                        && (vault.getCurrentViewer().equals(event.getPlayer().getUniqueId()))) {

                    event.getPlayer().sendMessage(ChatColor.GOLD + "Welcome to your bank vault");

                }
            }
        }
    }

}
