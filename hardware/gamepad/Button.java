package incognito.cog.hardware.gamepad;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import incognito.cog.actions.Action;

public class Button {
    ArrayList<Action> actions = new ArrayList<>();
    Callable<Boolean> updateFunction;
    Button onRise;
    Button onFall;
    Boolean value;
    Boolean lastValue;
    boolean temp;

    public Button(Callable<Boolean> updateFunction) {
        this(updateFunction, true);
    }

    private Button(Callable<Boolean> updateFunction, boolean coreButton) {
        this.updateFunction = updateFunction;
        if (coreButton) {
            this.onRise = new Button(() -> this.value && !this.lastValue, false);
            this.onFall = new Button(() -> this.lastValue && !this.value, false);
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
