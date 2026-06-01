import java.lang.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.io.*;
import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;
import testing.school.pojo.*;

class testingThreaded
{
// ── Shared counters across all threads ───────────────────────────────────────
static AtomicInteger successCount = new AtomicInteger(0);
static AtomicInteger failCount    = new AtomicInteger(0);
// ── How many threads run simultaneously ──────────────────────────────────────
static final int THREAD_COUNT = 15;
// ── How many save+query cycles each thread does ──────────────────────────────
static final int OPS_PER_THREAD = 30;
public static void main(String[] args)
{
System.out.println("=================================================");
System.out.println("  DataManager — Multi-Thread Test");
System.out.println("  Threads: " + THREAD_COUNT
+ "  |  Ops/thread: " + OPS_PER_THREAD);
System.out.println("=================================================\n");
try
{
DataManager.initialize(new File(System.getProperty("user.dir")));
DataManager dm = DataManager.getDataManager();
            // ── Build thread pool ────────────────────────────────────────────────
ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
// Latch: all threads start at exactly the same moment
// This maximises contention — real concurrent stress test
CountDownLatch startGun = new CountDownLatch(1);
CountDownLatch allDone  = new CountDownLatch(THREAD_COUNT);
// ── Submit worker threads ────────────────────────────────────────────
for (int t = 1; t <= THREAD_COUNT; t++)
{
final int threadId = t;
pool.submit(() ->
{
try
{
// Wait until all threads are ready, then start together
startGun.await();
runWorker(dm, threadId);
}
catch (InterruptedException ie)
{
Thread.currentThread().interrupt();
}
finally
{
allDone.countDown();
}
});
}
// ── Fire all threads simultaneously ──────────────────────────────────
System.out.println("[Main] All threads ready. Firing start gun...\n");
startGun.countDown();
// ── Wait for all threads to finish ───────────────────────────────────
allDone.await();
pool.shutdown();
// ── Final report ─────────────────────────────────────────────────────
int total = THREAD_COUNT * OPS_PER_THREAD;
System.out.println("\n=================================================");
System.out.println("  RESULTS");
System.out.println("=================================================");
System.out.println("  Total operations : " + total);
System.out.println("  Succeeded        : " + successCount.get());
System.out.println("  Failed           : " + failCount.get());
if (failCount.get() == 0)
System.out.println("\n  ✅  ALL PASSED — ThreadLocal isolation is working.");
else
System.out.println("\n  ❌  FAILURES DETECTED — check logs above.");
System.out.println("=================================================");
// ── Extra: single-thread query to confirm all rows actually saved ────
System.out.println("\n[Main] Final DB read — all courses saved:\n");
dm.begin();
List<Course> all = (List<Course>) dm.query(Course.class).fire();
dm.end();
for (Course c : all) System.out.println("  code=" + c.getCode() + "  title=" + c.getTitle());
}
catch (DataException de)
{
System.out.println("[Main] FATAL: " + de);
}
catch (InterruptedException ie)
{
Thread.currentThread().interrupt();
System.out.println("[Main] Interrupted: " + ie);
}
}

// ─────────────────────────────────────────────────────────────────────────────
// Worker: each thread runs this independently
// Each thread has its own Connection via ThreadLocal — no sharing
// ─────────────────────────────────────────────────────────────────────────────
static void runWorker(DataManager dm, int threadId)
{
String tag = "[Thread-" + threadId + "]";
System.out.println(tag + " started on " + Thread.currentThread().getName());

for (int op = 1; op <= OPS_PER_THREAD; op++)
{
// ── TEST 1: save() ───────────────────────────────────────────────────
Course saved = null;
try
{
dm.begin();
Course c = new Course();
c.setTitle("Course-T" + threadId + "-Op" + op);
dm.save(c);
dm.end();
saved = c;
successCount.incrementAndGet();
System.out.println(tag + " [Op " + op + "] SAVE OK"
+ " → code=" + c.getCode()
+ "  title=" + c.getTitle());
}
catch (DataException de)
{
failCount.incrementAndGet();
System.out.println(tag + " [Op " + op + "] SAVE FAILED: " + de);
try { dm.end(); } catch (Exception ignored) {}
}

// Small sleep — lets other threads interleave between save and query
sleep(50);

    // ── TEST 2: query() — each thread queries independently ──────────────
try
{
dm.begin();
List<Course> courses = (List<Course>) dm.query(Course.class).fire();
dm.end();
successCount.incrementAndGet();
System.out.println(tag + " [Op " + op + "] QUERY OK"
+ " → " + courses.size() + " courses in DB");
}
catch (DataException de)
{
failCount.incrementAndGet();
System.out.println(tag + " [Op " + op + "] QUERY FAILED: " + de);
try { dm.end(); } catch (Exception ignored) {}
}

// ── TEST 3: Intentional interleave check ─────────────────────────────
// Two threads run begin() at the same time here.
// If ThreadLocal is broken, they would share a connection and corrupt
// each other. Watch the output — codes must match their own thread.
if (saved != null)
{
try
{
dm.begin();

// Tiny sleep INSIDE the transaction — maximises overlap window
sleep(20);
List<Course> courses = (List<Course>) dm.query(Course.class).fire();
dm.end();

        // Verify the course we saved is actually in the result
final int savedCode = saved.getCode();
boolean found = courses.stream().anyMatch(c -> c.getCode() == savedCode);

if (found)
{
successCount.incrementAndGet();
System.out.println(tag + " [Op " + op + "] ISOLATION CHECK OK"
+ " — saved code " + savedCode + " confirmed in DB");
}
else
{
failCount.incrementAndGet();
System.out.println(tag + " [Op " + op + "] ISOLATION FAIL"
+ " — code " + savedCode + " NOT found (connection leaked?)");
}
}
catch (DataException de)
{
failCount.incrementAndGet();
System.out.println(tag + " [Op " + op + "] ISOLATION CHECK FAILED: " + de);
try { dm.end(); } catch (Exception ignored) {}
}
}
sleep(30);
}
System.out.println(tag + " finished.");
}

static void sleep(long ms)
{
try { Thread.sleep(ms); }
catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
}
}
