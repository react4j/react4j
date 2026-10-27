package react4j.dom.proptypes.html;

import static org.realityforge.braincheck.Guards.*;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.Renderable;
import react4j.dom.events.ClipboardEventHandler;
import react4j.dom.events.CompositionEventHandler;
import react4j.dom.events.DragEventHandler;
import react4j.dom.events.FocusEventHandler;
import react4j.dom.events.FormEventHandler;
import react4j.dom.events.KeyboardEventHandler;
import react4j.dom.events.MouseEventHandler;
import react4j.dom.events.ReactEventHandler;
import react4j.dom.events.TouchEventHandler;
import react4j.dom.events.UIEventHandler;
import react4j.dom.events.WheelEventHandler;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.dom.proptypes.html.attributeTypes.YesNo;

@SuppressWarnings("unused")
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class HtmlGlobalFields<T extends HtmlGlobalFields<T>> {
    @Nullable
    public Renderable children;

    @Nullable
    public String key;

    @Nullable
    public RefConsumer ref;

    @JsOverlay
    public final T ref(@Nullable final RefConsumer callback) {
        ref = callback;
        return self();
    }

    @JsOverlay
    public final T key(final int value) {
        return key(String.valueOf(value));
    }

    @JsOverlay
    public final T key(final String value) {
        key = value;
        return self();
    }

    @JsOverlay
    public final T dangerouslySetInnerHTML(final String html) {
        setDangerouslySetInnerHTML(JsPropertyMap.of("__html", html));
        return self();
    }

    @JsOverlay
    public final T prop(final String key, @Nullable final Any value) {
        Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(this).set(key, value);
        return self();
    }

    /** Sets a data attribute by suffix: {@code data("shared", "true")} sets data-shared; null removes it. */
    @JsOverlay
    public final T data(final String name, @Nullable final String value) {
        Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(this).set("data-" + name, value);
        return self();
    }

    @JsProperty
    protected native void setDangerouslySetInnerHTML(JsPropertyMap<Object> __html);

    // React Specific
    @JsProperty
    public native boolean isDefaultChecked();

    @JsProperty
    protected native void setDefaultChecked(boolean defaultChecked);

    @JsProperty
    public native @Nullable String getDefaultValue();

    @JsProperty
    protected native void setDefaultValue(@Nullable String defaultValue);

    // Standard HTML Attributes
    @JsProperty
    public native @Nullable String getAccept();

    @JsProperty
    protected native void setAccept(@Nullable String accept);

    @JsProperty
    public native @Nullable String getAcceptCharset();

    @JsProperty
    protected native void setAcceptCharset(@Nullable String acceptCharset);

    @JsProperty
    public native @Nullable String getAccessKey();

    @JsOverlay
    public final T accessKey(@Nullable String accessKey) {
        setAccessKey(accessKey);
        return self();
    }

    @JsProperty
    protected native void setAccessKey(@Nullable String accessKey);

    @JsProperty
    public native @Nullable String getAction();

    @JsProperty
    protected native void setAction(@Nullable String action);

    @JsProperty
    public native boolean isAllowFullScreen();

    @JsProperty
    protected native void setAllowFullScreen(boolean allowFullScreen);

    @JsProperty
    public native boolean isAllowTransparency();

    @JsProperty
    protected native void setAllowTransparency(boolean allowTransparency);

    @JsProperty
    public native @Nullable String getAlt();

    @JsProperty
    protected native void setAlt(@Nullable String alt);

    @JsProperty
    public native boolean isAsync();

    @JsProperty
    protected native void setAsync(boolean async);

    @JsProperty
    public native @Nullable String getAutoComplete();

    @JsProperty
    protected native void setAutoComplete(@Nullable String autoComplete);

    @JsProperty
    public native boolean isAutoFocus();

    @JsProperty
    protected native void setAutoFocus(boolean autoFocus);

    @JsProperty
    public native boolean isAutoPlay();

    @JsProperty
    protected native void setAutoPlay(boolean autoPlay);

    @JsProperty
    public native boolean isCapture();

    @JsProperty
    protected native void setCapture(boolean capture);

    @JsProperty
    public native @Nullable String getCellPadding();

    @JsProperty
    protected native void setCellPadding(@Nullable String cellPadding);

    @JsProperty
    public native @Nullable String getCellSpacing();

    @JsProperty
    protected native void setCellSpacing(@Nullable String cellSpacing);

    @JsProperty
    public native @Nullable String getCharSet();

    @JsProperty
    protected native void setCharSet(@Nullable String charSet);

    @JsProperty
    public native @Nullable String getChallenge();

    @JsProperty
    protected native void setChallenge(@Nullable String challenge);

    @JsProperty
    public native boolean isChecked();

    @JsProperty
    protected native void setChecked(boolean checked);

    @JsProperty
    public native @Nullable String getClassID();

    @JsProperty
    protected native void setClassID(@Nullable String classID);

    @JsProperty
    public native @Nullable String getClassName();

    @SuppressWarnings("StringConcatenationInLoop")
    @JsOverlay
    public final T className(final @Nullable String... elements) {
        String className = null;
        for (final String element : elements) {
            if (null != element) {
                if (React.shouldCheckInvariants()) {
                    invariant(
                            () -> !element.equals("") && !element.matches("^\\s+$"),
                            () -> "Empty classname element supplied. Remove the element.");
                    invariant(
                            () -> !element.matches("^\\s.*$"),
                            () -> "Classname element '" + element + "' starts with whitespace. Remove the whitespace.");
                    invariant(
                            () -> !element.matches("^.*\\s$"),
                            () -> "Classname element '" + element + "' ends with whitespace. Remove the whitespace.");
                }
                if (null == className) {
                    className = element;
                } else {
                    className += " " + element;
                }
            }
        }
        if (null != className) {
            setClassName(className);
        }
        return self();
    }

    @JsProperty
    protected native void setClassName(@Nullable String className);

    @JsProperty
    public native int getCols();

    @JsProperty
    protected native void setCols(int cols);

    @JsProperty
    public native int getColSpan();

    @JsProperty
    protected native void setColSpan(int colSpan);

    @JsProperty
    public native @Nullable String getContent();

    @JsProperty
    protected native void setContent(@Nullable String content);

    @JsProperty
    public native boolean isContentEditable();

    @JsOverlay
    public final T contentEditable(boolean contentEditable) {
        setContentEditable(contentEditable);
        return self();
    }

    @JsProperty
    protected native void setContentEditable(boolean contentEditable);

    @JsProperty
    public native @Nullable String getContextMenu();

    @JsOverlay
    public final T contextMenu(@Nullable String contextMenu) {
        setContextMenu(contextMenu);
        return self();
    }

    @JsProperty
    protected native void setContextMenu(@Nullable String contextMenu);

    @JsProperty
    public native boolean isControls();

    @JsProperty
    protected native void setControls(boolean controls);

    @JsProperty
    public native @Nullable String getCoords();

    @JsProperty
    protected native void setCoords(@Nullable String coords);

    @JsProperty
    public native @Nullable String getCrossOrigin();

    @JsProperty
    protected native void setCrossOrigin(@Nullable String crossOrigin);

    @JsProperty
    public native @Nullable String getData();

    @JsProperty
    protected native void setData(@Nullable String data);

    @JsProperty
    public native @Nullable String getDateTime();

    @JsProperty
    protected native void setDateTime(@Nullable String dateTime);

    // @JsProperty public native boolean is_default();
    // @JsProperty protected native void set_default(boolean _default);

    @JsProperty
    public native boolean isDefer();

    @JsProperty
    protected native void setDefer(boolean defer);

    @JsProperty
    public native @Nullable String getDir();

    @JsOverlay
    public final T dir(@Nullable String dir) {
        setDir(dir);
        return self();
    }

    @JsProperty
    protected native void setDir(@Nullable String dir);

    @JsProperty
    public native boolean isDisabled();

    @JsProperty
    protected native void setDisabled(boolean disabled);

    @JsProperty
    public native @Nullable String getDownload();

    @JsProperty
    protected native void setDownload(@Nullable String download);

    @JsProperty
    public native boolean isDraggable();

    @JsOverlay
    public final T draggable(boolean draggable) {
        setDraggable(draggable);
        return self();
    }

    @JsProperty
    protected native void setDraggable(boolean draggable);

    @JsProperty
    public native @Nullable String getEncType();

    @JsProperty
    protected native void setEncType(@Nullable String encType);

    @JsProperty
    public native @Nullable String getForm();

    @JsProperty
    protected native void setForm(@Nullable String form);

    @JsProperty
    public native @Nullable String getFormAction();

    @JsProperty
    protected native void setFormAction(@Nullable String formAction);

    @JsProperty
    public native @Nullable String getFormEncType();

    @JsProperty
    protected native void setFormEncType(@Nullable String formEncType);

    @JsProperty
    public native @Nullable String getFormMethod();

    @JsProperty
    protected native void setFormMethod(@Nullable String formMethod);

    @JsProperty
    public native boolean isFormNoValidate();

    @JsProperty
    protected native void setFormNoValidate(boolean formNoValidate);

    @JsProperty
    public native @Nullable String getFormTarget();

    @JsProperty
    protected native void setFormTarget(@Nullable String formTarget);

    @JsProperty
    public native @Nullable String getFrameBorder();

    @JsProperty
    protected native void setFrameBorder(@Nullable String frameBorder);

    @JsProperty
    public native @Nullable String getHeaders();

    @JsProperty
    protected native void setHeaders(@Nullable String headers);

    @JsProperty
    public native @Nullable String getHeight();

    @JsProperty
    protected native void setHeight(@Nullable String height);

    @JsProperty
    public native boolean isHidden();

    @JsOverlay
    public final T hidden(boolean hidden) {
        setHidden(hidden);
        return self();
    }

    @JsProperty
    protected native void setHidden(boolean hidden);

    @JsProperty
    public native int getHigh();

    @JsProperty
    protected native void setHigh(int high);

    @JsProperty
    public native @Nullable String getHref();

    @JsProperty
    protected native void setHref(@Nullable String href);

    @JsProperty
    public native @Nullable String getHrefLang();

    @JsProperty
    protected native void setHrefLang(@Nullable String hrefLang);

    @JsProperty
    public native @Nullable String getHtmlFor();

    @JsProperty
    protected native void setHtmlFor(@Nullable String htmlFor);

    @JsProperty
    public native @Nullable String getHttpEquiv();

    @JsProperty
    protected native void setHttpEquiv(@Nullable String httpEquiv);

    @JsProperty
    public native @Nullable String getIcon();

    @JsProperty
    protected native void setIcon(@Nullable String icon);

    @JsProperty
    public native @Nullable String getId();

    @JsOverlay
    public final T id(@Nullable String id) {
        setId(id);
        return self();
    }

    @JsProperty
    protected native void setId(@Nullable String id);

    @JsProperty
    public native @Nullable String getInputMode();

    @JsProperty
    protected native void setInputMode(@Nullable String inputMode);

    @JsProperty
    public native @Nullable String getIntegrity();

    @JsProperty
    protected native void setIntegrity(@Nullable String integrity);

    @JsProperty
    public native @Nullable String getIs();

    @JsProperty
    protected native void setIs(@Nullable String is);

    @JsProperty
    public native @Nullable String getKeyParams();

    @JsProperty
    protected native void setKeyParams(@Nullable String keyParams);

    @JsProperty
    public native @Nullable String getKeyType();

    @JsProperty
    protected native void setKeyType(@Nullable String keyType);

    @JsProperty
    public native @Nullable String getKind();

    @JsProperty
    protected native void setKind(@Nullable String kind);

    @JsProperty
    public native @Nullable String getLabel();

    @JsProperty
    protected native void setLabel(@Nullable String label);

    @JsProperty
    public native @Nullable String getLang();

    @JsOverlay
    public final T lang(@Nullable String lang) {
        setLang(lang);
        return self();
    }

    @JsProperty
    protected native void setLang(@Nullable String lang);

    @JsProperty
    public native @Nullable String getList();

    @JsProperty
    protected native void setList(@Nullable String list);

    @JsProperty
    public native boolean isLoop();

    @JsProperty
    protected native void setLoop(boolean loop);

    @JsProperty
    public native int getLow();

    @JsProperty
    protected native void setLow(int low);

    @JsProperty
    public native @Nullable String getManifest();

    @JsProperty
    protected native void setManifest(@Nullable String manifest);

    @JsProperty
    public native int getMarginHeight();

    @JsProperty
    protected native void setMarginHeight(int marginHeight);

    @JsProperty
    public native int getMarginWidth();

    @JsProperty
    protected native void setMarginWidth(int marginWidth);

    @JsProperty
    public native @Nullable String getMax();

    @JsProperty
    protected native void setMax(@Nullable String max);

    @JsProperty
    public native int getMaxLength();

    @JsProperty
    protected native void setMaxLength(int maxLength);

    @JsProperty
    public native @Nullable String getMedia();

    @JsProperty
    protected native void setMedia(@Nullable String media);

    @JsProperty
    public native @Nullable String getMediaGroup();

    @JsProperty
    protected native void setMediaGroup(@Nullable String mediaGroup);

    @JsProperty
    public native @Nullable String getMethod();

    @JsProperty
    protected native void setMethod(@Nullable String method);

    @JsProperty
    public native @Nullable String getMin();

    @JsProperty
    protected native void setMin(@Nullable String min);

    @JsProperty
    public native int getMinLength();

    @JsProperty
    protected native void setMinLength(int minLength);

    @JsProperty
    public native boolean isMultiple();

    @JsProperty
    protected native void setMultiple(boolean multiple);

    @JsProperty
    public native boolean isMuted();

    @JsProperty
    protected native void setMuted(boolean muted);

    @JsProperty
    public native @Nullable String getName();

    @JsProperty
    protected native void setName(@Nullable String name);

    @JsProperty
    public native boolean isNoValidate();

    @JsProperty
    protected native void setNoValidate(boolean noValidate);

    @JsProperty
    public native boolean isOpen();

    @JsProperty
    protected native void setOpen(boolean open);

    @JsProperty
    public native int getOptimum();

    @JsProperty
    protected native void setOptimum(int optimum);

    @JsProperty
    public native @Nullable String getPattern();

    @JsProperty
    protected native void setPattern(@Nullable String pattern);

    @JsProperty
    public native @Nullable String getPlaceholder();

    @JsProperty
    protected native void setPlaceholder(@Nullable String placeholder);

    @JsProperty
    public native @Nullable String getPoster();

    @JsProperty
    protected native void setPoster(@Nullable String poster);

    @JsProperty
    public native @Nullable String getPreload();

    @JsProperty
    protected native void setPreload(@Nullable String preload);

    @JsProperty
    public native @Nullable String getRadioGroup();

    @JsProperty
    protected native void setRadioGroup(@Nullable String radioGroup);

    @JsProperty
    public native boolean isReadOnly();

    @JsProperty
    protected native void setReadOnly(boolean readOnly);

    @JsProperty
    public native @Nullable String getRel();

    @JsProperty
    protected native void setRel(@Nullable String rel);

    @JsProperty
    public native @Nullable String getReferrerPolicy();

    @JsProperty
    protected native void setReferrerPolicy(@Nullable String rel);

    @JsProperty
    public native boolean isRequired();

    @JsProperty
    protected native void setRequired(boolean required);

    @JsProperty
    public native @Nullable String getRole();

    @JsProperty
    protected native void setRole(@Nullable String role);

    @JsProperty
    public native int getRows();

    @JsProperty
    protected native void setRows(int rows);

    @JsProperty
    public native int getRowSpan();

    @JsProperty
    protected native void setRowSpan(int rowSpan);

    @JsProperty
    public native @Nullable String getSandbox();

    @JsProperty
    protected native void setSandbox(@Nullable String sandbox);

    @JsProperty
    public native @Nullable String getScope();

    @JsProperty
    protected native void setScope(@Nullable String scope);

    @JsProperty
    public native boolean isScoped();

    @JsProperty
    protected native void setScoped(boolean scoped);

    @JsProperty
    public native @Nullable String getScrolling();

    @JsProperty
    protected native void setScrolling(@Nullable String scrolling);

    @JsProperty
    public native boolean isSeamless();

    @JsProperty
    protected native void setSeamless(boolean seamless);

    @JsProperty
    public native boolean isSelected();

    @JsProperty
    protected native void setSelected(boolean selected);

    @JsProperty
    public native @Nullable String getShape();

    @JsProperty
    protected native void setShape(@Nullable String shape);

    @JsProperty
    public native int getSize();

    @JsProperty
    protected native void setSize(int size);

    @JsProperty
    public native @Nullable String getSizes();

    @JsProperty
    protected native void setSizes(@Nullable String sizes);

    @JsProperty
    public native int getSpan();

    @JsProperty
    protected native void setSpan(int span);

    @JsProperty
    public native boolean isSpellCheck();

    @JsOverlay
    public final T spellCheck(boolean spellCheck) {
        setSpellCheck(spellCheck);
        return self();
    }

    @JsProperty
    protected native void setSpellCheck(boolean spellCheck);

    @JsProperty
    public native @Nullable String getSrc();

    @JsProperty
    protected native void setSrc(@Nullable String src);

    @JsProperty
    public native @Nullable String getSrcDoc();

    @JsProperty
    protected native void setSrcDoc(@Nullable String srcDoc);

    @JsProperty
    public native @Nullable String getSrcLang();

    @JsProperty
    protected native void setSrcLang(@Nullable String srcLang);

    @JsProperty
    public native @Nullable String getSrcSet();

    @JsProperty
    protected native void setSrcSet(@Nullable String srcSet);

    @JsProperty
    public native int getStart();

    @JsProperty
    protected native void setStart(int start);

    @JsProperty
    public native @Nullable String getStep();

    @JsProperty
    protected native void setStep(@Nullable String step);

    @JsProperty
    public native @Nullable CssProps getStyle();

    @JsOverlay
    public final T style(@Nullable CssProps style) {
        setStyle(style);
        return self();
    }

    @JsProperty
    protected native void setStyle(@Nullable CssProps style);

    @JsProperty
    public native @Nullable String getSummary();

    @JsProperty
    protected native void setSummary(@Nullable String summary);

    @JsProperty
    public native int getTabIndex();

    @JsOverlay
    public final T tabIndex(int tabIndex) {
        setTabIndex(tabIndex);
        return self();
    }

    @JsProperty
    protected native void setTabIndex(int tabIndex);

    @JsProperty
    public native @Nullable String getTarget();

    @JsProperty
    protected native void setTarget(@Nullable String target);

    @JsProperty
    public native @Nullable String getTitle();

    @JsOverlay
    public final T title(@Nullable String title) {
        setTitle(title);
        return self();
    }

    @JsProperty
    protected native void setTitle(@Nullable String title);

    @JsProperty
    public native @Nullable String getType();

    @JsProperty
    protected native void setType(@InputType String type);

    @JsProperty
    public native @Nullable String getTranslate();

    @JsOverlay
    public final T translate(@YesNo final String s) {
        setTranslate(s);
        return self();
    }

    @JsProperty
    protected native void setTranslate(@Nullable String translate);

    @JsProperty
    public native @Nullable String getUseMap();

    @JsProperty
    protected native void setUseMap(@Nullable String useMap);

    @JsProperty
    public native @Nullable String getValue();

    @JsProperty
    protected native void setValue(@Nullable String value);

    @JsProperty
    public native @Nullable String getWidth();

    @JsProperty
    protected native void setWidth(@Nullable String width);

    @JsProperty
    public native @Nullable String getWmode();

    @JsProperty
    protected native void setWmode(@Nullable String wmode);

    @JsProperty
    public native @Nullable String getWrap();

    @JsProperty
    protected native void setWrap(@Nullable String wrap);

    // RDFa Attributes

    @JsProperty
    public native @Nullable String getAbout();

    @JsProperty
    protected native void setAbout(@Nullable String about);

    @JsProperty
    public native @Nullable String getDatatype();

    @JsProperty
    protected native void setDatatype(@Nullable String datatype);

    @JsProperty
    public native @Nullable String getInlist();

    @JsProperty
    protected native void setInlist(@Nullable String inlist);

    @JsProperty
    public native @Nullable String getPrefix();

    @JsProperty
    protected native void setPrefix(@Nullable String prefix);

    @JsProperty
    public native @Nullable String getProperty();

    @JsProperty
    protected native void setProperty(@Nullable String property);

    @JsProperty
    public native @Nullable String getResource();

    @JsProperty
    protected native void setResource(@Nullable String resource);

    @JsProperty
    public native @Nullable String getTypeof();

    @JsProperty
    protected native void setTypeof(@Nullable String typeof);

    @JsProperty
    public native @Nullable String getVocab();

    @JsProperty
    protected native void setVocab(@Nullable String vocab);

    // Non-standard Attributes

    @JsProperty
    public native @Nullable String getAutoCapitalize();

    @JsProperty
    protected native void setAutoCapitalize(@Nullable String autoCapitalize);

    @JsProperty
    public native @Nullable String getAutoCorrect();

    @JsProperty
    protected native void setAutoCorrect(@Nullable String autoCorrect);

    @JsProperty
    public native @Nullable String getAutoSave();

    @JsProperty
    protected native void setAutoSave(@Nullable String autoSave);

    @JsProperty
    public native @Nullable String getColor();

    @JsProperty
    protected native void setColor(@Nullable String color);

    @JsProperty
    public native @Nullable String getItemProp();

    @JsProperty
    protected native void setItemProp(@Nullable String itemProp);

    @JsProperty
    public native boolean isItemScope();

    @JsProperty
    protected native void setItemScope(boolean itemScope);

    @JsProperty
    public native @Nullable String getItemType();

    @JsProperty
    protected native void setItemType(@Nullable String itemType);

    @JsProperty
    public native @Nullable String getItemID();

    @JsProperty
    protected native void setItemID(@Nullable String itemID);

    @JsProperty
    public native @Nullable String getItemRef();

    @JsProperty
    protected native void setItemRef(@Nullable String itemRef);

    @JsProperty
    public native int getResults();

    @JsProperty
    protected native void setResults(int results);

    @JsProperty
    public native @Nullable String getSecurity();

    @JsProperty
    protected native void setSecurity(@Nullable String security);

    @JsProperty
    public native boolean isUnselectable();

    @JsProperty
    protected native void setUnselectable(boolean unselectable);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnAnimationStart();

    @JsOverlay
    public final T onAnimationStart(@Nullable final ReactEventHandler handler) {
        setOnAnimationStart(handler);
        return self();
    }

    @JsProperty
    protected native void setOnAnimationStart(@Nullable final ReactEventHandler onCopy);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnAnimationEnd();

    @JsOverlay
    public final T onAnimationEnd(@Nullable final ReactEventHandler handler) {
        setOnAnimationEnd(handler);
        return self();
    }

    @JsProperty
    protected native void setOnAnimationEnd(@Nullable final ReactEventHandler onCopy);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnAnimationIteration();

    @JsOverlay
    public final T onAnimationIteration(@Nullable final ReactEventHandler handler) {
        setOnAnimationIteration(handler);
        return self();
    }

    @JsProperty
    protected native void setOnAnimationIteration(@Nullable final ReactEventHandler onCopy);

    // Clipboard Events

    @JsProperty
    @Nullable
    public native ClipboardEventHandler getOnCopy();

    @JsProperty
    protected native void setOnCopy(@Nullable final ClipboardEventHandler onCopy);

    @JsProperty
    @Nullable
    public native ClipboardEventHandler getOnCut();

    @JsProperty
    protected native void setOnCut(@Nullable final ClipboardEventHandler onCut);

    @JsProperty
    @Nullable
    public native ClipboardEventHandler getOnPaste();

    @JsProperty
    protected native void setOnPaste(@Nullable final ClipboardEventHandler onPaste);

    // Composition Events

    @JsProperty
    @Nullable
    public native CompositionEventHandler getOnCompositionEnd();

    @JsProperty
    protected native void setOnCompositionEnd(@Nullable final CompositionEventHandler onCompositionEnd);

    @JsProperty
    @Nullable
    public native CompositionEventHandler getOnCompositionStart();

    @JsProperty
    protected native void setOnCompositionStart(@Nullable final CompositionEventHandler onCompositionStart);

    @JsProperty
    @Nullable
    public native CompositionEventHandler getOnCompositionUpdate();

    @JsProperty
    protected native void setOnCompositionUpdate(@Nullable final CompositionEventHandler onCompositionUpdate);

    // Focus Events

    @JsProperty
    @Nullable
    public native FocusEventHandler getOnFocus();

    @JsProperty
    protected native void setOnFocus(@Nullable final FocusEventHandler onFocus);

    @JsProperty
    @Nullable
    public native FocusEventHandler getOnBlur();

    @JsProperty
    protected native void setOnBlur(@Nullable final FocusEventHandler onBlur);

    // Form Events

    @JsProperty
    @Nullable
    public native FormEventHandler getOnChange();

    @JsProperty
    protected native void setOnChange(@Nullable final FormEventHandler onChange);

    @JsProperty
    @Nullable
    public native FormEventHandler getOnInput();

    @JsProperty
    protected native void setOnInput(@Nullable final FormEventHandler onInput);

    @JsProperty
    @Nullable
    public native FormEventHandler getOnSubmit();

    @JsProperty
    protected native void setOnSubmit(@Nullable final FormEventHandler onSubmit);

    // Image Events

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnLoad();

    @JsProperty
    protected native void setOnLoad(@Nullable final ReactEventHandler onLoad);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnError();

    @JsProperty
    protected native void setOnError(@Nullable final ReactEventHandler onError);

    // Keyboard Events

    @JsProperty
    @Nullable
    public native KeyboardEventHandler getOnKeyDown();

    @JsProperty
    protected native void setOnKeyDown(@Nullable final KeyboardEventHandler onKeyDown);

    @JsProperty
    @Nullable
    public native KeyboardEventHandler getOnKeyPress();

    @JsProperty
    protected native void setOnKeyPress(@Nullable final KeyboardEventHandler onKeyPress);

    @JsProperty
    @Nullable
    public native KeyboardEventHandler getOnKeyUp();

    @JsProperty
    protected native void setOnKeyUp(@Nullable final KeyboardEventHandler onKeyUp);

    // Media Events

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnAbort();

    @JsProperty
    protected native void setOnAbort(@Nullable final ReactEventHandler onAbort);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnCanPlay();

    @JsProperty
    protected native void setOnCanPlay(@Nullable final ReactEventHandler onCanPlay);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnCanPlayThrough();

    @JsProperty
    protected native void setOnCanPlayThrough(@Nullable final ReactEventHandler onCanPlayThrough);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnDurationChange();

    @JsProperty
    protected native void setOnDurationChange(@Nullable final ReactEventHandler onDurationChange);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnEmptied();

    @JsProperty
    protected native void setOnEmptied(@Nullable final ReactEventHandler onEmptied);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnEncrypted();

    @JsProperty
    protected native void setOnEncrypted(@Nullable final ReactEventHandler onEncrypted);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnEnded();

    @JsProperty
    protected native void setOnEnded(@Nullable final ReactEventHandler onEnded);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnLoadedData();

    @JsProperty
    protected native void setOnLoadedData(@Nullable final ReactEventHandler onLoadedData);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnLoadedMetadata();

    @JsProperty
    protected native void setOnLoadedMetadata(@Nullable final ReactEventHandler onLoadedMetadata);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnLoadStart();

    @JsProperty
    protected native void setOnLoadStart(@Nullable final ReactEventHandler onLoadStart);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnPause();

    @JsProperty
    protected native void setOnPause(@Nullable final ReactEventHandler onPause);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnPlay();

    @JsProperty
    protected native void setOnPlay(@Nullable final ReactEventHandler onPlay);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnPlaying();

    @JsProperty
    protected native void setOnPlaying(@Nullable final ReactEventHandler onPlaying);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnProgress();

    @JsProperty
    protected native void setOnProgress(@Nullable final ReactEventHandler onProgress);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnRateChange();

    @JsProperty
    protected native void setOnRateChange(@Nullable final ReactEventHandler onRateChange);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnSeeked();

    @JsProperty
    protected native void setOnSeeked(@Nullable final ReactEventHandler onSeeked);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnSeeking();

    @JsProperty
    protected native void setOnSeeking(@Nullable final ReactEventHandler onSeeking);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnStalled();

    @JsProperty
    protected native void setOnStalled(@Nullable final ReactEventHandler onStalled);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnSuspend();

    @JsProperty
    protected native void setOnSuspend(@Nullable final ReactEventHandler onSuspend);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnTimeUpdate();

    @JsProperty
    protected native void setOnTimeUpdate(@Nullable final ReactEventHandler onTimeUpdate);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnVolumeChange();

    @JsProperty
    protected native void setOnVolumeChange(@Nullable final ReactEventHandler onVolumeChange);

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnWaiting();

    @JsProperty
    protected native void setOnWaiting(@Nullable final ReactEventHandler onWaiting);

    // MouseEvents

    @JsProperty
    public native @Nullable MouseEventHandler getOnClick();

    @JsProperty
    protected native void setOnClick(@Nullable final MouseEventHandler onClick);

    @JsProperty
    @Nullable
    public native MouseEventHandler getOnContextMenu();

    @JsProperty
    protected native void setOnContextMenu(@Nullable final MouseEventHandler onContextMenu);

    @JsProperty
    @Nullable
    public native MouseEventHandler getOnDoubleClick();

    @JsProperty
    protected native void setOnDoubleClick(@Nullable final MouseEventHandler onDoubleClick);

    @JsProperty
    @Nullable
    public native DragEventHandler getOnDrag();

    @JsProperty
    protected native void setOnDrag(@Nullable final DragEventHandler onDrag);

    @JsProperty
    @Nullable
    public native DragEventHandler getOnDragEnd();

    @JsProperty
    protected native void setOnDragEnd(@Nullable final DragEventHandler onDragEnd);

    @JsProperty
    @Nullable
    public native DragEventHandler getOnDragEnter();

    @JsProperty
    protected native void setOnDragEnter(@Nullable final DragEventHandler onDragEnter);

    @JsProperty
    @Nullable
    public native DragEventHandler getOnDragExit();

    @JsProperty
    protected native void setOnDragExit(@Nullable final DragEventHandler onDragExit);

    @JsProperty
    @Nullable
    public native DragEventHandler getOnDragLeave();

    @JsProperty
    protected native void setOnDragLeave(@Nullable final DragEventHandler onDragLeave);

    @JsProperty
    @Nullable
    public native DragEventHandler getOnDragOver();

    @JsProperty
    protected native void setOnDragOver(@Nullable final DragEventHandler onDragOver);

    @JsProperty
    @Nullable
    public native DragEventHandler getOnDragStart();

    @JsProperty
    protected native void setOnDragStart(@Nullable final DragEventHandler onDragStart);

    @JsProperty
    @Nullable
    public native DragEventHandler getOnDrop();

    @JsProperty
    protected native void setOnDrop(@Nullable final DragEventHandler onDrop);

    @JsProperty
    @Nullable
    public native MouseEventHandler getOnMouseDown();

    @JsProperty
    protected native void setOnMouseDown(@Nullable final MouseEventHandler onMouseDown);

    @JsProperty
    @Nullable
    public native MouseEventHandler getOnMouseEnter();

    @JsProperty
    protected native void setOnMouseEnter(@Nullable final MouseEventHandler onMouseEnter);

    @JsProperty
    @Nullable
    public native MouseEventHandler getOnMouseLeave();

    @JsProperty
    protected native void setOnMouseLeave(@Nullable final MouseEventHandler onMouseLeave);

    @JsProperty
    @Nullable
    public native MouseEventHandler getOnMouseMove();

    @JsProperty
    protected native void setOnMouseMove(@Nullable final MouseEventHandler onMouseMove);

    @JsProperty
    @Nullable
    public native MouseEventHandler getOnMouseOut();

    @JsProperty
    protected native void setOnMouseOut(@Nullable final MouseEventHandler onMouseOut);

    @JsProperty
    @Nullable
    public native MouseEventHandler getOnMouseOver();

    @JsProperty
    protected native void setOnMouseOver(@Nullable final MouseEventHandler onMouseOver);

    @JsProperty
    @Nullable
    public native MouseEventHandler getOnMouseUp();

    @JsProperty
    protected native void setOnMouseUp(@Nullable final MouseEventHandler onMouseUp);

    // Selection Events

    @JsProperty
    @Nullable
    public native ReactEventHandler getOnSelect();

    @JsProperty
    protected native void setOnSelect(@Nullable final ReactEventHandler onSelect);

    // Touch Events

    @JsProperty
    @Nullable
    public native TouchEventHandler getOnTouchCancel();

    @JsProperty
    protected native void setOnTouchCancel(@Nullable final TouchEventHandler onTouchCancel);

    @JsProperty
    @Nullable
    public native TouchEventHandler getOnTouchEnd();

    @JsProperty
    protected native void setOnTouchEnd(@Nullable final TouchEventHandler onTouchEnd);

    @JsProperty
    @Nullable
    public native TouchEventHandler getOnTouchMove();

    @JsProperty
    protected native void setOnTouchMove(@Nullable final TouchEventHandler onTouchMove);

    @JsProperty
    @Nullable
    public native TouchEventHandler getOnTouchStart();

    @JsProperty
    protected native void setOnTouchStart(@Nullable final TouchEventHandler onTouchStart);

    // UI Events
    @JsProperty
    @Nullable
    public native UIEventHandler getOnScroll();

    @JsProperty
    protected native void setOnScroll(@Nullable final UIEventHandler onScroll);

    // Wheel Events
    @JsProperty
    @Nullable
    public native WheelEventHandler getOnWheel();

    @JsProperty
    protected native void setOnWheel(@Nullable WheelEventHandler onWheel);

    @JsOverlay
    public final T onWheel(@Nullable final WheelEventHandler onWheel) {
        setOnWheel(onWheel);
        return self();
    }

    @JsOverlay
    @SuppressWarnings("SelfAlwaysReturnsThis")
    protected final T self() {
        return Js.uncheckedCast(this);
    }
}
