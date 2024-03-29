package incognito.cog.hardware.component.servo;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ContinuousServo extends SetServo {
    //private Servo servo;
    protected double startPosition;
    protected double lowerBound;
    protected double upperBound;
    protected double offsetFactor = 1;

    public ContinuousServo(HardwareMap hardwareMap, Telemetry telemetry, Servo crServo, double startPos) {
        this(hardwareMap, telemetry, crServo, startPos, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public ContinuousServo(HardwareMap hardwareMap, Telemetry telemetry, Servo crServo, double[] startPositions) {
        this(hardwareMap, telemetry, crServo, startPositions, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public ContinuousServo(HardwareMap hardwareMap, Telemetry telemetry, Servo crServo,
                           double startPos, double lowBound, double upBound) {
        this(hardwareMap, telemetry, crServo, new double[]{startPos}, lowBound, upBound);
    }


    public ContinuousServo(HardwareMap hardwareMap, Telemetry telemetry, Servo crServo,
                           double[] startPositions, double lowBound, double upBound) {
        super(hardwareMap, telemetry, crServo, startPositions);
        startPosition = startPositions[0];
        lowerBound = lowBound;
        upperBound = upBound;
        initializeHardware();
        goToStartPosition();
    }

    public Servo getServo() {
        return servo;
    }

    public void goToStartPosition() {
        setPosition(startPosition);
    }

    public void setPosition(double position) {
        setPosition(position, true);
    }

    public void setPosition(double position, boolean updateLastPosition) {
        if (position < lowerBound) position = lowerBound;
        if (position > upperBound) position = upperBound;
        if (updateLastPosition)
            lastPosition = position;
        servo.setPosition(position);
    }

    public void setOffsetFactor(double factor) {
        offsetFactor = factor;
    }

    public double getOffsetFactor() {
        return offsetFactor;
    }

    public void offsetPosition(double offset) {
        offsetPosition(offset, true);
    }

    public void offsetPosition(double offset, boolean updateLastPosition) {
        setPosition(getPosition() + offset * offsetFactor, updateLastPosition);
    }

    /*
    TODO: For Dallin to fix later, add checking of set positions within bounds
    @Override
    public void addSetPositions(double[] newPositions) {
        double[] editedPositions = newPositions.clone();
        for (int i = 0; i < newPositions.length; i++) {
            if (newPositions[i] < lowerBound) {
                editedPositions[i] = lowerBound;
            } else if (newPositions[i] > upperBound) {
                editedPositions[i] = upperBound;
            }
        }
        super.addSetPositions(editedPositions);
    }

     */

    @Override
    protected void initializeHardware() {
        // do nothing
    }
}