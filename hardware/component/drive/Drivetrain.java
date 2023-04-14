package incognito.cog.hardware.component.drive;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Drivetrain extends SampleMecanumDrive {
    // ===== Instance Variables =====
    // -- Hardware --

    // Robot Motors
    DcMotor DT_frontRight_M;
    DcMotor DT_backRight_M;
    DcMotor DT_frontLeft_M;
    DcMotor DT_backLeft_M;

    DcMotor[] DT_Motors;

    //BHI260IMU imu;

    public Drivetrain(HardwareMap hardwareMap, Telemetry telemetry, DcMotorEx frontRight, DcMotorEx backRight, DcMotorEx frontLeft, DcMotorEx backleft) {
        super(hardwareMap, telemetry, frontRight, backRight, frontLeft, backleft);
    }

    public Drivetrain(HardwareMap hardwareMap, Telemetry telemetry, DcMotorEx[] motors) {
        this(hardwareMap, telemetry, motors[0], motors[1], motors[2], motors[3]);
    }

    public IMU getImu() {
        return this.imu;
    }
}