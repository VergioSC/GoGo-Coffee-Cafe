import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Cafe.startTime = System.currentTimeMillis(); // <-- moved inside main()

        Cafe cafe = new Cafe();
        Random random = new Random();

        // --- Start 3 Baristas ---
        List<Barista> baristas = new ArrayList<>();
        List<Thread> baristaThreads = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            Barista barista = new Barista(i, cafe);
            Thread t = new Thread(barista, "Thread-Barista-" + i);
            baristas.add(barista);
            baristaThreads.add(t);
            t.start();
        }

        // --- Spawn 20 Customers one by one ---
        List<Thread> customerThreads = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            Customer customer = new Customer(i, cafe);
            Thread t = new Thread(customer, "Thread-Customer-" + i);
            customerThreads.add(t);
            t.start();

            // New customer arrives every 0, 1, or 2 seconds
            int arrivalDelay = random.nextInt(3) * 1000;
            Thread.sleep(arrivalDelay);
        }

        // --- Wait for all customers to finish ---
        for (Thread t : customerThreads) {
            t.join();
        }

        // --- Stop all baristas ---
        for (Barista b : baristas) {
            b.stopBarista();
        }

        // Wake baristas up so they can exit their while loop
        synchronized (cafe) {
            cafe.notifyAll();
        }

        // --- Wait for all baristas to finish ---
        for (Thread t : baristaThreads) {
            t.join();
        }

        // --- Print end of day sales report ---
        System.out.println("\n========================================");
        System.out.println("         GOGO COFFEE CAFE - END OF DAY REPORT");
        System.out.println("========================================");
        System.out.println("Cappuccinos sold : " + cafe.cappuccinoCount.get() + " x RM" + Cafe.CAPPUCCINO_PRICE + " = RM" + (cafe.cappuccinoCount.get() * Cafe.CAPPUCCINO_PRICE));
        System.out.println("Espressos sold   : " + cafe.espressoCount.get()   + " x RM" + Cafe.ESPRESSO_PRICE   + " = RM" + (cafe.espressoCount.get()   * Cafe.ESPRESSO_PRICE));
        System.out.println("Juices sold      : " + cafe.juiceCount.get()      + " x RM" + Cafe.JUICE_PRICE      + " = RM" + (cafe.juiceCount.get()      * Cafe.JUICE_PRICE));
        System.out.println("----------------------------------------");
        System.out.println("Total Sales      : RM" + cafe.totalSales.get());
        System.out.println("Cafe is now closed. Goodbye!");
        System.out.println("========================================");
    }
}