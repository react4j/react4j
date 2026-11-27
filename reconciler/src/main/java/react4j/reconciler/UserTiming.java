package react4j.reconciler;

import java.util.ArrayList;
import java.util.HashSet;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;

final class UserTiming {
    private static final String WAITING = "(Waiting for async callback...)";
    private static final String RECONCILING = "(React Tree Reconciliation)";
    private static final String COMMITTING = "(Committing Changes)";
    private static @Nullable UserTiming c_activeCommit;
    private static int c_nextRoot;

    private final Reporter _reporter;
    private final int _root = c_nextRoot++;
    private final ArrayList<Work> _work = new ArrayList<>();
    private final HashSet<String> _commitPhaseLabels = new HashSet<>();
    private int _nextMark;
    private boolean _waiting;
    private long _waitTimeout;
    private boolean _rendering;
    private boolean _scheduledDuringCommit;
    private @Nullable Phase _phase;

    UserTiming(Reporter reporter) {
        _reporter = reporter;
    }

    void startWaiting(long timeout) {
        if (!_waiting) {
            _waiting = true;
            _waitTimeout = timeout;
            _reporter.mark(markName(WAITING));
        }
    }

    void finishWaiting(boolean expired) {
        if (_waiting) {
            _waiting = false;
            measure(
                    WAITING,
                    "(Waiting for async callback... will force flush in " + _waitTimeout + " ms)",
                    expired ? "React was blocked by main thread" : null);
        }
    }

    void startRender() {
        _rendering = true;
        _reporter.mark(markName(RECONCILING));
        for (final var work : _work) {
            _reporter.mark(markName(work.mark));
        }
    }

    void finishRender(boolean completed, @Nullable String warning) {
        if (_rendering) {
            for (int i = _work.size() - 1; 0 <= i; i--) {
                final var work = _work.get(i);
                measure(work.mark, work.label, null);
                work.active = !completed;
            }
            if (completed) {
                _work.clear();
            }
            measure(
                    RECONCILING,
                    completed ? "(React Tree Reconciliation: Completed Root)" : "(React Tree Reconciliation: Yielded)",
                    warning);
            _rendering = false;
        }
    }

    void abandonRender() {
        for (int i = _work.size() - 1; 0 <= i; i--) {
            final var work = _work.get(i);
            if (_rendering) {
                measure(work.mark, work.label, null);
            }
            work.active = false;
        }
        _work.clear();
    }

    @Nullable
    Work startWork(String component, boolean mounting) {
        if (!_rendering) {
            return null;
        }
        final var work = new Work(component + (mounting ? " [mount]" : " [update]"), _nextMark++);
        _work.add(work);
        _reporter.mark(markName(work.mark));
        return work;
    }

    void finishWork(Work work, @Nullable String warning) {
        final var index = _work.size() - 1;
        if (0 <= index && work == _work.get(index)) {
            _work.remove(index);
            work.active = false;
            measure(work.mark, work.label, warning);
        }
    }

    void startCommit() {
        c_activeCommit = this;
        _scheduledDuringCommit = false;
        _reporter.mark(markName(COMMITTING));
    }

    void finishCommit() {
        measure(COMMITTING, COMMITTING, _scheduledDuringCommit ? "Lifecycle hook scheduled a cascading update" : null);
        _commitPhaseLabels.clear();
        c_activeCommit = null;
    }

    static void scheduledUpdate() {
        final var commit = c_activeCommit;
        if (null != commit) {
            commit._scheduledDuringCommit = true;
            if (null != commit._phase) {
                commit._phase.cascaded = true;
            }
        }
    }

    void startCommitPass(String pass) {
        _reporter.mark(markName("(" + pass + ")"));
    }

    void finishCommitPass(String pass, int count) {
        measure("(" + pass + ")", "(" + pass + ": " + count + " Total)", null);
    }

    @Nullable
    Phase startPhase(String component, String lifecycle) {
        final var label = component + "." + lifecycle;
        if (this == c_activeCommit && !_commitPhaseLabels.add(label)) {
            return null;
        }
        final var phase = new Phase(label, label + " (#" + _nextMark++ + ")");
        _phase = phase;
        _reporter.mark(markName(phase.mark));
        return phase;
    }

    void finishPhase(Phase phase) {
        _phase = null;
        measure(phase.mark, phase.label, phase.cascaded ? "Scheduled a cascading update" : null);
    }

    private void measure(String mark, String label, @Nullable String warning) {
        final var formattedMark = markName(mark);
        final var formattedLabel = null == warning ? "⚛ " + label : "⛔ " + label + " Warning: " + warning;
        try {
            _reporter.measure(formattedLabel, formattedMark);
        } catch (Throwable ignored) {
            // A consumer can clear marks before the measurement closes.
        } finally {
            _reporter.clearMarks(formattedMark);
            _reporter.clearMeasures(formattedLabel);
        }
    }

    private String markName(String label) {
        return "⚛ " + label + " [root " + _root + "]";
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Performance")
    interface Reporter {
        void mark(String name);

        void measure(String name, String mark);

        void clearMarks(String name);

        void clearMeasures(String name);
    }

    static final class Work {
        private final String label;
        private final String mark;
        private boolean active = true;

        private Work(String label, int id) {
            this.label = label;
            mark = label + " (#" + id + ")";
        }

        boolean active() {
            return active;
        }
    }

    static final class Phase {
        private final String label;
        private final String mark;
        private boolean cascaded;

        private Phase(String label, String mark) {
            this.label = label;
            this.mark = mark;
        }
    }
}
