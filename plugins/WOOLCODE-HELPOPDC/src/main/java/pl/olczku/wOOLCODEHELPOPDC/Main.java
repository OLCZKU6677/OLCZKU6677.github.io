package pl.olczku.wOOLCODEHELPOPDC;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private static DiscordWebhook webhook;
    private Messages messages;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        messages = new Messages(this);
        messages.setup();

        String webhookUrl = getConfig().getString("discord.webhook-url");
        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            webhook = new DiscordWebhook(webhookUrl);
        } else {
            getLogger().severe("Nie ustawiono webhook URL w configu!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("helpop").setExecutor(new HelpopCommand());
        getLogger().info("WOOLCODEHELPOPDC wlaczony!");
    }

    public static Main getInstance() {
        return instance;
    }

    public static DiscordWebhook getWebhook() {
        return webhook;
    }

    public Messages getMessages() {
        return messages;
    }
}
