package incognito.cog.hardware.component.motor;

import static incognito.cog.util.Generic.withinThreshold;
import static incognito.teamcode.config.SlideConstants.CURRENT_ALERT_STOPPED;
import static incognito.teamcode.config.SlideConstants.S_RUN_TO_POSITION_POWER;
import static incognito.teamcode.config.SlideConstants.S_SET_POSITION_THRESHOLD;
import static incognito.teamcode.config.SlideConstants.VELOCITY_STOP_THRESHOLD;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.ArrayList;

import incognito.cog.exceptions.UndefinedSetPositionException;
import incognito.cog.hardware.component.HardwareComponent;
import incognito.teamcode.robot.component.slide.VerticalSlide;

public class MotorGroup extends HardwareComponent {
    protected double MIN_ENCODER_POSITION;
    protected double MAX_ENCODER_POSITION;
    protected double TICKS_PER_INCH;
    // temp public
    public double RUN_TO_POSITION_POWER = S_RUN_TO_POSITION_POWER;

    protected ArrayList<Integer> setPositions;
    protected int lastPosition = 0;
    protected double maxPower = 1;
    protected double startMaxPower;
    protected double lastPower = 0;

    public DcMotorEx[] motors;

    protected boolean disabled = false;

    public MotorGroup(HardwareMap hardwareMap, Telemetry telemetry, DcMotorEx motor, double ticksPerInch) {
        this(hardwareMap, telemetry, new DcMotorEx[]{motor}, ticksPerInch);
    }

    public MotorGroup(HardwareMap hardwareMap, Telemetry telemetry, DcMotorEx motor, double minEncoderPosition, double maxEncoderPosition, double ticksPerInch) {
        this(hardwareMap, telemetry, new DcMotorEx[]{motor}, minEncoderPosition, maxEncoderPosition, ticksPerInch);
    }

    public MotorGroup(HardwareMap hardwareMap, Telemetry telemetry, DcMotorEx[] motors, double ticksPerInch) {
        this(hardwareMap, telemetry, motors, 0, Double.POSITIVE_INFINITY, ticksPerInch);
    }

    public MotorGroup(HardwareMap hardwareMap, Telemetry telemetry, DcMotorEx[] motors, double minEncoderPosition, double maxEncoderPosition, double ticksPerInch) {
        super(hardwareMap, telemetry);
        this.setPositions = new ArrayList<>();
        this.motors = motors;
        this.MIN_ENCODER_POSITION = minEncoderPosition;
        this.MAX_ENCODER_POSITION = maxEncoderPosition;
        this.TICKS_PER_INCH = ticksPerInch;
        setTargetPosition(0);
        startMaxPower = maxPower;

        //initializeHardware();
    }

    protected void initializeHardware() {
        for (DcMotorEx motor : motors) {
            motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        }
    }

    public void setMaxPower(double newPower) {
        maxPower = newPower;
    }

    public double getDirection() {
        return Math.signum(lastPower);
    }

    public void setPower(double power) {
        if (disabled)
            return;

        if (power == 0 && motors[0].getMode() == DcMotorEx.RunMode.RUN_TO_POSITION) {
            return;
        }
        lastPower = power;
        double realPower = Math.min(power * maxPower, maxPower);
        for (DcMotorEx motor : motors) {
            motor.setPower(realPower);
            motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        }
    }

    public ArrayList<Double> getPowers() {
        ArrayList<Double> powers = new ArrayList<>();
        for (DcMotorEx motor : motors) {
            powers.add(motor.getPower());
        }
        return powers;
    }

    public double getPower() {
        double sum = 0;
        for (DcMotorEx motor : motors) {
            sum += motor.getPower();
        }
        return sum / motors.length;
    }

    /*
    public static boolean withinThreshold(double currentValue, double targetValue, double threshold) {
        return Math.abs(currentValue - targetValue) <= threshold;
    }
     */

    public boolean atSetPosition() {
        return atSetPosition(S_SET_POSITION_THRESHOLD);
    }

