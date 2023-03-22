package incognito.cog.util;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TelemetryBigError {
    static boolean initialized = false;
    static String[] errorMessages;
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
        try {
            // Read the file BigNumbers.txt and split it by double newlines
            errorMessages = String.join("\n",
                    Files.readAllLines(
                            Paths.get("incognito/cog/util/BigNumbers.txt")
                    )
            ).split("\n\n");
        } catch (IOException e) {
            telemetry.addLine("Error reading error messages.");
        }
    }

    public static void raise(int errorCode) {
        raise(errorCode, 1000);
    }

    public static void raise(int errorCode, double duration) {
        if (initialized) return;
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
            telemetry.addLine(errorMessages[errorCode]);
            if (time.time() > startTime + duration) {
                errorCodes.remove(i);
                errorDurations.remove(i);
                startTimes.remove(i);
                i--;
            }
        }
    }
}
