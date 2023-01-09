import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomExecutor extends ThreadPoolExecutor {

    private static int numOfCores = Runtime.getRuntime().availableProcessors();
    private int[] counts = { 0, 0, 0 };

    public CustomExecutor() {
        super(numOfCores / 2, numOfCores - 1, 300, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>());
    }

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

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        int priority = getCurrentMax();
        TaskType type = TaskType.IO;
        if (1 <= priority && priority <= 3)
            type.setPriority(priority);
        return Task.createTask(callable, type);
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        Task<T> task = Task.createTask(callable);
        return submit(task);
    }

    public <T> Future<T> submit(Callable<T> callable, TaskType type) {
        Task<T> task = Task.createTask(callable, type);
        return submit(task);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        int priority = getCurrentMax();
        if (1 <= priority && priority <= 3)
            counts[priority - 1]--;
    }

    public int getCurrentMax() {
        if (0 < counts[0])
            return 1;
        if (0 < counts[1])
            return 2;
        if (0 < counts[2])
            return 3;
        return 0;
    }

    public int[] getCounts() {
        return counts;
    }

    public int hashCode() {
        return counts.hashCode() * getQueue().hashCode();
    }

    public void gracefullyTerminate() {
        try {
            super.awaitTermination(3, TimeUnit.SECONDS);
            super.shutdown();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
