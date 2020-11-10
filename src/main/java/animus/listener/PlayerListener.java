package animus.listener;

import animus.AnimusPlugin;
import animus.sql.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Ethan Borawski
 */
public class PlayerListener implements Listener {
    private DatabaseManager database;
    public PlayerListener(DatabaseManager instance) {
        this.database = instance;
    }

    public boolean register(UUID uuid, String name) throws SQLException {
        PreparedStatement statement = database.prepareStatement(
                "SELECT * FROM `animus_user` WHERE user_uuid='" + uuid.toString() + "';");
        ResultSet set = statement.executeQuery();
        if(!set.next()) {
            insert(uuid, name);
            return false;
        }
        return true;
    }

    public void insert(UUID uuid, String name) {
        try {
            PreparedStatement statement = database.prepareStatement(
                    "INSERT INTO `animus_user` (user_id,user_uuid,user_name) VALUES (NULL, '" + uuid.toString() + "', '" + name + "')");
            statement.executeUpdate();
            System.out.println("UUID -> " + " (" + uuid + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        // player trying to log in
        Player player = event.getPlayer();
        try {
            boolean registered = register(player.getUniqueId(), player.getName());

            // Player has logged into the server before
            if(registered) {
                System.out.println("Welcome back: " + player.getName());
            }

            // Player has never logged into the server before
            else {
                System.out.println("Inserted new user: Welcome, " + player.getName());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
