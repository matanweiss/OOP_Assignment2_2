import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Task<T> extends FutureTask<T> implements Callable<T>, Comparable<Task<T>> {

    private TaskType type;
    private Callable<T> callable;

    private Task(Callable<T> callable) {
        this(TaskType.IO, callable);
    }

    private Task(TaskType type, Callable<T> callable) {
        super(callable);
        this.type = type;
        this.callable = callable;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Callable<T> getCallable() {
        return callable;
    }

    public void setCallable(Callable<T> callable) {
        this.callable = callable;
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
        int difference = o.type.getPriorityValue() - this.type.getPriorityValue();
        return Integer.signum(difference);
    }
}
