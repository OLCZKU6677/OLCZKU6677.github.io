package pl.olczku.skyluckCore.Przekierowanie;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RedirectCommand extends Command {
    private final String targetCommand;
    public RedirectCommand(String alias, String targetCommand) {
        super(alias);
        this.targetCommand = targetCommand;
        this.description = "Przekierowuje do /" + targetCommand;
        this.usageMessage = "/" + alias;
    }
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        String commandToExecute = targetCommand;
        if (args.length > 0) commandToExecute += " " + String.join(" ", args);
        Bukkit.dispatchCommand(sender, commandToExecute.trim());
        return true;
    }
}