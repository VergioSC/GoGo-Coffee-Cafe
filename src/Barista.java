public class Barista implements Runnable {

    private final int id;
    private final Cafe cafe;
    private volatile boolean running = true;

    public Barista(int id, Cafe cafe) {
        this.id = id;
        this.cafe = cafe;
    }

    @Override
    public void run() {
        log("is ready for work!");

        while (running) {
            Customer customer = null;

            // --- Step 1: Wait for a customer in the queue ---
            synchronized (cafe) {
                while (cafe.waitingQueue.isEmpty() && running) {
                    log("is sleeping... waiting for a customer.");
                    try {
                        cafe.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // --- Step 2: Take the next customer (FIFO) ---
                customer = cafe.waitingQueue.poll();
            }

            if (customer == null) continue;

            log("is now taking order from Customer " + customer.getId() + " who ordered a " + customer.getDrinkOrder());

            // --- Step 3: Wake up the customer and free their waiting slot ---
            synchronized (customer) {
                customer.isBeingServed = true;
                cafe.waitingCount.decrementAndGet();
                customer.notifyAll();
            }

            // --- Step 4: Prepare the drink ---
            try {
                prepareDrink(customer);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            // --- Step 5: Notify customer drink is ready ---
            synchronized (customer) {
                customer.drinkReady = true;
                customer.notifyAll();
            }

            log("has finished serving Customer " + customer.getId() + ". Next customer please!");
        }

        log("is done for the day. Goodnight!");
    }

    private void prepareDrink(Customer customer) throws InterruptedException {
        String drink = customer.getDrinkOrder();

        if (drink.equals(Cafe.DRINK_CAPPUCCINO)) {
            log("is acquiring Espresso Machine for Customer " + customer.getId() + "...");
            cafe.espressoMachine.acquire();
            log("acquired Espresso Machine! Now acquiring Milk Frother...");
            cafe.milkFrother.acquire();
            log("acquired Milk Frother! Preparing Cappuccino for Customer " + customer.getId() + "...");

            Thread.sleep(2000);

            log("releasing Espresso Machine and Milk Frother.");
            cafe.espressoMachine.release();
            cafe.milkFrother.release();

            cafe.cappuccinoCount.incrementAndGet();
            cafe.totalSales.addAndGet(Cafe.CAPPUCCINO_PRICE);

        } else if (drink.equals(Cafe.DRINK_ESPRESSO)) {
            log("is acquiring Espresso Machine for Customer " + customer.getId() + "...");
            cafe.espressoMachine.acquire();
            log("acquired Espresso Machine! Preparing Espresso for Customer " + customer.getId() + "...");

            Thread.sleep(1500);

            log("releasing Espresso Machine.");
            cafe.espressoMachine.release();

            cafe.espressoCount.incrementAndGet();
            cafe.totalSales.addAndGet(Cafe.ESPRESSO_PRICE);

        } else if (drink.equals(Cafe.DRINK_JUICE)) {
            log("is acquiring Juice Tap for Customer " + customer.getId() + "...");
            cafe.juiceTap.acquire();
            log("acquired Juice Tap! Preparing Juice for Customer " + customer.getId() + "...");

            Thread.sleep(1000);

            log("releasing Juice Tap.");
            cafe.juiceTap.release();

            cafe.juiceCount.incrementAndGet();
            cafe.totalSales.addAndGet(Cafe.JUICE_PRICE);
        }

        log("drink is ready for Customer " + customer.getId() + "!");
    }

    public void stopBarista() {
        running = false;
    }

    private void log(String message) {
        long time = (System.currentTimeMillis() - Cafe.startTime) / 1000;
        System.out.println(Thread.currentThread().getName() + ": " + time + "s: Barista " + id + ": " + message);
    }

}