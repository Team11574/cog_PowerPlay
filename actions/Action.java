package incognito.cog.actions;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Action {
    ArrayList<SubAction> actions = new ArrayList<>();
    public int index = -1;
    ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    boolean globalized = false;

    public Action() {}

    public Action(Runnable function) {
        addAction(new Runner(function));
    }

    public Action(SubAction subAction) {
        addAction(subAction);
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


    private Action addAction(SubAction subAction) {
        actions.add(subAction);
        return this;
    }

    private Action addAction(Action action) {
        for (SubAction subAction : action.actions) {
            addAction(subAction);
        }
        return this;
    }

    public Action then(Runnable function) {
        return then(new Runner(function));
    }

    public Action then(SubAction subAction) {
        return new Action(this).addAction(subAction);
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

    public Action doIf(SubAction action, Callable<Boolean> condition) {
        return new Action(this).addAction(new ConditionalRunner(action, condition));
    }

    public Action doIf(Action action, Callable<Boolean> condition) {
        return new Action(this).addAction(new ConditionalRunner(action, condition));
    }

    public void run() {
        if (actions.size() == 0) return;
        if (!globalized) globalize();
        index = 0;
        actions.get(0).start();
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
            SubAction currentAction = actions.get(index);
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

abstract class SubAction {
    public abstract boolean isFinished(double time);
    public void start() {}
    public void end() {}
}

class Runner extends SubAction {
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

class Condition extends SubAction {
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

class Delay extends SubAction {
    double delay;
    public Delay (double delay) {
        this.delay = delay;
    }
    public boolean isFinished(double time) {
        return time >= delay;
    }
}

class ConditionalRunner extends SubAction {
    Action action;
    Callable<Boolean> condition;
    boolean finished = false;
    public ConditionalRunner (Runnable runnable, Callable<Boolean> condition) {
        this(new Action(runnable), condition);
    }

    public ConditionalRunner (SubAction action, Callable<Boolean> condition) {
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