package me.crazycranberry.streamcraft.config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.streamcraft.config.model.actions.EntitySpawn;

import java.util.LinkedHashMap;

import static me.crazycranberry.streamcraft.StreamCraft.logger;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Action {
    private ActionType type;
    private Trigger trigger;
    private String target;

    public static Action fromYaml(LinkedHashMap<String, ?> input) {
        ActionType type = validateType(input.get("type"));
        Trigger trigger = validateTrigger(input.get("trigger"));
        String target = validateTarget(input.get("target"));
        if (type == null || trigger == null) {
            return null;
        }
        switch(type) {
            case ENTITY_SPAWN:
                return EntitySpawn.fromYaml(type, trigger, target, input);
        }
        return null;
    }

    private static <T> String validateTarget(T target) {
        if (target == null) {
            return "*";
        }
        if (!(target instanceof String)) {
            logger().warning("An Action target was not a String.");
            return "*";
        }
        return (String) target;
    }

    private static <T> Trigger validateTrigger(T trigger) {
        if (!(trigger instanceof LinkedHashMap)) {
            logger().warning("An Action trigger was not an Object.");
            return null;
        }
        return Trigger.fromYaml((LinkedHashMap<String, ?>) trigger);
    }

    private static <T> ActionType validateType(T type) {
        if (!(type instanceof String)) {
            logger().warning("An Action type was not a String.");
            return null;
        }
        ActionType t = ActionType.fromValue((String) type);
        if (t == null) {
            logger().warning("The following Action type is invalid: " + type);
        }
        return t;
    }
}