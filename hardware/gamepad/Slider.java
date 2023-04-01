package incognito.cog.hardware.gamepad;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import incognito.cog.actions.Action;

// Make this like button but for floats... DO LATER
public class Slider {
    ArrayList<Action> actions = new ArrayList<>();
    Callable<Boolean> updateFunction;
    Slider onRise;
    Slider onFall;
    Boolean value;
    Boolean lastValue;
    boolean temp;

    public Slider(Callable<Boolean> updateFunction) {
        this(updateFunction, true);
    }

    private Slider(Callable<Boolean> updateFunction, boolean coreButton) {
        this.updateFunction = updateFunction;
        if (coreButton) {
            this.onRise = new Slider(() -> this.value && !this.lastValue, false);
            this.onFall = new Slider(() -> this.lastValue && !this.value, false);
        }
    }

    public void update() {
        temp = value;
        try {
            value = updateFunction.call();
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

    public Slider onRise(Action action) {
        this.onRise.bind(action);
        return this;
    }

    public Slider onRise(Runnable runner) {
        return this.onRise(new Action(runner));
    }

    public Slider onFall(Action action) {
        this.onFall.bind(action);
        return this;
    }

    public Slider onFall(Runnable runner) {
        return this.onFall(new Action(runner));
    }

    public Slider bind(Action action) {
        actions.add(action);
        return this;
    }

    public Slider bind(Runnable runner) {
        return bind(new Action(runner));
    }
}
