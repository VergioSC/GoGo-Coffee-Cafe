import java.util.concurrent.Semaphore;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Cafe {

    // --- Drink names ---
    public static final String DRINK_CAPPUCCINO = "Cappuccino";
    public static final String DRINK_ESPRESSO   = "Espresso";
    public static final String DRINK_JUICE      = "Juice";

    // --- Machines (only 1 of each; fair=true prevents barista starvation) ---
    public final Semaphore espressoMachine = new Semaphore(1, true);
    public final Semaphore milkFrother     = new Semaphore(1, true);
    public final Semaphore juiceTap        = new Semaphore(1, true);

    // --- Seating (5 tables x 2 chairs = 10 chairs total) ---
    public final Semaphore chairs = new Semaphore(10, true);

    // --- Waiting queue (FIFO - longest waiting goes first) ---
    public final LinkedBlockingQueue<Customer> waitingQueue = new LinkedBlockingQueue<>();

    // --- Tracks how many people are currently waiting to order ---
    public final AtomicInteger waitingCount = new AtomicInteger(0);

    // --- Sales tracking ---
    public final AtomicInteger cappuccinoCount = new AtomicInteger(0);
    public final AtomicInteger espressoCount = new AtomicInteger(0);
    public final AtomicInteger juiceCount = new AtomicInteger(0);
    public final AtomicInteger totalSales = new AtomicInteger(0);

    // --- Max waiting allowed before customer leaves ---
    public static final int MAX_WAITING = 5;

    // --- Drink prices ---
    public static final int CAPPUCCINO_PRICE = 9;
    public static final int ESPRESSO_PRICE = 6;
    public static final int JUICE_PRICE = 7;
    public static long startTime = System.currentTimeMillis();
}