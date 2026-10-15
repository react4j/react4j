package react4j.drumloop.model;

import arez.Arez;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Track extends AbstractSoundSample {
    private static final int MAX_BEATS = 16;
    private final List<StepCell> _stepCells = new ArrayList<>();

    public Track(final DrumMachine drumMachine, final String name, final String sound) {
        super(drumMachine, name, sound);
        for (int i = 0; i < MAX_BEATS; i++) {
            _stepCells.add(StepCell.create(i));
        }
    }

    public List<StepCell> getStepCells() {
        return Arez.areCollectionsPropertiesUnmodifiable() ? Collections.unmodifiableList(_stepCells) : _stepCells;
    }
}
