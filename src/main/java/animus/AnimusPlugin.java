package animus;

import animus.command.LoadInventoryCommand;
import animus.command.StoreInventoryCommand;
import animus.listener.PlayerListener;
import animus.listener.VaultListener;
import animus.sql.DatabaseManager;
import animus.vault.Vault;
import animus.vault.VaultConfig;
import animus.vault.VaultManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

/**
 * @author Ethan Borawski
 */
public class AnimusPlugin extends JavaPlugin {

    private static AnimusPlugin instance;
    private DatabaseManager database;
    private VaultConfig vaultConfig;
    private VaultManager vaultManager;

    /**
     * This method is called when the Plugin
     * is enabled in the server.
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        // Instantiate fields
        try {
            database = new DatabaseManager(
                    "sql.ca.electricviking.media", "dion_devops", "3koH2IVqij9hsxpk", "dion_vextra_util"
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        vaultConfig = new VaultConfig(getConfig().getConfigurationSection("vaults"));
        vaultManager = new VaultManager(database, vaultConfig);
        vaultManager.reload();
        for (Vault vault : vaultManager.getVaults()) {
            vaultManager.invokeAnimation(vault, false);
        }

        getCommand("vload").setExecutor(new LoadInventoryCommand());
        getCommand("vsave").setExecutor(new StoreInventoryCommand());

        // Register server events
        getServer().getPluginManager().registerEvents(new PlayerListener(database), this);
        getServer().getPluginManager().registerEvents(new VaultListener(vaultManager), this);
    }

    public static AnimusPlugin instance() {
        return instance;
    }

    public DatabaseManager getDatabase() {
        return database;
    }

    public VaultConfig getVaultConfig() {
        return vaultConfig;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }
}
