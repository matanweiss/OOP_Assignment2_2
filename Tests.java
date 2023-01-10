import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.*;

public class Tests {
    public static final Logger logger = LoggerFactory.getLogger(Tests.class);

    @Test
    public void myTest() {
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
        Task<String> task7 = Task.createTask(callable2, TaskType.COMPUTATIONAL);
        Task<String> task8 = Task.createTask(callable2, TaskType.COMPUTATIONAL);
        Task<String> task9 = Task.createTask(callable2, TaskType.COMPUTATIONAL);
        Task<String> task10 = Task.createTask(callable2, TaskType.COMPUTATIONAL);
        Task<String> task11 = Task.createTask(callable2, TaskType.COMPUTATIONAL);
        task2.getType().setPriority(2);
        System.out.println(task1.getType().getPriorityValue());
        System.out.println(task2.getType().getPriorityValue());
        Future<String> result1 = c.submit(task1);
        Future<String> result2 = c.submit(task2);
        Future<String> result3 = c.submit(task3);
        Future<String> result4 = c.submit(task4);
        Future<String> result5 = c.submit(task5);
        Future<String> result6 = c.submit(task6);
        c.submit(task7);
        c.submit(task8);
        c.submit(task9);
        c.submit(task10);
        c.submit(task11);
        logger.info(() -> String.valueOf(c.getCurrentMax()));
        logger.info(() -> Arrays.toString(c.getCounts()));
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            logger.error(() -> e.getMessage());
        }
        logger.info(() -> String.valueOf(c.getCurrentMax()));
        logger.info(() -> Arrays.toString(c.getCounts()));
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            logger.error(() -> e.getMessage());
        }
        logger.info(() -> String.valueOf(c.getCurrentMax()));
        logger.info(() -> Arrays.toString(c.getCounts()));
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            logger.error(() -> e.getMessage());
        }
        logger.info(() -> String.valueOf(c.getCurrentMax()));
        logger.info(() -> Arrays.toString(c.getCounts()));
        try {
            System.out.println(result1.get());
            System.out.println(result2.get());
            System.out.println(result3.get());
            System.out.println(result4.get());
            System.out.println(result5.get());
            System.out.println(result6.get());
        } catch (Exception e) {
            logger.error(() -> e.getMessage());
        }
        c.gracefullyTerminate();
        logger.info(() -> String.valueOf(c.getCurrentMax()));
        logger.info(() -> Arrays.toString(c.getCounts()));

    }

    @Test
    public void partialTest() {
        CustomExecutor customExecutor = new CustomExecutor();
        var task = Task.createTask(() -> {
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
            }
            return sum;
        }, TaskType.COMPUTATIONAL);
        var sumTask = customExecutor.submit(task);
        final int sum;
        try {
            sum = sumTask.get(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        logger.info(() -> "Sum of 1 through 10 = " + sum);
        Callable<String> callable2 = () -> {
            StringBuilder sb = new StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            return sb.reverse().toString();
        };
        // var is used to infer the declared type automatically
        var priceTask = customExecutor.submit(() -> {
            return 1000 * Math.pow(1.02, 5);
        }, TaskType.COMPUTATIONAL);
        var reverseTask = customExecutor.submit(callable2, TaskType.IO);
        customExecutor.submit(callable2, TaskType.IO);
        logger.info(() -> "Current maximum priority = " + customExecutor.getCurrentMax());
        final Double totalPrice;
        final String reversed;
        try {
            totalPrice = priceTask.get();
            reversed = reverseTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        logger.info(() -> "Reversed String = " + reversed);
        logger.info(() -> String.valueOf("Total Price = " + totalPrice));
        logger.info(() -> "Current maximum priority = " + customExecutor.getCurrentMax());
        customExecutor.gracefullyTerminate();
    }
}