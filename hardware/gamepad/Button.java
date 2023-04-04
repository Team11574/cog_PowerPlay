package incognito.cog.hardware.gamepad;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Function;

import incognito.cog.actions.Action;
import incognito.cog.util.TelemetryBigError;

public class Button {
    ArrayList<Action> actions = new ArrayList<>();
    Function<Gamepad, Boolean> coreFunction;
    Function<Button, Boolean> riseFallFunction;
    public Button onRise;
    public Button onFall;
    public Boolean value = false;
    public Boolean lastValue;
    boolean isRiseFall = false;
    boolean temp;

    public Button(Function<Gamepad, Boolean> updateFunction) {
        this.coreFunction = updateFunction;
        this.onRise = new Button((Button button) -> button.value && !button.lastValue, true);
        this.onFall = new Button((Button button) -> button.lastValue && !button.value, true);
    }

    private Button(Function<Button, Boolean> updateFunction, boolean isRiseFall) {
        this.riseFallFunction = updateFunction;
        this.isRiseFall = isRiseFall;
    }

    public void update(Gamepad gamepad) {
        temp = value;
        try {
            value = coreFunction.apply(gamepad);
            lastValue = temp;
            onRise.update(this);
            onFall.update(this);
        } catch (Exception e) {
            TelemetryBigError.raise(3, 100);
            value = temp;
        }
        if (value) {
            run();
        }
    }

    private void update(Button button) {
        temp = value;
        try {
            value = riseFallFunction.apply(button);
            lastValue = temp;
        } catch (Exception e) {
            value = temp;
        }
        if (value) {
            run();
        }
    }

    private void run() {
        for (Action action : actions) {
            action.run();
        }
    }

    public Button onRise(Action action) {
        this.onRise.bind(action);
        return this;
    }

    public Button onRise(Runnable runner) {
        return this.onRise(new Action(runner));
    }

    public Button onFall(Action action) {
        this.onFall.bind(action);
        return this;
    }

    public Button onFall(Runnable runner) {
        return this.onFall(new Action(runner));
    }

    public Button bind(Action action) {
        actions.add(action);
        return this;
    }

    public Button bind(Runnable runner) {
        return bind(new Action(runner));
    }
}
