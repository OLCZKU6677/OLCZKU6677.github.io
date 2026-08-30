package pl.olczku.wOOLCODEKOMENDALOBBY;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(id = "woolcode-komendalobby", name = "WOOLCODE-KOMENDALOBBY", version = "1.0-SNAPSHOT")
public class WOOLCODEKOMENDALOBBY {

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer proxyServer;

    @Inject
    private CommandManager commandManager;

    private String lobbyServerName;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        loadConfig();
        registerCommands();
    }

    private void loadConfig() {
        Path configPath = proxyServer.getPluginManager().getPlugin("woolcode-komendalobby").get().getConfigDirectory();
        File configFile = new File(configPath.toFile(), "config.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                logger.error("Could not create config file!", e);
            }
        }

        YAMLConfigurationLoader configLoader = YAMLConfigurationLoader.builder().setFile(configFile).build();
        try {
            ConfigurationNode config = configLoader.load();
            if (config.getNode("lobby-server-name").isVirtual()) {
                config.getNode("lobby-server-name").setValue("lobby");
                configLoader.save(config);
            }
            lobbyServerName = config.getNode("lobby-server-name").getString();
        } catch (IOException e) {
            logger.error("Could not load config file!", e);
        }
    }

    private void registerCommands() {
        commandManager.register("lobby", new LobbyCommand(), "hub");
    }

    private class LobbyCommand implements SimpleCommand {

        @Override
        public void execute(Invocation invocation) {
            CommandSource source = invocation.source();
            if (source instanceof Player) {
                Player player = (Player) source;
                Optional<RegisteredServer> lobbyServer = proxyServer.getServer(lobbyServerName);
                if (lobbyServer.isPresent()) {
                    player.createConnectionRequest(lobbyServer.get()).fireAndForget();
                } else {
                    player.sendMessage(net.kyori.adventure.text.Component.text("Lobby server not found!"));
                }
            } else {
                source.sendMessage(net.kyori.adventure.text.Component.text("Only players can use this command!"));
            }
        }
    }
}
