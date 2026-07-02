# GoGo Coffee Café — Concurrent Programming Simulation

A Java-based multi-threaded simulation built for **CT074-3-2 Concurrent Programming** (Asia Pacific University), modeling a coffee café where baristas and customers interact concurrently under realistic resource constraints.

## 📖 Overview

This project simulates a small café with:
- **3 baristas** working independently
- **5 tables** (2 chairs each)
- **1 espresso machine**, **1 milk frothing machine**, and **1 juice tap** shared as limited resources
- **20 customers** arriving at random intervals, ordering drinks, waiting for seats, and leaving once finished

Each barista and customer is simulated as a **separate thread**, competing and cooperating for shared resources without violating thread-safety rules (no `Thread.stop`, `resume`, `suspend`, `interrupt`, or `setDaemon`).

## ☕ How It Works

**Baristas:**
- Sleep when no customers are waiting, and wake when a customer arrives
- Prepare drinks based on machine requirements:
  - Cappuccino (RM9) — needs espresso machine + milk frother
  - Espresso (RM6) — needs espresso machine
  - Juice (RM7) — needs juice tap
- Report machine acquisition/release and order completion in real time

**Customers:**
- Leave immediately if more than 5 people are already waiting
- Queue in **arrival order** (longest-waiting served first)
- Take a seat once available, preferring not to share a table with strangers (up to a wait threshold)
- Some customers get tired of standing and leave before being served
- Report entering, ordering, sitting, sipping, and leaving events
- Order distribution: 70% Cappuccino, 20% Espresso, 10% Juice

The café closes once all customers have left and all baristas are asleep.

## 🧵 Concurrency Concepts Used

- **`Semaphore`** — controls exclusive access to the espresso machine, milk frother, and juice tap
- **`LinkedBlockingQueue`** — manages the customer waiting line (FIFO ordering)
- **`AtomicInteger`** — thread-safe counters for tracking sales and drink totals
- **`synchronized` / `wait()` / `notifyAll()`** — coordinates barista sleep/wake behavior and seating availability

## 🚀 Running the Simulation

```bash
javac *.java
java Main
```

*(adjust the entry-point class name if different)*

The simulation runs for under 60 seconds and prints a live event log tagged by thread name, followed by a summary of total drinks sold and total sales for the day.

## 📊 Sample Output Format
