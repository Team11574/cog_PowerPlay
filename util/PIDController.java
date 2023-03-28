package incognito.cog.util;

import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDController {
    private PIDCoefficients pidCoefficients;
    private double desired_value;
    private double previous_error, current_error = 0;
    private double P, I, D = 0;
    ElapsedTime timer = new ElapsedTime();


    public PIDController(PIDCoefficients pidCoefficients) {
        this.pidCoefficients = pidCoefficients;
    }

    public double update(double new_value) {
        this.current_error = new_value - desired_value;
        this.D = (this.current_error - this.previous_error) / timer.seconds();
        this.I += this.current_error * timer.seconds();
        this.P = this.current_error;
        this.previous_error = this.current_error;
        timer.reset();
        return getOutput();
    }

    public double getOutput() {
        return pidCoefficients.p * P + pidCoefficients.i * I + pidCoefficients.d * D;
    }

    public void setDesiredValue(double desired_value) {
        this.desired_value = desired_value;
    }

    public void reset() {
        this.previous_error = 0;
        this.current_error = 0;
        this.P = 0;
        this.I = 0;
        this.D = 0;
    }

    public void setPIDCoefficients(PIDCoefficients pidCoefficients) {
        this.pidCoefficients = pidCoefficients;
    }

    public PIDCoefficients getPIDCoefficients() {
        return pidCoefficients;
    }


}