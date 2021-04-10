package machine;

import lombok.Getter;
import lombok.Setter;

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
        return (T) this.args.get(arg);
    }
}
