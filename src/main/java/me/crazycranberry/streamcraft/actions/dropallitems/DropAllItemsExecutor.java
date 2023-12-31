package me.crazycranberry.streamcraft.actions.dropallitems;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;

public class DropAllItemsExecutor implements Executor {
    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof DropAllItems)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        DropAllItems dai = (DropAllItems) action;
        for (Player p : getTargetedPlayers(dai)) {
            for (int i = 0; i < p.getInventory().getContents().length; i++) {
                if (p.getInventory().getContents()[i] != null) {
                    double randXOffset = Math.random() * 10 - 5;
                    double randYOffset = Math.random() * 10 - 5;
                    double randZOffset = Math.random() * 10 - 5;
                    p.getWorld().dropItem(p.getLocation().add(new Vector(randXOffset, randYOffset, randZOffset)), p.getInventory().getContents()[i]);
                    p.getInventory().setItem(i, null);
                }
            }
        }
    }
}
