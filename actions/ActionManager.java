package incognito.cog.actions;

import java.util.ArrayList;

public class ActionManager {
    private static final ArrayList<Action> actions = new ArrayList<>();

    public static void add(Action action) {
        actions.add(action);
    }

    public static void clear() {
        actions.clear();
    }

    public static void update() {
        for (Action action : actions) {
            action.update();
        }
    }
}
