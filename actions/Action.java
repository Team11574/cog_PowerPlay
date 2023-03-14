package incognito.cog.actions;

import incognito.cog.callbacks.CallbackManagerRunnable;
import incognito.cog.callbacks.Event;

public class Action {
    public boolean resolved = false;
    private final Runnable action;
    public String name;

    private final CallbackManagerRunnable<Event.Default> callbacks = new CallbackManagerRunnable<Event.Default>(Event.Default.values());

    Action(String name, Runnable action) {
        this.name = name;
        this.action = action;
    }

    public void run() {
        action.run();
        resolved = true;
        dispatchCallback(Event.Default.RESOLVED);
    }

    // Method to see if it's completed
    public boolean resolved() {
        return resolved;
    }

    protected void resolve() {
        this.resolved = true;
        dispatchCallback(Event.Default.RESOLVED);
    }

    public void registerCallback(Event.Default event, Runnable callback) {
        callbacks.registerCallback(event, callback);
    }

    public void dispatchCallback(Event.Default event) {
        callbacks.dispatchCallback(event);
    }

}