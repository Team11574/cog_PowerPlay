package incognito.cog.actions;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class Action {
    ArrayList<ActionType> actions = new ArrayList<>();
    public int index = -1;
    ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

    public Action() {
        globalize();
    }

    public Action(Runnable function) {
        addAction(new Runner(function));
        globalize();
    }

    public Action(ActionType actionType) {
        addAction(actionType);
        globalize();
    }

    public Action(Action action) {
        addAction(action);
        globalize();
    }

    private void globalize() {
        ActionManager.add(this);
    }


    private Action addAction(ActionType actionType) {
        actions.add(actionType);
        return this;
    }

    private Action addAction(Action action) {
        for (ActionType actionType : action.actions) {
            addAction(actionType);
        }
        return this;
    }

    public Action then(Runnable function) {
        return then(new Runner(function));
    }

    public Action then(ActionType actionType) {
        return addAction(actionType);
    }

    public Action then(Action a) {
        return addAction(a);
    }

    public Action until(Callable<Boolean> initialCondition) {
        return addAction(new Condition(initialCondition));
    }

    public Action when(Callable<Boolean> endCondition) {
        return until(endCondition);
    }

    public Action delay(double delay) {
        return addAction(new Delay(delay));
    }

    public Action waitFor(Action action) {
        return addAction(new Condition(() -> action.index == -1));
    }

    public void run() {
        index = 0;
        timer.reset();
    }

    public void cancel() {
        index = -1;
        timer.reset();
    }

    public boolean isActive() {
        return index >= 0;
    }

    public void update() {
        if (index >= actions.size()) {
            index = -1;
        }
        if (index >= 0) {
            ActionType currentAction = actions.get(index);
            switch (currentAction.type()) {
                case RUNNER:
                    ((Runner) currentAction).run();
                    index++;
                    timer.reset();
                    break;
                case CONDITION:
                    if (((Condition) currentAction).call()) {
                        index++;
                        timer.reset();
                    }
                    break;
                case DELAY:
                    if (((Delay) currentAction).apply(timer.time())) {
                        index++;
                        timer.reset();
                    }
                    break;
            }
        }
    }
}

abstract class ActionType {
    public enum TYPE {
        RUNNER,
        CONDITION,
        DELAY
    }

    public abstract TYPE type();
}
class Runner extends ActionType {
    Runnable function;
    public Runner (Runnable function) {
        this.function = function;
    }

    public void run() {
        function.run();
    }

    public TYPE type() {
        return TYPE.RUNNER;
    }
}

class Condition extends ActionType {
    Callable<Boolean> condition;
    public Condition (Callable<Boolean> condition) {
        this.condition = condition;
    }
    public boolean call() {
        try {
            return condition.call();
        } catch (Exception ignored) {
            return false;
        }
    }
    public TYPE type() {
        return TYPE.CONDITION;
    }
}

class Delay extends ActionType {
    double delay;
    public Delay (double delay) {
        this.delay = delay;
    }
    public boolean apply(double time) {
        return time >= delay;
    }
    public TYPE type() {
        return TYPE.DELAY;
    }
}