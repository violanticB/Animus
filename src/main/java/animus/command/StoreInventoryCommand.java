package animus.command;

import animus.AnimusPlugin;
import animus.item.AnimusItem;
import animus.listener.PlayerListener;
import animus.user.AnimusUser;
import animus.util.Base64Coder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import java.sql.SQLException;

public class StoreInventoryCommand implements CommandExecutor {

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

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player player = (Player) commandSender;
        player.sendMessage(ChatColor.GREEN + "Storing inventory");
        AnimusUser user = PlayerListener.cache.get(player.getUniqueId());

        int id = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if(stack == null || stack.getType() == Material.AIR)
                continue;

                String base65 = toBase64(stack);

                // Item is fresh
                String insertQuery = "INSERT INTO `animus_item` (`item_id`, `owner`, `vault_index`, `item`) " +
                        "VALUES (NULL, " + user.getUser_id() + ", " + id++ + ", '" + base65 + "')";

                AnimusPlugin.instance().getServer().getScheduler().runTaskAsynchronously(AnimusPlugin.instance(), () -> {
                    try {
                        PreparedStatement statement = AnimusPlugin.instance().getDatabase().prepareStatement(insertQuery);
                        statement.executeUpdate();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
                System.out.println(insertQuery);

        }

        return false;
    }
}
