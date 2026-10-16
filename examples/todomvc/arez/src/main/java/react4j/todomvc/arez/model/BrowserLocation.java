package react4j.todomvc.arez.model;

import arez.ComputableValue;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.ComputableValueRef;
import arez.annotations.DepType;
import arez.annotations.Feature;
import arez.annotations.Memoize;
import arez.annotations.Observable;
import arez.annotations.OnActivate;
import arez.annotations.OnDeactivate;
import react4j.todomvc.browser.Event;
import react4j.todomvc.browser.EventListener;
import react4j.todomvc.browser.Location;
import react4j.todomvc.browser.WindowGlobal;

/**
 * Tracks the browser hash through Arez.
 * Adapted from the arez-dom library.
 *
 * @see https://github.com/arez/arez-dom
 */
@ArezComponent(requireId = Feature.DISABLE, disposeNotifier = Feature.DISABLE)
abstract class BrowserLocation {
    private final EventListener _listener = this::onHashChangeEvent;
    /**
     * The location according to the application.
     */
    private String _location;
    /**
     * The location that the application is attempting to update the browser to.
     */
    private String _targetLocation;

    BrowserLocation() {
        _targetLocation = _location = getHash();
    }

    /**
     * Change the target location to the specified parameter.
     * This will ultimately result in a side-effect that updates the browsers location.
     * This location parameter should not include "#" as the first character.
     *
     * @param targetLocation the location to change to.
     */
    @Action(verifyRequired = false)
    void changeLocation(final String targetLocation) {
        _targetLocation = targetLocation;
        if (targetLocation.equals(getBrowserLocation())) {
            setLocation(targetLocation);
        }
        setHash(targetLocation);
        /*
         * pushState does not emit hashchange, so reconcile the location here
         */
        updateBrowserLocation();
    }

    /**
     * Revert the browsers location to the application location.
     */
    @Action
    void resetBrowserLocation() {
        changeLocation(getLocation());
    }

    /**
     * Return the location as the application sees it.
     * This return value does not include a "#" as the first character.
     *
     * @return the location.
     */
    @Observable
    String getLocation() {
        return _location;
    }

    @Observable
    void setLocation(final String location) {
        _location = location;
    }

    @Memoize(depType = DepType.AREZ_OR_EXTERNAL)
    String getBrowserLocation() {
        return getHash();
    }

    @OnActivate
    void onBrowserLocationActivate() {
        WindowGlobal.addEventListener("hashchange", _listener, false);
    }

    @OnDeactivate
    void onBrowserLocationDeactivate() {
        WindowGlobal.removeEventListener("hashchange", _listener, false);
    }

    @ComputableValueRef
    abstract ComputableValue<?> getBrowserLocationComputableValue();

    @Action
    void updateBrowserLocation() {
        getBrowserLocationComputableValue().reportPossiblyChanged();
        final String location = getBrowserLocation();
        if (_targetLocation.equals(location)) {
            setLocation(location);
        }
    }

    private void onHashChangeEvent(final Event e) {
        e.preventDefault();
        updateBrowserLocation();
    }

    private String getHash() {
        final String hash = WindowGlobal.location().hash();
        return hash.isEmpty() ? "" : hash.substring(1);
    }

    private void setHash(final String hash) {
        final Location location = WindowGlobal.location();
        if (0 == hash.length()) {
            /*
             * This code is needed to remove the stray #.
             * See https://stackoverflow.com/questions/1397329/how-to-remove-the-hash-from-window-location-url-with-javascript-without-page-r/5298684#5298684
             */
            WindowGlobal.history()
                    .pushState("", WindowGlobal.document().title(), location.pathname() + location.search());
        } else {
            location.setHash(hash);
        }
    }
}
