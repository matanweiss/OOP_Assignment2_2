import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomExecutor extends ThreadPoolExecutor {

    private static int numOfCores = Runtime.getRuntime().availableProcessors();
    private int[] counts = { 0, 0, 0 };

    /**
     * Creates a new CustomExecutor by calling ThreadPoolExecutor's constructor
     */
    public CustomExecutor() {
        super(numOfCores / 2, numOfCores - 1, 300, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>());
    }

    /**
     * Submits a task to the PriorityBlockingQueue
     * Also checking for the Task's type and updating counts for getCurrentMax()
     * 
     * @param <T>  the Task's return type
     * @param task the Task
     * @return a RunnableFuture<T> that has been executed
     */
    public <T> Future<T> submit(Task<T> task) {
        int priority = 0;
        String s = task.getType().toString();
        if (s == "Computationl Task")
            priority = 1;
        if (s == "IO-Bound Task")
            priority = 2;
        if (s == "Unknown Task")
            priority = 3;
        if (1 <= priority && priority <= 3)
            counts[priority - 1]++;
        if (task == null || task.getCallable() == null)
            throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }

    /**
     * Creating a Task from a Callable
     * checking it's priority by getCurrentMax()
     * 
     * @return the Task that the method created
     */
    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        int priority = getCurrentMax();
        TaskType type = TaskType.IO;
        if (1 <= priority && priority <= 3)
            type.setPriority(priority);
        return Task.createTask(callable, type);
    }

    /**
     * Creating a Task with a default priority from Callable and calling submit()
     * with the said task
     */
    /** */
    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        Task<T> task = Task.createTask(callable);
        return submit(task);
    }

    /**
     * Creating a Task with from Callable and a TaskType, then calling submit()
     * 
     * @param <T>
     * @param callable
     * @param type
     * @return a RunnableFuture<T> that has been executed
     */
    public <T> Future<T> submit(Callable<T> callable, TaskType type) {
        Task<T> task = Task.createTask(callable, type);
        return submit(task);
    }

    /**
     * This method is called before a Task is executed
     * This method lowers the count of the said Task type
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        int priority = getCurrentMax();
        if (1 <= priority && priority <= 3)
            counts[priority - 1]--;
    }

    /**
     * This method return an Integer representing the highest priority of a Task in
     * PriorityBlockingQueue
     * This method performs a constant number of operations so its time complexity
     * is O(1)
     * 
     * @return An Integer
     * 
     */
    public int getCurrentMax() {
        if (0 < counts[0])
            return 1;
        if (0 < counts[1])
            return 2;
        if (0 < counts[2])
            return 3;
        return 0;
    }

    /**
     * 
     * @return counts array
     */
    public int[] getCounts() {
        return counts;
    }

    public int hashCode() {
        return counts.hashCode() * getQueue().hashCode();
    }

    /**
     * This method terminates the CustomExecutor by not allowing new Tasks to be
     * submitted, executing the Tasks left in the PriorityBlockingQueue and the
     * Tasks that being executed currently
     */
    public void gracefullyTerminate() {
        try {
            super.awaitTermination(3, TimeUnit.SECONDS);
            super.shutdown();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
