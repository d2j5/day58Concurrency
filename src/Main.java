import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SharedBuffer {
    private final List<Integer> buffer;
    private final int maxSize;

    public SharedBuffer(int maxSize) {
        this.buffer = new ArrayList<>();
        this.maxSize = maxSize;
    }

    public synchronized void produce(int number) throws InterruptedException {
        while (buffer.size() == maxSize) {
            // Buffer is full, wait for consumer to remove elements
            wait();
        }

        buffer.add(number);
        System.out.println("Producer produced: " + number);
        notifyAll();
    }

    public synchronized int consume() throws InterruptedException {
        while (buffer.isEmpty()) {
            // Buffer is empty, wait for producer to add elements
            wait();
        }

        int number = buffer.remove(0);
        System.out.println("Consumer consumed: " + number);
        notifyAll();

        return number;
    }
}

class Producer implements Runnable {
    private SharedBuffer sharedBuffer;
    private int maxIterations;

    public Producer(SharedBuffer sharedBuffer, int maxIterations) {
        this.sharedBuffer = sharedBuffer;
        this.maxIterations = maxIterations;
    }

    @Override
    public void run() {
        Random random = new Random();
        try {
            int iterations = 0;
            while (iterations < maxIterations) {
                int number = random.nextInt(100);
                sharedBuffer.produce(number);
                Thread.sleep(1000); // Sleep for some time before producing the next number
                iterations++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {
    private SharedBuffer sharedBuffer;
    private int sum;
    private int maxIterations;

    public Consumer(SharedBuffer sharedBuffer, int maxIterations) {
        this.sharedBuffer = sharedBuffer;
        this.sum = 0;
        this.maxIterations = maxIterations;
    }

    @Override
    public void run() {
        try {
            int iterations = 0;
            while (iterations < maxIterations) {
                int number = sharedBuffer.consume();
                sum += number;
                System.out.println("Consumer current sum: " + sum);
                Thread.sleep(2000); // Sleep for some time before consuming the next number
                iterations++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        int bufferSize = 5;
        SharedBuffer sharedBuffer = new SharedBuffer(bufferSize);

        int maxIterations = 10; //Modify as desired.

        Producer producer = new Producer(sharedBuffer, maxIterations);
        Consumer consumer = new Consumer(sharedBuffer, maxIterations);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();
    }


}