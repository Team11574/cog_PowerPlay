package incognito.cog.hardware.component.servo;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ContinuousServoGroup extends ContinuousServo {
    protected Servo[] servos;
    public ContinuousServoGroup(HardwareMap hardwareMap, Telemetry telemetry, Servo[] crServos, double startPos) {
        this(hardwareMap, telemetry, crServos, startPos, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public ContinuousServoGroup(HardwareMap hardwareMap, Telemetry telemetry, Servo[] crServos, double[] startPositions) {
        this(hardwareMap, telemetry, crServos, startPositions, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public ContinuousServoGroup(HardwareMap hardwareMap, Telemetry telemetry, Servo[] crServos,
                           double startPos, double lowBound, double upBound) {
        this(hardwareMap, telemetry, crServos, new double[]{startPos}, lowBound, upBound);
    }


    public ContinuousServoGroup(HardwareMap hardwareMap, Telemetry telemetry, Servo[] crServos,
                           double[] startPositions, double lowBound, double upBound) {
        super(hardwareMap, telemetry, crServos[0], startPositions);
        if (crServos.length < 2)
            throw new IllegalArgumentException("ContinuousServoGroup must have at least 2 servos");
        startPosition = startPositions[0];
        lowerBound = lowBound;
        upperBound = upBound;
        servos = crServos;
        initializeHardware();
        goToStartPosition();
    }

    @Override
    public void setPosition(double position) {
        setPosition(position, true);
    }

    @Override
    public void setPosition(double position, boolean updateLastPosition) {
        if (position < lowerBound) position = lowerBound;
        if (position > upperBound) position = upperBound;
        if (updateLastPosition)
            lastPosition = position;
        for (Servo servo : servos) {
            servo.setPosition(position);
        }
    }
}
