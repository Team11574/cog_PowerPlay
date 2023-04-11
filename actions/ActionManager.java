package incognito.cog.actions;

import java.util.ArrayList;

import incognito.cog.util.TelemetryBigError;
import incognito.teamcode.opmodes.tele.Tele;

public class ActionManager {
    private static final ArrayList<Action> actions = new ArrayList<>();
    private static final ArrayList<Action> actionHolder = new ArrayList<>();

    public static void add(Action action) {
        /*
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            TelemetryBigError.telemetry.addLine(element.toString());
        }
        TelemetryBigError.telemetry.addLine();
        TelemetryBigError.telemetry.update();
         */
        actionHolder.add(action);
    }

    public static void clear() {
        actions.clear();
    }

    public static void update() {
        for (Action action : actions) {
            action.update();
        }
        if (actionHolder.size() > 0) {
            actions.addAll(actionHolder);
            actionHolder.clear();
        }
    }
}
