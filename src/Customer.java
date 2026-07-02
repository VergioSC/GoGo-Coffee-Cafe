import java.util.Random;

public class Customer implements Runnable {

    private final int id;
    private final Cafe cafe;
    private final String drinkOrder;
    public volatile boolean isBeingServed = false;
    public volatile boolean drinkReady = false;
    private static final Random random = new Random();

    private static final String[] DRINKS = {
        Cafe.DRINK_CAPPUCCINO, Cafe.DRINK_CAPPUCCINO, Cafe.DRINK_CAPPUCCINO,
        Cafe.DRINK_CAPPUCCINO, Cafe.DRINK_CAPPUCCINO, Cafe.DRINK_CAPPUCCINO,
        Cafe.DRINK_CAPPUCCINO, Cafe.DRINK_ESPRESSO, Cafe.DRINK_ESPRESSO, Cafe.DRINK_JUICE
    };

    public Customer(int id, Cafe cafe) {
        this.id = id;
        this.cafe = cafe;
        this.drinkOrder = DRINKS[random.nextInt(10)];
    }

    @Override
    public void run() {
        log("has arrived at the cafe.");

        // Atomic check-then-enter: prevents more than MAX_WAITING customers from queuing
        synchronized (cafe) {
            if (cafe.waitingCount.get() >= Cafe.MAX_WAITING) {
                log("sees too many people waiting. Leaving the cafe!");
                return;
            }
            cafe.waitingCount.incrementAndGet();
            cafe.waitingQueue.add(this);
            cafe.notifyAll();
        }

        log("has entered the cafe and is waiting to order. (Waiting: " + cafe.waitingCount.get() + ")");

        // Wait until a barista picks this customer, or leave if too tired
        long waitLimit = (random.nextInt(6) + 5) * 1000; // 5-10 seconds
        synchronized (this) {
            long waitedSoFar = 0;
            long start = System.currentTimeMillis();
            while (!isBeingServed && waitedSoFar < waitLimit) {
                try {
                    wait(waitLimit - waitedSoFar);
                    waitedSoFar = System.currentTimeMillis() - start;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            if (!isBeingServed) {
                // Try to remove ourselves from the queue atomically.
                // If remove() returns true  → barista hasn't polled us yet; we leave safely.
                // If remove() returns false → barista already polled us but hasn't set
                //   isBeingServed yet; stay and wait for the signal so we aren't orphaned.
                boolean selfRemoved = cafe.waitingQueue.remove(this);
                if (selfRemoved) {
                    cafe.waitingCount.decrementAndGet();
                    log("has been waiting too long and is leaving! (Tired of waiting)");
                    return;
                }
                while (!isBeingServed) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }

        log("is giving order: " + drinkOrder + ". Waiting for drink...");

        // Wait for drink to be ready
        synchronized (this) {
            while (!drinkReady) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        log("drink is ready! Going to find a seat.");

        // Get a seat
        try {
            cafe.chairs.acquire();
            log("has found a seat and is now drinking their " + drinkOrder + ". Yum!");

            int drinkTime = (random.nextInt(4) + 3) * 1000;
            Thread.sleep(drinkTime);

            log("has finished their " + drinkOrder + " and is leaving. Bye!");
            cafe.chairs.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getDrinkOrder() {
        return drinkOrder;
    }

    public int getId() {
        return id;
    }

    private void log(String message) {
        long time = (System.currentTimeMillis() - Cafe.startTime) / 1000;
        System.out.println(Thread.currentThread().getName() + ": " + time + "s - Customer " + id + " " + message);
    }
}