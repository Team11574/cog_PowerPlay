package incognito.cog.actions;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class Action {
    ArrayList<ActionBase> actions = new ArrayList<>();
    int stage = 0;
    int index = -1;
    ElapsedTime timer = new ElapsedTime();

    public Action(Runnable function) {
        //this(function, () -> true);
        addAction(new ActionBase(function, () -> true, () -> true, 0));
        globalize();
    }

    private void globalize() {
        ActionManager.add(this);
    }

    public Action(ActionBase actionBase) {
        addAction(actionBase);
    }

    private void addAction(ActionBase action) {
        actions.add(action);
    }

    public Action then(Runnable function) {
        return then(new ActionBase(function, () -> true, () -> true, 0));
    }

    public Action then(ActionBase a) {
        addAction(a);
        return this;
    }

    public Action then(Action a) {
        for (ActionBase action : a.actions) {
            addAction(action);
        }
        return this;
    }

    private ActionBase getLastActionBase() {
        return actions.get(actions.size() - 1);
    }

    public Action until(Callable<Boolean> initialCondition) {
        // Consider what to do if this action is somehow empty SHOULD NEVER HAPPEN
        if (actions.size() == 0) return this;
        getLastActionBase().setInitialCondition(initialCondition);
        return this;
    }

    public Action when(Callable<Boolean> endCondition) {
        // Consider what to do if this action is somehow empty SHOULD NEVER HAPPEN
        if (actions.size() == 0) return this;
        getLastActionBase().setEndCondition(endCondition);
        return this;
    }

    public Action delay(double delay) {
        // Consider what to do if this action is somehow empty SHOULD NEVER HAPPEN
        if (actions.size() == 0) return this;
        getLastActionBase().setDelay(delay);
        return this;
    }

    public void run() {
        index = 0;
        timer.reset();
    }

    public void cancel() {
        index = -1;
        timer.reset();
    }

    public void update() {
        if (index >= actions.size()) {
            index = -1;
        }
        if (index > 0) {
            ActionBase currentAction = actions.get(index);
            try {
                switch (stage) {
                    case 0:
                        if (currentAction.initialCondition.call()) {
                            stage++;
                        }
                        break;
                    case 1:
                        currentAction.function.run();
                        stage++;
                        break;
                    case 2:
                        if (currentAction.delay.apply(timer.time())) {
                            stage++;
                        }
                        timer.reset();
                        break;
                    case 3:
                        if (currentAction.endCondition.call()) {
                            stage++;
                        }
                        break;
                    case 4:
                        stage = 0;
                        index++;
                        break;
                }
            } catch (Exception ignored) {
            }
        }
    }

    /*

        public Action(Runnable function, Callable<Boolean> initialCondition) {
            this(function, initialCondition, () -> true);
        }

        public Action(Runnable function, Callable<Boolean> initialCondition, Callable<Boolean> endCondition) {
            this(function, initialCondition, endCondition, 0);
        }


        public Action(Runnable function, Callable<Boolean> initialCondition, Callable<Boolean> endCondition, double delay) {
            addAction(new ActionBase(function, initialCondition, endCondition, delay));
        }

        public Action then(Runnable function, Callable<Boolean> condition) {
            return then(function, condition, 0);
        }
        public Action then(Runnable function, Callable<Boolean> condition, double delay) {
            return then(new ActionBase(function, condition, delay));
        }
        */
}

class ActionBase {
    Runnable function;
    Callable<Boolean> initialCondition;
    Callable<Boolean> endCondition;
    Function<Double, Boolean> delay;

    public ActionBase(Runnable function,
                      Callable<Boolean> initialCondition,
                      Callable<Boolean> endCondition,
                      double delay) {
        this(function, initialCondition, endCondition, (time) -> time >= delay);
    }
    public ActionBase(Runnable function,
                      Callable<Boolean> initialCondition,
                      Callable<Boolean> endCondition,
                      Function<Double, Boolean> delay) {
        this.function = function;
        this.initialCondition = initialCondition;
        this.endCondition = endCondition;
        this.delay = delay;
    }

    public void setInitialCondition(Callable<Boolean> initialCondition) {
        this.initialCondition = initialCondition;
    }

    public void setEndCondition(Callable<Boolean> endCondition) {
        this.endCondition = endCondition;
    }

    public void setDelay(double delay) {
        setDelay((time) -> time >= delay);
    }

    public void setDelay(Function<Double, Boolean> delay) {
        this.delay = delay;
    }
}