package incognito.cog.hardware.component.camera;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.openftc.easyopencv.OpenCvCamera;

import incognito.cog.hardware.component.HardwareComponent;
import incognito.teamcode.robot.component.camera.cv.Pipeline;
import org.openftc.easyopencv.OpenCvCameraRotation;

public class CameraSuper extends HardwareComponent {

    // Instance Variables
    protected Pipeline pipeline;
    protected OpenCvCamera cvCamera;
    protected boolean isStreaming = true;

    public CameraSuper(HardwareMap hardwareMap, Telemetry telemetry) {
        super(hardwareMap, telemetry);
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
    }

    protected void initializeHardware() {

    }

    /**
     * Terminate Camera stream to save resources.
     */
    public void stopCamera() {
        isStreaming = false;
        cvCamera.stopStreaming();
        pipeline.stop();
    }

    public void startCamera() {
        isStreaming = true;
        cvCamera.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
        pipeline.start();
    }

    public void toggleCamera() {
        if (isStreaming) {
            stopCamera();
        } else {
            startCamera();
        }
    }
}