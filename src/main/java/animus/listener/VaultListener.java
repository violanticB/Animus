package animus.listener;

import animus.AnimusPlugin;
import animus.item.AnimusItem;
import animus.user.AnimusUser;
import animus.util.Base64Coder;
import animus.vault.Vault;
import animus.vault.VaultManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        for (Integer removedItem : vault.getRemovedItems()) {
            String removeQuery = "DELETE FROM `animus_item` WHERE item_id=" + removedItem;
            AnimusPlugin.instance().getServer().getScheduler().runTaskAsynchronously(AnimusPlugin.instance(), () -> {
                try {
                    PreparedStatement statement = manager.getManager().prepareStatement(removeQuery);
                    statement.executeUpdate();

                    System.out.println("Removing item: " + removedItem);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        }

        for (AnimusItem animusItem : vault.getCurrentItems()) {
            if(animusItem == null)
                continue;

            if(animusItem.getId() == -1L) {

                // Item is fresh
                String insertQuery = "INSERT INTO `animus_item` (`item_id`, `owner`, `vault_index`, `item`) " +
                        "VALUES (NULL, " + user.getUser_id() + ", " + animusItem.getIndex() + ", '" + animusItem.getStackBase64() + "')";

                AnimusPlugin.instance().getServer().getScheduler().runTaskAsynchronously(AnimusPlugin.instance(), () -> {
                    try {
                        PreparedStatement statement = manager.getManager().prepareStatement(insertQuery);
                        statement.executeUpdate();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
                System.out.println(insertQuery);

            }
        }
    }

    public AnimusItem[] loadItems(Vault vault, AnimusUser user) {
        String query = "SELECT * FROM animus_item WHERE owner=" + user.getUser_id() + ";";
        AnimusItem[] items = new AnimusItem[vault.getItemSlots().size()];

        try {
            PreparedStatement statement = manager.getManager().prepareStatement(query);
            ResultSet set = statement.executeQuery();

            System.out.println("Querying items for: " + user.getUser_name());
            int i = 0;
            while(set.next()) {

                AnimusItem item = new AnimusItem();
                item.setId(set.getInt("item_id"));
                item.setIndex(set.getInt("vault_index"));

                Blob b = set.getBlob("item");
                item.setStackBase64(set.getString("item"));

                System.out.println("-     " + item.getStackBase64());
                items[i++] = item;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return items;
    }

    public String toBase64(ItemStack stack) {
        String base64 = "";

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(stream);

            data.writeObject(stack);
            data.close();
            return Base64Coder.encodeLines(stream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return base64;
    }

    public ItemStack fromBase64(String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            return (ItemStack) dataInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void check(Vault vault, Location clickedBlock,
                      Player player, ItemStack stack) {

        for (int i = 0; i < vault.getItemSlots().size(); i++) {

            Location itemSlot = vault.getItemSlots().get(i);
            if(itemSlot.getBlockX() == clickedBlock.getBlockX() && itemSlot.getBlockY() == clickedBlock.getBlockY()
                    && itemSlot.getBlockZ() == clickedBlock.getBlockZ()) {

                // Player clicked on a vault slot
                // Item slot is empty

                System.out.println("Checking item");
                if(vault.getCurrentItems()[i] != null) {
                    System.out.println("Item not null: " + vault.getCurrentItems()[i].getStackBase64());
                    AnimusItem current = vault.getCurrentItems()[i];
                    String base64 = current.getStackBase64();
                    ItemStack added = fromBase64(base64);

                    if (added == null) {
                        System.out.println("Could not parse current item");
                        return;
                    }

                    player.getInventory().addItem(added);
                    vault.getCurrentItems()[i] = null;
                    vault.getStands().get(i).getEquipment().setHelmet(new ItemStack(Material.AIR));

                    player.sendMessage(ChatColor.GRAY + "You have removed an item from your vault: ");
                    player.sendMessage(ChatColor.YELLOW + base64);

                } else {

                    // Item doesn't exist
                    if(player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You must have an item in your hand to do that");
                        return;
                    }

                    System.out.println("Adding item");

                    AnimusItem item = new AnimusItem();
                    item.setIndex(i);
                    item.setStackBase64(toBase64(stack));
                    vault.getCurrentItems()[i] = item;
                    vault.getStands().get(i).getEquipment().setHelmet(fromBase64(item.getStackBase64()));

                    player.sendMessage(ChatColor.GRAY + "You have added an item to your vault: ");
                    player.sendMessage(ChatColor.YELLOW + item.getStackBase64());
                    player.setItemInHand(new ItemStack(Material.AIR));

                }

            }


        }

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
                vault.setCurrentItems(loadItems(vault, PlayerListener.cache.get(player.getUniqueId())));

                int i = 0;
                for (ArmorStand stand : vault.getStands()) {
                    if(vault.getCurrentItems()[i] == null || vault.getCurrentItems()[i].getStackBase64() == null)
                        continue;

                    stand.getEquipment().setHelmet(
                            fromBase64(vault.getCurrentItems()[i++].getStackBase64())
                    );
                }

                new BukkitRunnable() {
                    public void run() {

                        if(!vault.getRegion().isInside(player.getLocation())) {
                            player.sendMessage(ChatColor.RED + "The vault has closed because you did not enter");
                            vault.removeViewer();
                            manager.invokeAnimation(vault, false);
                        }
                    }
                }.runTaskLater(AnimusPlugin.instance(), 20 * 5);

            } else {
                check(vault, clicked.getLocation(), player, event.getItem());
                event.setCancelled(true);
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

                    saveVault(vault, PlayerListener.cache.get(event.getPlayer().getUniqueId()));

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
