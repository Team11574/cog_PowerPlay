package incognito.cog.v2;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class GlobalTelemetry {
    private static Cogsole telemetry = null;

    public static Telemetry getInstance() {
        if (telemetry == null) {
            throw new NullPointerException("Global Telemetry not yet initialized!");
        }
        return telemetry;
    }

    public static void initialize(Cogsole telemetry) {
        GlobalTelemetry.telemetry = telemetry;
    }
}