    public boolean atSetPosition(double threshold) {
        double sum = 0;
        for (DcMotorEx motor : motors) {
            sum += motor.getCurrentPosition();
        }
        sum /= motors.length;
        return withinThreshold(motors[0].getTargetPosition(), sum, threshold);
    }

    public void setTargetPosition(int position) {
        if (disabled)
            return;
        // Run to position needs some power value to run at, default is 1
        RUN_TO_POSITION_POWER = Math.min(RUN_TO_POSITION_POWER * maxPower, maxPower);

        lastPosition = getPosition();

        for (DcMotorEx motor : motors) {
            motor.setTargetPosition(position);
            motor.setPower(RUN_TO_POSITION_POWER);
            motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        }
    }

    public double getTargetPosition() {
        return motors[0].getTargetPosition();
    }

    public void goToSetPosition(int setPosition) {
        if (setPosition > setPositions.size()) {
            telemetry.addLine("Undefined set position!");
        }
        setTargetPosition(setPositions.get(setPosition));
    }

    public boolean goToPositionConstant(VerticalSlide.SetPosition index) {
        return goToPositionConstant(index.ordinal());
    }

    public boolean goToPositionConstant(int index) {
        if (index > setPositions.size()) {
            telemetry.addLine("Undefined set position!");
            return false;
        }
        if (Math.abs(setPositions.get(index) - getPosition()) < S_SET_POSITION_THRESHOLD) {
            setPower(0);
            return true;
        } else {
            setPower(Math.signum(setPositions.get(index) - getPosition()) * RUN_TO_POSITION_POWER);
            return false;
        }
    }

    public void goToLastPosition() {
        setTargetPosition(lastPosition);
    }

    public void hardReset() {
        for (DcMotorEx motor : motors) {
            motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

    public void addSetPosition(int position) {
        setPositions.add(position);
    }

    public void addSetPositionLength(double length) {
        addSetPosition((int) (length * TICKS_PER_INCH));
    }

    public void addSetPositions(int[] positions) {
        for (int position : positions) {
            addSetPosition(position);
        }
    }

    public void addSetPositionLengths(double[] lengths) {
        for (double length : lengths) {
            addSetPositionLength(length);
        }
    }


    public void setSetPosition(int positionIndex, int positionValue) throws UndefinedSetPositionException {
        if (positionIndex > setPositions.size()) {
            throw new UndefinedSetPositionException();
        }
        setPositions.set(positionIndex, positionValue);
    }


    public void setSetPositionLength(int positionIndex, double positionValue) throws UndefinedSetPositionException {
        setSetPosition(positionIndex, (int) (positionValue * TICKS_PER_INCH));
    }

    public int getPosition() {
        int totalCount = 0;
        for (DcMotorEx motor : motors) {
            totalCount += motor.getCurrentPosition();
        }
        return totalCount / motors.length;
    }

    public void goToLength(double length) {
        setTargetPosition((int) (MIN_ENCODER_POSITION + TICKS_PER_INCH * length));
    }

    public double getVelocity() {
        double totalVel = 0;
        for (DcMotorEx motor : motors) {
            totalVel += motor.getVelocity();
        }
        return totalVel / motors.length;
    }

    public void update() {
        if (disabled) {
            telemetry.addData("DISABLED", this.getClass());
            return;
        }
        for (DcMotorEx motor : motors) {
            boolean overCurrent = motor.isOverCurrent();
            boolean overStopCurrent = (motor.getCurrent(CurrentUnit.MILLIAMPS) > CURRENT_ALERT_STOPPED &&
                    motor.getVelocity() < VELOCITY_STOP_THRESHOLD);
            if (overCurrent || overStopCurrent) {
                disable();
                break;
            }
        }
    }

    public void disable() {
        disabled = true;
        for (DcMotorEx motor : motors) {
            motor.setMotorDisable();
            motor.setPower(0);
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }
}