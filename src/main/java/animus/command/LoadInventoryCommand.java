package animus.command;

import animus.AnimusPlugin;
import animus.item.AnimusItem;
import animus.listener.PlayerListener;
import animus.user.AnimusUser;
import animus.util.Base64Coder;
import animus.vault.Vault;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoadInventoryCommand implements CommandExecutor {

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

    public AnimusItem[] loadItems(Player player, AnimusUser user) {
        String query = "SELECT * FROM animus_item WHERE owner=" + user.getUser_id() + ";";
        AnimusItem[] items = new AnimusItem[36];

        try {
            PreparedStatement statement = AnimusPlugin.instance().getDatabase().prepareStatement(query);
            ResultSet set = statement.executeQuery();

            System.out.println("Querying items for: " + user.getUser_name());
            int i = 0;
            while(set.next()) {

                AnimusItem item = new AnimusItem();
                item.setId(set.getInt("item_id"));
                item.setIndex(set.getInt("vault_index"));

                item.setStackBase64(set.getString("item"));

                System.out.println("-     " + item.getStackBase64());
                items[i++] = item;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return items;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        AnimusItem[] items = loadItems(player, PlayerListener.cache.get(player.getUniqueId()));

        player.getInventory().clear();
        player.updateInventory();

        for (AnimusItem item : items) {
            player.getInventory().addItem(fromBase64(item.getStackBase64()));
        }

        player.updateInventory();
        player.sendMessage(ChatColor.GREEN + "You have loaded the items from the database");

        return false;
    }
}
