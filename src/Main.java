import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        ExecutorService threadPool = Executors.newCachedThreadPool();
        // newCachedThreadPool() Показал себя быстрее чем фиксированное кол-во потоков, хоть и бьет по производительности
        List<Thread> threads = new ArrayList<>();

            for (String text : texts) {
                threadPool.submit(() -> {
                    int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }

                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);

                    CompletableFuture.allOf(threads.toArray(new CompletableFuture[0])).join();
                    threadPool.shutdown();

                });

            }

        // Пришлось обернуть конечную часть таймера для верности измерений
        if(threadPool.awaitTermination(10, TimeUnit.MINUTES)){
            long endTs = System.currentTimeMillis(); // end time
            System.out.println("Time: " + (endTs - startTs) + "ms");
        }else {
            System.out.println("!");
        }

    }
    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}