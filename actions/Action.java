package incognito.cog.actions;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Action {
    ArrayList<ActionType> actions = new ArrayList<>();
    public int index = -1;
    ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    boolean globalized = false;

    public Action() {}

    public Action(Runnable function) {
        addAction(new Runner(function));
    }

    public Action(ActionType actionType) {
        addAction(actionType);
    }

    public Action(Action action) {
        addAction(action);
    }

    public Action globalize() {
        if (!globalized) {
            ActionManager.add(this);
            globalized = true;
        }
        return this;
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
        return new Action(this).addAction(actionType);
    }

    public Action then(Action a) {
        return new Action(this).addAction(a);
    }

    public Action until(Callable<Boolean> initialCondition) {
        return new Action(this).addAction(new Condition(initialCondition));
    }

    public Action when(Callable<Boolean> endCondition) {
        return until(endCondition);
    }

    public Action delay(double delay) {
        return new Action(this).addAction(new Delay(delay));
    }

    public Action waitFor(Action action) {
        return new Action(this).addAction(new Condition(() -> action.index == -1));
    }

    public Action doIf(Runnable function, Callable<Boolean> condition) {
        return new Action(this).addAction(new ConditionalRunner(function, condition));
    }

    public Action doIf(ActionType action, Callable<Boolean> condition) {
        return new Action(this).addAction(new ConditionalRunner(action, condition));
    }

    public Action doIf(Action action, Callable<Boolean> condition) {
        return new Action(this).addAction(new ConditionalRunner(action, condition));
    }

    public void run() {
        if (!globalized) globalize();
        index = 0;
        timer.reset();
    }

    public void cancel() {
        if (!globalized) globalize();
        index = -1;
        timer.reset();
    }

    public boolean isActive() {
        return index >= 0;
    }

    public void update() {
        if (index >= 0) {
            ActionType currentAction = actions.get(index);
            if (currentAction.isFinished(timer.time())) {
                // End the current action
                currentAction.end();
                index++;
                timer.reset();
                // Start the next action if need be
                if (index < actions.size()) {
                    actions.get(index).start();
                } else {
                    index = -1;
                }
            }
        }
    }
}

abstract class ActionType {
    public abstract boolean isFinished(double time);
    public void start() {};
    public void end() {};
}

class Runner extends ActionType {
    Runnable function;
    public Runner (Runnable function) {
        this.function = function;
    }

    public void run() {
        function.run();
    }

    public boolean isFinished(double time) {
        run();
        return true;
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

    public boolean isFinished(double time) {
        return call();
    }
}

class Delay extends ActionType {
    double delay;
    public Delay (double delay) {
        this.delay = delay;
    }
    public boolean isFinished(double time) {
        return time >= delay;
    }
}

class ConditionalRunner extends ActionType {
    Action action;
    Callable<Boolean> condition;
    boolean finished = false;
    public ConditionalRunner (Runnable runnable, Callable<Boolean> condition) {
        this(new Action(runnable), condition);
    }

    public ConditionalRunner (ActionType action, Callable<Boolean> condition) {
        this(new Action(action), condition);
    }

    public ConditionalRunner (Action action, Callable<Boolean> condition) {
        this.action = action;
        this.action.globalize();
        this.condition = condition;
    }

    public void run() {
        action.run();
    }

    public boolean call() {
        try {
            return condition.call();
        } catch (Exception ignored) {
            return false;
        }
    }

    public void start() {
        finished = false;
        if (call()) {
            run();
        } else {
            finished = true;
        }
    }

    public boolean isFinished(double time) {
        finished = finished || !action.isActive();
        return finished;
    }
}