package react4j.examples.arez.step2.model;

import arez.annotations.ArezComponent;
import arez.annotations.Observable;

@ArezComponent
public abstract class ViewService {
    private FilterMode _filterMode = FilterMode.ALL;

    ViewService() {}

    @Observable
    public FilterMode getFilterMode() {
        return _filterMode;
    }

    public void setFilterMode(final FilterMode filterMode) {
        _filterMode = filterMode;
    }
}
