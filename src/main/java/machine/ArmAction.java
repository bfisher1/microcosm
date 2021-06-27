package machine;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class ArmAction {
    private ArmActionType type;
    private HashMap<String, Object> args;

    public ArmAction(ArmActionType type) {
        this.type = type;
        this.args = new HashMap<>();
    }

    public void setArg(String arg, Object value) {
        this.args.put(arg, value);
    }

    public boolean hasArg(String arg) {
        return args.containsKey(arg);
    }

    public <T> T getArgValue(String arg, Class<T> clazz) {
        if (!hasArg(arg)) {
            throw new IllegalArgumentException("Arg " + arg + " not provided for " + type + " action!");
        }
        return (T) this.args.get(arg);
    }

    public String getArgsString() {
        final StringBuilder ret = new StringBuilder("");
        args.keySet().stream().forEach(key -> {
            ret.append(key);
            ret.append(" : ");
            ret.append(args.get(key));
            ret.append(", ");
        });
        return ret.toString();
    }
}
