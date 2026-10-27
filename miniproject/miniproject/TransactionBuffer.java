
public class TransactionBuffer {
    private final Runnable[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = 0;

    public TransactionBuffer(int capacity) {
        buffer = new Runnable[capacity];
    }

    public synchronized void put(Runnable task)
            throws InterruptedException {
        while (count == buffer.length) {
            wait();
        }

        buffer[in] = task;
        in = (in + 1) % buffer.length;
        count++;

        notifyAll();
    }

    public synchronized Runnable take()
            throws InterruptedException {
        while (count == 0) {
            wait();
        }

        Runnable task = buffer[out];
        buffer[out] = null;
        out = (out + 1) % buffer.length;
        count--;

        notifyAll();
        return task;
    }
}
