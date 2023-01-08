import java.util.HashMap;
import java.util.concurrent.Callable;

public class Task<T> implements Callable<T>, Comparable<Task<T>> {

    private TaskType type;
    private Callable<T> callable;
    private boolean isWaiting;
    public static HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
    static {
        counts.put(1, 0);
        counts.put(2, 0);
        counts.put(3, 0);
    }

    private Task(Callable<T> callable) {
        this(TaskType.IO, callable);
    }

    private Task(TaskType type, Callable<T> callable) {
        this.type = type;
        this.callable = callable;
        this.isWaiting = false;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        if (isWaiting) {
            int oldPriority = this.type.getPriorityValue();
            int newPriority = type.getPriorityValue();
            counts.put(oldPriority, counts.get(oldPriority) - 1);
            counts.put(newPriority, counts.get(newPriority) + 1);
        }
        this.type = type;
    }

    public Callable<T> getCallable() {
        return callable;
    }

    public void setCallable(Callable<T> callable) {
        this.callable = callable;
    }

    public boolean getIsWaiting() {
        return isWaiting;
    }

    public void setIsWaiting(boolean b) {
        isWaiting = b;
    }

    public int hashCode() {
        return type.getPriorityValue() * callable.hashCode();
    }

    public boolean equals(Task<T> other) {
        if (compareTo(other) == 0)
            return true;
        return false;
    }

    @Override
    public T call() throws Exception {
        isWaiting = false;
        int priority = type.getPriorityValue();
        counts.put(priority, counts.get(priority) - 1);
        return callable.call();
    }

    // factory method 1
    public static <T> Task<T> createTask(Callable<T> callable) {
        return new Task<T>(callable);
    }

    // factory method 2
    public static <T> Task<T> createTask(Callable<T> callable, TaskType type) {
        return new Task<T>(type, callable);
    }

    @Override
    public int compareTo(Task<T> o) {
        int difference = o.type.getPriorityValue() - type.getPriorityValue();
        return Integer.signum(difference);
    }
}
