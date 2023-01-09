import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Task<T> extends FutureTask<T> implements Callable<T>, Comparable<Task<T>> {

    private TaskType type;
    private Callable<T> callable;

    /**
     * Creates a Task with a default priority
     * 
     * @param callable The Task to be performed
     */
    private Task(Callable<T> callable) {
        this(TaskType.IO, callable);
    }

    /**
     * Creates a Task
     * 
     * @param type     The task's priority
     * @param callable The Task to be performed
     */
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

    /**
     * This method is used to execute the task
     * 
     * @return A Future object that will get
     *         the return value
     */
    @Override
    public T call() throws Exception {
        return callable.call();
    }

    /**
     * First factory method
     * Creates a Task with a default priority
     * 
     * @param <T>      The return type of the Task
     * @param callable The task to be performed
     * @return The created Task object
     */
    public static <T> Task<T> createTask(Callable<T> callable) {
        return new Task<T>(callable);
    }

    /**
     * Second factory method
     * 
     * @param <T>      The return type of the Task
     * @param callable The task to be performed
     * @param type     The Task's priority
     * @return The created Task object
     */
    public static <T> Task<T> createTask(Callable<T> callable, TaskType type) {
        return new Task<T>(type, callable);
    }

    /**
     * Allows the Task class to be comparable to other Tasks, so the
     * PriorityBlockingQueue is able to sort them by priority
     */
    @Override
    public int compareTo(Task<T> o) {
        int difference = o.type.getPriorityValue() - this.type.getPriorityValue();
        return Integer.signum(difference);
    }
}
