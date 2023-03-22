package incognito.cog.util;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

public class TelemetryBigError {
    static boolean initialized = false;
    static String[] errorMessages = "  █████╗\n ██████║\n██╔═███║\n╚═╝ ███║\n    ███║\n    ███║\n    ███║\n███████████╗\n╚══════════╝\n\n █████████╗\n██╔══════██╗\n╚═╝     ███║\n     ████╔═╝\n   ████╔═╝\n ███╔══╝\n███╔╝\n███████████╗\n╚══════════╝\n\n █████████╗\n██╔══════██╗\n╚═╝      ██║\n     █████╔╝\n         ██╗\n         ██║\n██       ██║\n╚█████████╔╝\n ╚════════╝".split("\n\n");
    static Telemetry telemetry;
    static ElapsedTime time;
    static int i;
    static ArrayList<Integer> errorCodes = new ArrayList<>(); // index
    static ArrayList<Double> errorDurations = new ArrayList<>(); // milliseconds
    static ArrayList<Double> startTimes = new ArrayList<>();

    public static void initialize(Telemetry tel) {
        if (initialized) return;
        telemetry = tel;
        time = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        initialized = true;
    }

    public static void raise(int errorCode) {
        raise(errorCode, 3000);
    }

    public static void raise(int errorCode, double duration) {
        if (!initialized) return;
        errorCodes.add(errorCode-1);
        errorDurations.add(duration);
        startTimes.add(time.time());
    }

    public static void update() {
        if (!initialized) return;
        for (i = 0; i < errorCodes.size(); i++) {
            int errorCode = errorCodes.get(i);
            if (errorCode >= errorMessages.length || errorCode < 0) {
                telemetry.addLine("Error code out of range.");
                continue;
            }
            double duration = errorDurations.get(i);
            double startTime = startTimes.get(i);
            for (String line : errorMessages[errorCode].split("\n")) {
                telemetry.addLine(line);
            }
            if (time.time() > startTime + duration) {
                errorCodes.remove(i);
                errorDurations.remove(i);
                startTimes.remove(i);
                i--;
            }
        }
    }
}
