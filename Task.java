import java.util.concurrent.Callable;

public class Task<T> implements Callable<T>, Comparable<Task<T>> {

    private TaskType type;
    private Callable<T> callable;

    public Task(Callable<T> callable) {
        this(TaskType.IO, callable);
    }

    public Task(TaskType type, Callable<T> callable) {
        this.type = type;
        this.callable = callable;
    }

    public Callable<T> getCallable() {
        return callable;
    }

    @Override
    public T call() {
        try {
            return callable.call();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // factory method 1
    public static <T> Task<T> createTask(Callable<T> callable) {
        return new Task<T>(callable);
    }

    // factory method 2
    public static <T> Task<T> createTask(TaskType type, Callable<T> callable) {
        return new Task<T>(type, callable);
    }

    @Override
    public int compareTo(Task<T> o) {
        int difference = o.type.getPriorityValue() - type.getPriorityValue();
        return Integer.signum(difference);
    }
}
