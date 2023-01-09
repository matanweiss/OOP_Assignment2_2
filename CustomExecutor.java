import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomExecutor extends ThreadPoolExecutor {

    private static int numOfCores = Runtime.getRuntime().availableProcessors();
    private HashMap<Integer, Integer> counts;

    public CustomExecutor() {
        super(numOfCores / 2, numOfCores - 1, 300, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>());
        counts = new HashMap<Integer, Integer>();
        counts.put(1, 0);
        counts.put(2, 0);
        counts.put(3, 0);
    }

    public <T> Future<T> submit(Task<T> task) {
        int priority = task.getType().getPriorityValue();
        counts.put(priority, counts.get(priority) + 1);
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
        counts.put(priority, counts.get(priority) - 1);
    }

    public int getCurrentMax() {
        if (0 < counts.get(1))
            return 1;
        if (0 < counts.get(2))
            return 2;
        if (0 < counts.get(3))
            return 3;
        return 0;
    }

    public HashMap<Integer, Integer> getCounts() {
        return counts;
    }

    public int hashCode() {
        return counts.hashCode() * getQueue().hashCode();
    }

    public static void main(String[] args) {
        // Comparator c1 = (Task<T> task1, Task<T> task2) -> task1.compareTo(task2);
        Callable<Integer> callable1 = (() -> 1 * 9);
        Task<Integer> task1 = Task.createTask(callable1, TaskType.OTHER);
        Task<Integer> task2 = Task.createTask(callable1, TaskType.IO);
        Task<Integer> task3 = Task.createTask(callable1, TaskType.OTHER);
        Task<Integer> task4 = Task.createTask(callable1, TaskType.OTHER);
        Task<Integer> task5 = Task.createTask(callable1, TaskType.OTHER);
        Task<Integer> task6 = Task.createTask(callable1, TaskType.OTHER);
        CustomExecutor c = new CustomExecutor();
        Future<Integer> result1 = c.submit(task1);
        Future<Integer> result2 = c.submit(task2);
        Future<Integer> result3 = c.submit(task3);
        Future<Integer> result4 = c.submit(task4);
        Future<Integer> result5 = c.submit(task5);
        Future<Integer> result6 = c.submit(task6);
        System.out.println(c.getCurrentMax());
        System.out.println(c.getCounts());
        System.out.println(c.getCurrentMax());
        int res = 0;
        try {
            res = result1.get();
            res = result2.get();
            res = result3.get();
            res = result4.get();
            res = result5.get();
            res = result6.get();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("result: " + res);
        System.out.println(c.getCurrentMax());
        c.shutdown();
        System.out.println(c.getCounts());
    }
}
