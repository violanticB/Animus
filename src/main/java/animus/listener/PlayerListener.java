package animus.listener;

import animus.AnimusPlugin;
import animus.sql.DatabaseManager;
import animus.user.AnimusUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ethan Borawski
 */
public class PlayerListener implements Listener {
    private DatabaseManager database;
    public static Map<UUID, AnimusUser> cache = new ConcurrentHashMap<>();
    public PlayerListener(DatabaseManager instance) {
        this.database = instance;
    }

    public DatabaseManager getDatabase() {
        return database;
    }

    public Map<UUID, AnimusUser> getCache() {
        return cache;
    }

    public boolean register(UUID uuid, String name) throws SQLException {
        PreparedStatement statement = database.prepareStatement(
                "SELECT * FROM `animus_user` WHERE user_uuid='" + uuid.toString() + "';");
        ResultSet set = statement.executeQuery();
        if(!set.next()) {
            insert(uuid, name);
            return false;
        } else {
            AnimusUser user = new AnimusUser();
            PreparedStatement idQuery = database.prepareStatement(
                    "SELECT user_id FROM `animus_user` WHERE user_uuid='" + uuid.toString() + "';");
            ResultSet idSet = idQuery.executeQuery();
            user.setUser_name(name);
            user.setUser_uuid(uuid);

            while (idSet.next()) {
                user.setUser_id(idSet.getInt("user_id"));
            }

            cache.put(uuid, user);
            return true;
        }
    }

    public void insert(UUID uuid, String name) {
        try {
            PreparedStatement statement = database.prepareStatement(
                    "INSERT INTO `animus_user` (user_id,user_uuid,user_name) VALUES (NULL, '" + uuid.toString() + "', '" + name + "')");
            statement.executeUpdate();
            System.out.println("UUID -> " + " (" + uuid + ")");

            AnimusUser user = new AnimusUser();
            user.setUser_uuid(uuid);
            user.setUser_name(name);
            PreparedStatement idQuery = database.prepareStatement(
                    "SELECT user_id FROM `animus_user` WHERE user_uuid='" + uuid.toString() + "';");
            ResultSet idSet = idQuery.executeQuery();

            while (idSet.next()) {
                user.setUser_id(idSet.getInt("user_id"));
            }

            cache.put(uuid, user);

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
