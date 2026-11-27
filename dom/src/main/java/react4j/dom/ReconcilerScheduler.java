package react4j.dom;

interface ReconcilerScheduler {
    double now();

    boolean shouldYield(double deadline);

    int schedule(Reconciler.Task task, int delay);

    void cancel(int id);
}
