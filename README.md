# ☕ GoGo Coffee Café

A Java simulation of a chaotic little café where 3 baristas try to keep up with 20 caffeine-craving customers, one shared espresso machine, one milk frother, and a single juice tap. Built for my Concurrent Programming module (CT074-3-2) at APU.

Basically — what happens when multiple threads all want the same coffee machine at once, and nobody's allowed to just kill a thread to make the problem go away.

## What's actually happening here

Three baristas sleep until a customer shows up, then scramble to make drinks using whatever machines are free. Customers wander in randomly, join a queue if it's busy, grab a seat if one's open (or wait it out, or just get tired and leave), and eventually get their coffee — cappuccino, espresso, or juice, in that order of popularity.

The catch: there's only one of each machine. So if two baristas both need the espresso machine at the same time, one of them waits. No cheating, no shortcuts, no `Thread.stop()` — just proper synchronization doing its job.

## The building blocks

- **Semaphores** — gatekeeping the espresso machine, frother, and juice tap so only one barista uses each at a time
- **LinkedBlockingQueue** — keeps the customer line honest (first come, first served)
- **AtomicInteger** — counts drinks sold and total sales without threads stepping on each other
- **synchronized / wait() / notifyAll()** — how baristas nap until a customer shows up, and how seating gets sorted out

## Running it

```bash
javac *.java
java Main
```

Runs for under a minute, throws out a live play-by-play labeled by thread (so you can actually tell which barista or customer is doing what), and ends with a tally of drinks sold and cash made.

## What you'll see in the logs
