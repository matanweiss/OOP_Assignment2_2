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
        if (task == null)
            throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task.getCallable());
        execute(ftask);
        return ftask;
    }

    public static void main(String[] args) {
        Callable<Integer> callable1 = (() -> 1 * 9);
        Task<Integer> task1 = new Task<>(callable1);
        CustomExecutor c = new CustomExecutor();
        Future<Integer> result = c.submit(task1);
        int res = 0;
        try {
            res = result.get();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(res);
        c.shutdown();
    }
}
