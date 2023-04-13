package incognito.cog.hardware.gamepad;

import com.qualcomm.robotcore.hardware.Gamepad;

public class Cogtroller {
    public Button dpad_up;
    public Button dpad_down;
    public Button dpad_left;
    public Button dpad_right;
    public Button a;
    public Button b;
    public Button x;
    public Button y;
    public Button left_bumper;
    public Button right_bumper;
    public Button left_stick_button;
    public Button right_stick_button;
    public Button left_trigger;
    public Button right_trigger;
    public Button guide;

    public Gamepad gamepad;

    public Cogtroller(Gamepad pad) {
        gamepad = pad;
        dpad_up = new Button((Gamepad gamepad) -> gamepad.dpad_up);
        dpad_down = new Button((Gamepad gamepad) -> gamepad.dpad_down);
        dpad_left = new Button((Gamepad gamepad) -> gamepad.dpad_left);
        dpad_right = new Button((Gamepad gamepad) -> gamepad.dpad_right);
        a = new Button((Gamepad gamepad) -> gamepad.a);
        b = new Button((Gamepad gamepad) -> gamepad.b);
        x = new Button((Gamepad gamepad) -> gamepad.x);
        y = new Button((Gamepad gamepad) -> gamepad.y);
        left_bumper = new Button((Gamepad gamepad) -> gamepad.left_bumper);
        right_bumper = new Button((Gamepad gamepad) -> gamepad.right_bumper);
        left_stick_button = new Button((Gamepad gamepad) -> gamepad.left_stick_button);
        right_stick_button = new Button((Gamepad gamepad) -> gamepad.right_stick_button);
        left_trigger = new Button((Gamepad gamepad) -> gamepad.left_trigger > 0);
        right_trigger = new Button((Gamepad gamepad) -> gamepad.right_trigger > 0);
        guide = new Button((Gamepad gamepad) -> gamepad.guide);
    }

    public float get_partitioned_left_stick_y() {
        double theta = Math.atan2(gamepad.left_stick_y, gamepad.left_stick_x);
        if (Math.abs(theta) < Math.PI / 6) {
            return 0;
        }
        return gamepad.left_stick_y;
    }

    public float get_partitioned_left_stick_x() {
        double theta = Math.atan2(gamepad.left_stick_y, gamepad.left_stick_x);
        if (Math.abs(theta - Math.PI / 2) < Math.PI / 6) {
            return 0;
        }

        return gamepad.left_stick_x;
    }

    public float get_partitioned_right_stick_y() {
        double theta = Math.atan2(gamepad.right_stick_y, gamepad.right_stick_x);
        if (Math.abs(theta) < Math.PI / 6) {
            return 0;
        }
        return gamepad.right_stick_y;
    }

    public float get_partitioned_right_stick_x() {
        double theta = Math.atan2(gamepad.right_stick_y, gamepad.right_stick_x);
        if (Math.abs(theta - Math.PI / 2) < Math.PI / 6) {
            return 0;
        }
        return gamepad.right_stick_x;
    }

    public double right_stick_angle() {
        return Math.atan2(-gamepad.right_stick_y, gamepad.right_stick_x);
    }

    public double left_stick_angle() {
        return Math.atan2(-gamepad.left_stick_y, gamepad.left_stick_x);
    }

    /**
     * Returns the octant of the right stick,
     * where 0 is from -pi/8 to pi/8, 1 is from pi/8 to 3pi/8, etc.
     *
     * @return the octant of the right stick
     */
    public int right_stick_octant() {
        if (gamepad.right_stick_x == 0 && gamepad.right_stick_y == 0) {
            return -1;
        }
        double shift = Math.PI / 8;
        double theta = right_stick_angle();
        if (theta < 0) {
            theta += 2 * Math.PI;
        }
        return (int) Math.ceil((theta + shift) / (Math.PI / 4) - 1) % 8;
    }

    /**
     * Returns the octant of the left stick,
     * where 0 is from -pi/8 to pi/8, 1 is from pi/8 to 3pi/8, etc.
     *
     * @return the octant of the left stick
     */
    public int left_stick_octant() {
        if (gamepad.left_stick_x == 0 && gamepad.left_stick_y == 0) {
            return -1;
        }
        double shift = Math.PI / 8;
        double theta = left_stick_angle();
        if (theta < 0) {
            theta += 2 * Math.PI;
        }
        return (int) Math.ceil((theta + shift) / (Math.PI / 4) - 1) % 8;
    }

    public boolean right_trigger_active() {
        return gamepad.right_trigger > 0;
    }

    public boolean left_trigger_active() {
        return gamepad.left_trigger > 0;
    }

    public void update() {
        dpad_up.update(this.gamepad);
        dpad_down.update(this.gamepad);
        dpad_left.update(this.gamepad);
        dpad_right.update(this.gamepad);
        a.update(this.gamepad);
        b.update(this.gamepad);
        x.update(this.gamepad);
        y.update(this.gamepad);
        left_bumper.update(this.gamepad);
        right_bumper.update(this.gamepad);
        left_stick_button.update(this.gamepad);
        right_stick_button.update(this.gamepad);
        left_trigger.update(this.gamepad);
        right_trigger.update(this.gamepad);
        guide.update(this.gamepad);
    }

}