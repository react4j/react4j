package react4j.drumloop.model;

import org.jspecify.annotations.Nullable;
import react4j.Keyed;
import react4j.drumloop.browser.audio.AudioBuffer;

public abstract class AbstractSoundSample implements Keyed {
    private final DrumMachine _drumMachine;
    private final String _name;
    private final String _sound;

    @Nullable
    private AudioBuffer _audioBuffer;

    AbstractSoundSample(final DrumMachine drumMachine, final String name, final String sound) {
        _drumMachine = drumMachine;
        _name = name;
        _sound = sound;
    }

    @Override
    public String getKey() {
        return getSound();
    }

    public final String getName() {
        return _name;
    }

    public final String getSound() {
        return _sound;
    }

    public final void suspendUntilAudioLoaded() {
        if (null == _audioBuffer) {
            _audioBuffer = _drumMachine.getAudioCache().read(_sound);
        }
    }

    public DrumMachine getDrumMachine() {
        return _drumMachine;
    }

    public AudioBuffer getAudioBuffer() {
        assert null != _audioBuffer;
        return _audioBuffer;
    }
}
