package incognito.cog.hardware.component.motor;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import incognito.cog.hardware.component.HardwareComponent;

public class LinearSlide extends HardwareComponent {
    public LinearSlide(HardwareMap hardwareMap, Telemetry telemetry) {
        super(hardwareMap, telemetry);
    }

    @Override
    protected void initializeHardware() {

    }

    public static enum Direction {
        VERTICAL, HORIZONTAL
    }
}