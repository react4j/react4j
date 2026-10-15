package react4j.drumloop.model;

import arez.Arez;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.Observable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import react4j.drumloop.ReactCache;
import react4j.drumloop.browser.Response;
import react4j.drumloop.browser.WindowGlobal;
import react4j.drumloop.browser.audio.AudioBuffer;
import react4j.drumloop.browser.audio.AudioBufferSourceNode;
import react4j.drumloop.browser.audio.AudioContext;
import react4j.drumloop.browser.audio.GainNode;
import react4j.drumloop.browser.promise.Promise;
import zemeckis.Zemeckis;

@ArezComponent(service = Feature.ENABLE, requireId = Feature.DISABLE, disposeNotifier = Feature.DISABLE)
public abstract class DrumMachine {
    private static final int INITIAL_BPM = 65;
    private static final int BAR_COUNT = 4;
    private static final int BEATS_PER_BAR = 4;
    private static final int MAX_STEPS = BAR_COUNT * BEATS_PER_BAR;
    private final AudioContext _audioContext;
    private final List<Track> _tracks = new ArrayList<>();
    private final List<SoundEffect> _effects = new ArrayList<>();
    private int _bpm = INITIAL_BPM;
    private final ReactCache<String, AudioBuffer> _audioCache;

    DrumMachine() {
        _audioContext = new AudioContext();
        _tracks.add(new Track(this, "Kick", "sounds/kick.wav"));
        _tracks.add(new Track(this, "Sub1", "sounds/bass.wav"));
        _tracks.add(new Track(this, "Sub2", "sounds/sub.wav"));
        _tracks.add(new Track(this, "Snare", "sounds/snare.wav"));
        _tracks.add(new Track(this, "Clap", "sounds/clap.wav"));
        _tracks.add(new Track(this, "HiHat", "sounds/hat2.wav"));
        _tracks.add(new Track(this, "OpenHiHat", "sounds/openhihat.wav"));
        _effects.add(new SoundEffect(this, "Turn Up (F)", "sounds/loop.wav"));
        _effects.add(new SoundEffect(this, "SQUAD (Am)", "sounds/loop130.wav"));
        _effects.add(new SoundEffect(this, "Hey", "sounds/hey.wav"));
        _effects.add(new SoundEffect(this, "Yeah", "sounds/yeah.wav"));
        _audioCache = new ReactCache<>(this::loadAudioData);
        for (final Track track : _tracks) {
            _audioCache.preload(track.getSound());
        }
        for (final SoundEffect effect : _effects) {
            _audioCache.preload(effect.getSound());
        }
    }

    public List<Track> getTracks() {
        return Arez.areCollectionsPropertiesUnmodifiable() ? Collections.unmodifiableList(_tracks) : _tracks;
    }

    public List<SoundEffect> getEffects() {
        return Arez.areCollectionsPropertiesUnmodifiable() ? Collections.unmodifiableList(_effects) : _effects;
    }

    public AudioContext getActiveAudioContext() {
        if ("suspended".equals(_audioContext.state())) {
            _audioContext.resume();
        }
        return _audioContext;
    }

    ReactCache<String, AudioBuffer> getAudioCache() {
        return _audioCache;
    }

    @Observable(writeOutsideTransaction = Feature.ENABLE)
    public int getBpm() {
        return _bpm;
    }

    public void setBpm(final int bpm) {
        _bpm = bpm;
    }

    @Observable(writeOutsideTransaction = Feature.ENABLE)
    public abstract int getCurrentStep();

    abstract void setCurrentStep(int currentStep);

    @Observable
    public abstract boolean isRunning();

    public abstract void setRunning(boolean running);

    @Action
    public void toggleRunning() {
        final boolean running = !isRunning();
        setRunning(running);
        setCurrentStep(MAX_STEPS - 1);
        if (running) {
            getActiveAudioContext();
            playBeat();
        }
    }

    private void playSoundAt(final AudioBuffer buffer) {
        final GainNode gain = _audioContext.createGain();
        gain.gain().value = 0.2F;
        gain.connect(_audioContext.destination());
        final AudioBufferSourceNode node = _audioContext.createBufferSource();
        node.buffer = buffer;
        node.connect(gain);
        node.start();
    }

    @Action
    void playBeat() {
        if (isRunning()) {
            final int nextStep = (getCurrentStep() + 1) % MAX_STEPS;

            for (final Track track : getTracks()) {
                final StepCell cell = track.getStepCells().get(nextStep);
                if (cell.on()) {
                    if (cell.doubled()) {
                        playSoundAt(track.getAudioBuffer());
                        Zemeckis.delayedTask(() -> playSoundAt(track.getAudioBuffer()), 100);
                    } else {
                        playSoundAt(track.getAudioBuffer());
                    }
                }
            }

            setCurrentStep(nextStep);
            Zemeckis.delayedTask(this::playBeat, 60_000 / _bpm);
        }
    }

    private Promise<AudioBuffer> loadAudioData(final String sound) {
        return WindowGlobal.fetch(sound).then(Response::arrayBuffer).then(_audioContext::decodeAudioData);
    }
}
