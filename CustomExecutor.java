import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomExecutor extends ThreadPoolExecutor {

    private static int numOfCores = Runtime.getRuntime().availableProcessors();

    public CustomExecutor() {
        super(numOfCores / 2, numOfCores - 1, 300, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>());
    }

    public <T> Future<T> submit(Task<T> task) {
        task.setIsWaiting(true);
        int priority = task.getType().getPriorityValue();
        Task.counts.put(priority, Task.counts.get(priority) + 1);
        if (task == null || task.getCallable() == null)
            throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
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

    public int getCurrentMax() {
        if (0 < Task.counts.get(1))
            return 1;
        if (0 < Task.counts.get(2))
            return 2;
        if (0 < Task.counts.get(3))
            return 3;
        return 0;
    }

    // @Override
    // protected void beforeExecute(Thread t, Runnable r) { }

    // }

    public static void main(String[] args) {
        Callable<Integer> callable1 = (() -> 1 * 9);
        Task<Integer> task1 = Task.createTask(callable1, TaskType.COMPUTATIONAL);
        CustomExecutor c = new CustomExecutor();
        Future<Integer> result = c.submit(task1);
        System.out.println(c.getCurrentMax());
        int res = 0;
        try {
            res = result.get();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(c.getCurrentMax());
        c.shutdown();
        System.out.println(Task.counts);
    }
}
