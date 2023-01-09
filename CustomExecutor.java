import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomExecutor extends ThreadPoolExecutor {

    private static int numOfCores = Runtime.getRuntime().availableProcessors();
    private int[] counts = {0, 0, 0};

    public CustomExecutor() {
        super(numOfCores / 2, numOfCores - 1, 300, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>());
    }

    public <T> Future<T> submit(Task<T> task) {
        int priority = 0;
        String s = task.getType().toString();
        if (s == "Computationl Task") priority = 1;
        if (s == "IO-Bound Task") priority = 2;
        if (s == "Unknown Task") priority = 3;
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

    public static void main(String[] args) {
        CustomExecutor c = new CustomExecutor();
        Callable<String> callable1 = (() -> {
            Thread.sleep(1000);
            return "OTHER";
        });
        Callable<String> callable2 = (() -> {
            Thread.sleep(1000);
            return "COMPUTATIONAL";
        });
        Task<String> task1 = Task.createTask(callable1, TaskType.OTHER);
        Task<String> task2 = Task.createTask(callable1, TaskType.OTHER);
        Task<String> task3 = Task.createTask(callable1, TaskType.OTHER);
        Task<String> task4 = Task.createTask(callable1, TaskType.OTHER);
        Task<String> task5 = Task.createTask(callable1, TaskType.OTHER);
        Task<String> task6 = Task.createTask(callable1, TaskType.OTHER);
        Task<String> task7 = Task.createTask(callable1, TaskType.COMPUTATIONAL);
        Task<String> task8 = Task.createTask(callable1, TaskType.COMPUTATIONAL);
        Task<String> task9 = Task.createTask(callable1, TaskType.COMPUTATIONAL);
        Task<String> task10 = Task.createTask(callable1, TaskType.COMPUTATIONAL);
        Task<String> task11 = Task.createTask(callable1, TaskType.COMPUTATIONAL);
        task2.getType().setPriority(2);
        System.out.println(task1.getType().getPriorityValue());
        System.out.println(task2.getType().getPriorityValue());
        Future<String> result1 = c.submit(task1);
        Future<String> result2 = c.submit(task2);
        Future<String> result3 = c.submit(task3);
        Future<String> result4 = c.submit(task4);
        Future<String> result5 = c.submit(task5);
        Future<String> result6 = c.submit(task6);
        Future<String> result7 = c.submit(task7);
        Future<String> result8 = c.submit(task8);
        Future<String> result9 = c.submit(task9);
        Future<String> result10 = c.submit(task10);
        Future<String> result11 = c.submit(task11);
        System.out.println(c.getCurrentMax());
        System.out.println(Arrays.toString(c.getCounts()));
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(c.getCurrentMax());
        System.out.println(Arrays.toString(c.getCounts()));
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(c.getCurrentMax());
        System.out.println(Arrays.toString(c.getCounts()));
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(c.getCurrentMax());
        System.out.println(Arrays.toString(c.getCounts()));
//        System.out.println(Arrays.toString(c.getCounts()));
//        System.out.println(c.getCurrentMax());
        int res = 0;
        try {
            System.out.println(result1.get());
            System.out.println(result2.get());
            System.out.println(result3.get());
            System.out.println(result4.get());
            System.out.println(result5.get());
            System.out.println(result6.get());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        System.out.println("result: " + res);
//        System.out.println(c.getCurrentMax());
        c.gracefullyTerminate();
        System.out.println(Arrays.toString(c.getCounts()));
    }
}
