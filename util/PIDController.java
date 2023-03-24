package incognito.cog.util;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

public class PIDController {
    private PIDCoefficients pidCoefficients;
    private double value;
    private double desired_value;
    private double previous_error, current_error = 0;
    private double P, I, D = 0;


    public PIDController(PIDCoefficients pidCoefficients) {
        this.pidCoefficients = pidCoefficients;
    }

    public void update(double new_value) {
        this.current_error = new_value - desired_value;
        this.D = this.current_error - this.previous_error;
        this.I += this.current_error;
        this.P = this.current_error;
        this.previous_error = this.current_error;
        this.value = new_value;
    }

    public double getOutput() {
        return pidCoefficients.p * value + pidCoefficients.i * I + pidCoefficients.d * D;
    }


}