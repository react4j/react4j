package react4j.dom.proptypes.html;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;
import react4j.dom.events.DragEventHandler;
import react4j.dom.events.FocusEventHandler;
import react4j.dom.events.FormEventHandler;
import react4j.dom.events.KeyboardEventHandler;
import react4j.dom.events.MouseEventHandler;
import react4j.dom.events.TouchEventHandler;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.dom.proptypes.html.attributeTypes.OnOff;

/**
 * Props for input elements. Refer to http://www.w3schools.com/tags/tag_input.asp
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class InputProps extends HtmlGlobalFields<InputProps> {
    @JsOverlay
    public final InputProps accept(@Nullable String s) {
        setAccept(s);
        return self();
    }

    @JsOverlay
    public final InputProps alt(@Nullable String s) {
        setAlt(s);
        return self();
    }

    @JsOverlay
    public final InputProps async() {
        return async(true);
    }

    @JsOverlay
    public final InputProps async(boolean b) {
        setAsync(b);
        return self();
    }

    @JsOverlay
    public final InputProps autoComplete(@OnOff final String value) {
        setAutoComplete(value);
        return self();
    }

    @JsOverlay
    public final InputProps autoFocus() {
        return autoFocus(true);
    }

    @JsOverlay
    public final InputProps autoFocus(boolean b) {
        setAutoFocus(b);
        return self();
    }

    @JsOverlay
    public final InputProps checked() {
        return checked(true);
    }

    @JsOverlay
    public final InputProps checked(boolean b) {
        setChecked(b);
        return self();
    }

    @JsOverlay
    public final InputProps disabled() {
        return disabled(true);
    }

    @JsOverlay
    public final InputProps disabled(boolean b) {
        setDisabled(b);
        return self();
    }

    @JsOverlay
    public final InputProps form(@Nullable String s) {
        setForm(s);
        return self();
    }

    @JsOverlay
    public final InputProps formAction(@Nullable String s) {
        setFormAction(s);
        return self();
    }

    @JsOverlay
    public final InputProps formEncType(@Nullable String s) {
        setFormEncType(s);
        return self();
    }

    @JsOverlay
    public final InputProps formMethod(@Nullable String s) {
        setFormMethod(s);
        return self();
    }

    @JsOverlay
    public final InputProps formNoValidate() {
        return formNoValidate(true);
    }

    @JsOverlay
    public final InputProps formNoValidate(boolean b) {
        setFormNoValidate(b);
        return self();
    }

    @JsOverlay
    public final InputProps formTarget(@Nullable String s) {
        setFormTarget(s);
        return self();
    }

    @JsOverlay
    public final InputProps height(@Nullable String s) {
        setHeight(s);
        return self();
    }

    @JsOverlay
    public final InputProps list(@Nullable String s) {
        setList(s);
        return self();
    }

    @JsOverlay
    public final InputProps max(@Nullable String s) {
        setMax(s);
        return self();
    }

    @JsOverlay
    public final InputProps maxLength(int b) {
        setMaxLength(b);
        return self();
    }

    @JsOverlay
    public final InputProps min(@Nullable String s) {
        setMin(s);
        return self();
    }

    @JsOverlay
    public final InputProps multiple() {
        return multiple(true);
    }

    @JsOverlay
    public final InputProps multiple(boolean b) {
        setMultiple(b);
        return self();
    }

    @JsOverlay
    public final InputProps name(@Nullable String s) {
        setName(s);
        return self();
    }

    @JsOverlay
    public final InputProps placeHolder(@Nullable String s) {
        setPlaceholder(s);
        return self();
    }

    @JsOverlay
    public final InputProps readonly() {
        return readonly(true);
    }

    @JsOverlay
    public final InputProps readonly(boolean b) {
        setReadOnly(b);
        return self();
    }

    @JsOverlay
    public final InputProps required() {
        return required(true);
    }

    @JsOverlay
    public final InputProps required(boolean b) {
        setRequired(b);
        return self();
    }

    @JsOverlay
    public final InputProps size(int b) {
        setSize(b);
        return self();
    }

    @JsOverlay
    public final InputProps src(@Nullable String s) {
        setSrc(s);
        return self();
    }

    @JsOverlay
    public final InputProps step(@Nullable String s) {
        setStep(s);
        return self();
    }

    @JsOverlay
    public final InputProps type(@InputType final String value) {
        setType(value);
        return self();
    }

    @JsOverlay
    public final InputProps value(@Nullable String s) {
        setValue(s);
        return self();
    }

    @JsOverlay
    public final InputProps pattern(final String value) {
        setPattern(value);
        return self();
    }

    @JsOverlay
    public final InputProps width(int i) {
        setWidth(Integer.toString(i));
        return self();
    }

    // React Specific

    @JsOverlay
    public final InputProps defaultChecked() {
        return defaultChecked(true);
    }

    @JsOverlay
    public final InputProps defaultChecked(boolean b) {
        setDefaultChecked(b);
        return self();
    }

    @JsOverlay
    public final InputProps defaultValue(@Nullable String s) {
        setDefaultValue(s);
        return self();
    }

    // Applicable Event Handlers

    // Focus Events
    @JsOverlay
    public final InputProps onBlur(@Nullable final FocusEventHandler handler) {
        setOnBlur(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onFocus(@Nullable final FocusEventHandler handler) {
        setOnFocus(handler);
        return self();
    }

    // Form Events
    @JsOverlay
    public final InputProps onChange(@Nullable final FormEventHandler handler) {
        setOnChange(handler);
        return self();
    }

    // Keyboard Events
    @JsOverlay
    public final InputProps onKeyDown(@Nullable final KeyboardEventHandler handler) {
        setOnKeyDown(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onKeyPress(@Nullable final KeyboardEventHandler handler) {
        setOnKeyPress(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onKeyUp(@Nullable final KeyboardEventHandler handler) {
        setOnKeyUp(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onClick(@Nullable final MouseEventHandler handler) {
        setOnClick(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onContextMenu(@Nullable final MouseEventHandler handler) {
        setOnContextMenu(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onDoubleClick(@Nullable final MouseEventHandler handler) {
        setOnDoubleClick(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onDrag(@Nullable final DragEventHandler handler) {
        setOnDrag(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onDragEnd(@Nullable final DragEventHandler handler) {
        setOnDragEnd(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onDragEnter(@Nullable final DragEventHandler handler) {
        setOnDragEnter(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onDragExit(@Nullable final DragEventHandler handler) {
        setOnDragExit(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onDragLeave(@Nullable final DragEventHandler handler) {
        setOnDragLeave(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onDragOver(@Nullable final DragEventHandler handler) {
        setOnDragOver(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onDragStart(@Nullable final DragEventHandler handler) {
        setOnDragStart(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onDrop(@Nullable final DragEventHandler handler) {
        setOnDrop(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onMouseDown(@Nullable final MouseEventHandler handler) {
        setOnMouseDown(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onMouseEnter(@Nullable final MouseEventHandler handler) {
        setOnMouseEnter(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onMouseLeave(@Nullable final MouseEventHandler handler) {
        setOnMouseLeave(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onMouseMove(@Nullable final MouseEventHandler handler) {
        setOnMouseMove(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onMouseOut(@Nullable final MouseEventHandler handler) {
        setOnMouseOut(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onMouseOver(@Nullable final MouseEventHandler handler) {
        setOnMouseOver(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onMouseUp(@Nullable final MouseEventHandler handler) {
        setOnMouseUp(handler);
        return self();
    }

    // Touch Events
    @JsOverlay
    public final InputProps onTouchCancel(@Nullable final TouchEventHandler handler) {
        setOnTouchCancel(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onTouchEnd(@Nullable final TouchEventHandler handler) {
        setOnTouchEnd(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onTouchMove(@Nullable final TouchEventHandler handler) {
        setOnTouchMove(handler);
        return self();
    }

    @JsOverlay
    public final InputProps onTouchStart(@Nullable final TouchEventHandler handler) {
        setOnTouchStart(handler);
        return self();
    }
}
