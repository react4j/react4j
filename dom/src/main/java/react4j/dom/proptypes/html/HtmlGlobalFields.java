package react4j.dom.proptypes.html;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.intellij.lang.annotations.MagicConstant;
import react4j.React;
import react4j.ReactNode;
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
import static org.realityforge.braincheck.Guards.*;

@SuppressWarnings( "unused" )
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class HtmlGlobalFields<T extends HtmlGlobalFields>
{
  @Nullable
  public ReactNode children;
  @Nullable
  public String key;
  @Nullable
  public RefConsumer ref;

  @JsOverlay
  @Nonnull
  public final T ref( @Nonnull final RefConsumer callback )
  {
    ref = callback;
    return self();
  }

  @JsOverlay
  @Nonnull
  public final T key( final int value )
  {
    return key( String.valueOf( value ) );
  }

  @JsOverlay
  @Nonnull
  public final T key( @Nonnull final String value )
  {
    key = value;
    return self();
  }

  @JsOverlay
  @Nonnull
  public final T dangerouslySetInnerHTML( @Nonnull final String html )
  {
    setDangerouslySetInnerHTML( JsPropertyMap.of( "__html", html ) );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final T prop( @Nonnull final String key, @Nullable final Any value )
  {
    Js.asPropertyMap( this ).set( key, value );
    return self();
  }

  @JsProperty
  protected native void setDangerouslySetInnerHTML( @Nonnull JsPropertyMap<Object> __html );

  //React Specific
  @JsProperty
  public native boolean isDefaultChecked();

  @JsProperty
  protected native void setDefaultChecked( boolean defaultChecked );

  @JsProperty
  public native String getDefaultValue();

  @JsProperty
  protected native void setDefaultValue( String defaultValue );

  // Standard HTML Attributes
  @JsProperty
  public native String getAccept();

  @JsProperty
  protected native void setAccept( String accept );

  @JsProperty
  public native String getAcceptCharset();

  @JsProperty
  protected native void setAcceptCharset( String acceptCharset );

  @JsProperty
  public native String getAccessKey();

  @JsOverlay
  @Nonnull
  public final T accessKey( String accessKey )
  {
    setAccessKey( accessKey );
    return self();
  }

  @JsProperty
  protected native void setAccessKey( String accessKey );

  @JsProperty
  public native String getAction();

  @JsProperty
  protected native void setAction( String action );

  @JsProperty
  public native boolean isAllowFullScreen();

  @JsProperty
  protected native void setAllowFullScreen( boolean allowFullScreen );

  @JsProperty
  public native boolean isAllowTransparency();

  @JsProperty
  protected native void setAllowTransparency( boolean allowTransparency );

  @JsProperty
  public native String getAlt();

  @JsProperty
  protected native void setAlt( String alt );

  @JsProperty
  public native boolean isAsync();

  @JsProperty
  protected native void setAsync( boolean async );

  @JsProperty
  public native String getAutoComplete();

  @JsProperty
  protected native void setAutoComplete( String autoComplete );

  @JsProperty
  public native boolean isAutoFocus();

  @JsProperty
  protected native void setAutoFocus( boolean autoFocus );

  @JsProperty
  public native boolean isAutoPlay();

  @JsProperty
  protected native void setAutoPlay( boolean autoPlay );

  @JsProperty
  public native boolean isCapture();

  @JsProperty
  protected native void setCapture( boolean capture );

  @JsProperty
  public native String getCellPadding();

  @JsProperty
  protected native void setCellPadding( String cellPadding );

  @JsProperty
  public native String getCellSpacing();

  @JsProperty
  protected native void setCellSpacing( String cellSpacing );

  @JsProperty
  public native String getCharSet();

  @JsProperty
  protected native void setCharSet( String charSet );

  @JsProperty
  public native String getChallenge();

  @JsProperty
  protected native void setChallenge( String challenge );

  @JsProperty
  public native boolean isChecked();

  @JsProperty
  protected native void setChecked( boolean checked );

  @JsProperty
  public native String getClassID();

  @JsProperty
  protected native void setClassID( String classID );

  @JsProperty
  public native String getClassName();

  @SuppressWarnings( "StringConcatenationInLoop" )
  @JsOverlay
  @Nonnull
  public final T className( final String... elements )
  {
    String className = null;
    for ( final String element : elements )
    {
      if ( null != element )
      {
        if ( React.shouldCheckInvariants() )
        {
          invariant( () -> !element.equals( "" ) && !element.matches( "^\\s+$" ),
                     () -> "Empty classname element supplied. Remove the element." );
          invariant( () -> !element.matches( "^\\s.*$" ),
                     () -> "Classname element '" + element + "' starts with whitespace. Remove the whitespace." );
          invariant( () -> !element.matches( "^.*\\s$" ),
                     () -> "Classname element '" + element + "' ends with whitespace. Remove the whitespace." );
        }
        if ( null == className )
        {
          className = element;
        }
        else
        {
          className += " " + element;
        }
      }
    }
    if ( null != className )
    {
      setClassName( className );
    }
    return self();
  }

  @JsProperty
  protected native void setClassName( String className );

  @JsProperty
  public native int getCols();

  @JsProperty
  protected native void setCols( int cols );

  @JsProperty
  public native int getColSpan();

  @JsProperty
  protected native void setColSpan( int colSpan );

  @JsProperty
  public native String getContent();

  @JsProperty
  protected native void setContent( String content );

  @JsProperty
  public native boolean isContentEditable();

  @JsOverlay
  @Nonnull
  public final T contentEditable( boolean contentEditable )
  {
    setContentEditable( contentEditable );
    return self();
  }

  @JsProperty
  protected native void setContentEditable( boolean contentEditable );

  @JsProperty
  public native String getContextMenu();

  @JsOverlay
  @Nonnull
  public final T contextMenu( String contextMenu )
  {
    setContextMenu( contextMenu );
    return self();
  }

  @JsProperty
  protected native void setContextMenu( String contextMenu );

  @JsProperty
  public native boolean isControls();

  @JsProperty
  protected native void setControls( boolean controls );

  @JsProperty
  public native String getCoords();

  @JsProperty
  protected native void setCoords( String coords );

  @JsProperty
  public native String getCrossOrigin();

  @JsProperty
  protected native void setCrossOrigin( String crossOrigin );

  @JsProperty
  public native String getData();

  @JsProperty
  protected native void setData( String data );

  @JsProperty
  public native String getDateTime();

  @JsProperty
  protected native void setDateTime( String dateTime );

  //@JsProperty public native boolean is_default();
  //@JsProperty protected native void set_default(boolean _default);

  @JsProperty
  public native boolean isDefer();

  @JsProperty
  protected native void setDefer( boolean defer );

  @JsProperty
  public native String getDir();

  @JsOverlay
  @Nonnull
  public final T dir( String dir )
  {
    setDir( dir );
    return self();
  }

  @JsProperty
  protected native void setDir( String dir );

  @JsProperty
  public native boolean isDisabled();

  @JsProperty
  protected native void setDisabled( boolean disabled );

  @JsProperty
  public native String getDownload();

  @JsProperty
  protected native void setDownload( String download );

  @JsProperty
  public native boolean isDraggable();

  @JsOverlay
  @Nonnull
  public final T draggable( boolean draggable )
  {
    setDraggable( draggable );
    return self();
  }

  @JsProperty
  protected native void setDraggable( boolean draggable );

  @JsProperty
  public native String getEncType();

  @JsProperty
  protected native void setEncType( String encType );

  @JsProperty
  public native String getForm();

  @JsProperty
  protected native void setForm( String form );

  @JsProperty
  public native String getFormAction();

  @JsProperty
  protected native void setFormAction( String formAction );

  @JsProperty
  public native String getFormEncType();

  @JsProperty
  protected native void setFormEncType( String formEncType );

  @JsProperty
  public native String getFormMethod();

  @JsProperty
  protected native void setFormMethod( String formMethod );

  @JsProperty
  public native boolean isFormNoValidate();

  @JsProperty
  protected native void setFormNoValidate( boolean formNoValidate );

  @JsProperty
  public native String getFormTarget();

  @JsProperty
  protected native void setFormTarget( String formTarget );

  @JsProperty
  public native String getFrameBorder();

  @JsProperty
  protected native void setFrameBorder( String frameBorder );

  @JsProperty
  public native String getHeaders();

  @JsProperty
  protected native void setHeaders( String headers );

  @JsProperty
  public native String getHeight();

  @JsProperty
  protected native void setHeight( String height );

  @JsProperty
  public native boolean isHidden();

  @JsOverlay
  @Nonnull
  public final T hidden( boolean hidden )
  {
    setHidden( hidden );
    return self();
  }

  @JsProperty
  protected native void setHidden( boolean hidden );

  @JsProperty
  public native int getHigh();

  @JsProperty
  protected native void setHigh( int high );

  @JsProperty
  public native String getHref();

  @JsProperty
  protected native void setHref( String href );

  @JsProperty
  public native String getHrefLang();

  @JsProperty
  protected native void setHrefLang( String hrefLang );

  @JsProperty
  public native String getHtmlFor();

  @JsProperty
  protected native void setHtmlFor( String htmlFor );

  @JsProperty
  public native String getHttpEquiv();

  @JsProperty
  protected native void setHttpEquiv( String httpEquiv );

  @JsProperty
  public native String getIcon();

  @JsProperty
  protected native void setIcon( String icon );

  @JsProperty
  public native String getId();

  @JsOverlay
  @Nonnull
  public final T id( String id )
  {
    setId( id );
    return self();
  }

  @JsProperty
  protected native void setId( String id );

  @JsProperty
  public native String getInputMode();

  @JsProperty
  protected native void setInputMode( String inputMode );

  @JsProperty
  public native String getIntegrity();

  @JsProperty
  protected native void setIntegrity( String integrity );

  @JsProperty
  public native String getIs();

  @JsProperty
  protected native void setIs( String is );

  @JsProperty
  public native String getKeyParams();

  @JsProperty
  protected native void setKeyParams( String keyParams );

  @JsProperty
  public native String getKeyType();

  @JsProperty
  protected native void setKeyType( String keyType );

  @JsProperty
  public native String getKind();

  @JsProperty
  protected native void setKind( String kind );

  @JsProperty
  public native String getLabel();

  @JsProperty
  protected native void setLabel( String label );

  @JsProperty
  public native String getLang();

  @JsOverlay
  @Nonnull
  public final T lang( String lang )
  {
    setLang( lang );
    return self();
  }

  @JsProperty
  protected native void setLang( String lang );

  @JsProperty
  public native String getList();

  @JsProperty
  protected native void setList( String list );

  @JsProperty
  public native boolean isLoop();

  @JsProperty
  protected native void setLoop( boolean loop );

  @JsProperty
  public native int getLow();

  @JsProperty
  protected native void setLow( int low );

  @JsProperty
  public native String getManifest();

  @JsProperty
  protected native void setManifest( String manifest );

  @JsProperty
  public native int getMarginHeight();

  @JsProperty
  protected native void setMarginHeight( int marginHeight );

  @JsProperty
  public native int getMarginWidth();

  @JsProperty
  protected native void setMarginWidth( int marginWidth );

  @JsProperty
  public native String getMax();

  @JsProperty
  protected native void setMax( String max );

  @JsProperty
  public native int getMaxLength();

  @JsProperty
  protected native void setMaxLength( int maxLength );

  @JsProperty
  public native String getMedia();

  @JsProperty
  protected native void setMedia( String media );

  @JsProperty
  public native String getMediaGroup();

  @JsProperty
  protected native void setMediaGroup( String mediaGroup );

  @JsProperty
  public native String getMethod();

  @JsProperty
  protected native void setMethod( String method );

  @JsProperty
  public native String getMin();

  @JsProperty
  protected native void setMin( String min );

  @JsProperty
  public native int getMinLength();

  @JsProperty
  protected native void setMinLength( int minLength );

  @JsProperty
  public native boolean isMultiple();

  @JsProperty
  protected native void setMultiple( boolean multiple );

  @JsProperty
  public native boolean isMuted();

  @JsProperty
  protected native void setMuted( boolean muted );

  @JsProperty
  public native String getName();

  @JsProperty
  protected native void setName( String name );

  @JsProperty
  public native boolean isNoValidate();

  @JsProperty
  protected native void setNoValidate( boolean noValidate );

  @JsProperty
  public native boolean isOpen();

  @JsProperty
  protected native void setOpen( boolean open );

  @JsProperty
  public native int getOptimum();

  @JsProperty
  protected native void setOptimum( int optimum );

  @JsProperty
  public native String getPattern();

  @JsProperty
  protected native void setPattern( String pattern );

  @JsProperty
  public native String getPlaceholder();

  @JsProperty
  protected native void setPlaceholder( String placeholder );

  @JsProperty
  public native String getPoster();

  @JsProperty
  protected native void setPoster( String poster );

  @JsProperty
  public native String getPreload();

  @JsProperty
  protected native void setPreload( String preload );

  @JsProperty
  public native String getRadioGroup();

  @JsProperty
  protected native void setRadioGroup( String radioGroup );

  @JsProperty
  public native boolean isReadOnly();

  @JsProperty
  protected native void setReadOnly( boolean readOnly );

  @JsProperty
  public native String getRel();

  @JsProperty
  protected native void setRel( String rel );

  @JsProperty
  public native String getReferrerPolicy();

  @JsProperty
  protected native void setReferrerPolicy( String rel );

  @JsProperty
  public native boolean isRequired();

  @JsProperty
  protected native void setRequired( boolean required );

  @JsProperty
  public native String getRole();

  @JsProperty
  protected native void setRole( String role );

  @JsProperty
  public native int getRows();

  @JsProperty
  protected native void setRows( int rows );

  @JsProperty
  public native int getRowSpan();

  @JsProperty
  protected native void setRowSpan( int rowSpan );

  @JsProperty
  public native String getSandbox();

  @JsProperty
  protected native void setSandbox( String sandbox );

  @JsProperty
  public native String getScope();

  @JsProperty
  protected native void setScope( String scope );

  @JsProperty
  public native boolean isScoped();

  @JsProperty
  protected native void setScoped( boolean scoped );

  @JsProperty
  public native String getScrolling();

  @JsProperty
  protected native void setScrolling( String scrolling );

  @JsProperty
  public native boolean isSeamless();

  @JsProperty
  protected native void setSeamless( boolean seamless );

  @JsProperty
  public native boolean isSelected();

  @JsProperty
  protected native void setSelected( boolean selected );

  @JsProperty
  public native String getShape();

  @JsProperty
  protected native void setShape( String shape );

  @JsProperty
  public native int getSize();

  @JsProperty
  protected native void setSize( int size );

  @JsProperty
  public native String getSizes();

  @JsProperty
  protected native void setSizes( String sizes );

  @JsProperty
  public native int getSpan();

  @JsProperty
  protected native void setSpan( int span );

  @JsProperty
  public native boolean isSpellCheck();

  @JsOverlay
  @Nonnull
  public final T spellCheck( boolean spellCheck )
  {
    setSpellCheck( spellCheck );
    return self();
  }

  @JsProperty
  protected native void setSpellCheck( boolean spellCheck );

  @JsProperty
  public native String getSrc();

  @JsProperty
  protected native void setSrc( String src );

  @JsProperty
  public native String getSrcDoc();

  @JsProperty
  protected native void setSrcDoc( String srcDoc );

  @JsProperty
  public native String getSrcLang();

  @JsProperty
  protected native void setSrcLang( String srcLang );

  @JsProperty
  public native String getSrcSet();

  @JsProperty
  protected native void setSrcSet( String srcSet );

  @JsProperty
  public native int getStart();

  @JsProperty
  protected native void setStart( int start );

  @JsProperty
  public native String getStep();

  @JsProperty
  protected native void setStep( String step );

  @JsProperty
  public native CssProps getStyle();

  @JsOverlay
  @Nonnull
  public final T style( CssProps style )
  {
    setStyle( style );
    return self();
  }

  @JsProperty
  protected native void setStyle( CssProps style );

  @JsProperty
  public native String getSummary();

  @JsProperty
  protected native void setSummary( String summary );

  @JsProperty
  public native int getTabIndex();

  @JsOverlay
  @Nonnull
  public final T tabIndex( int tabIndex )
  {
    setTabIndex( tabIndex );
    return self();
  }

  @JsProperty
  protected native void setTabIndex( int tabIndex );

  @JsProperty
  public native String getTarget();

  @JsProperty
  protected native void setTarget( String target );

  @JsProperty
  public native String getTitle();

  @JsOverlay
  @Nonnull
  public final T title( String title )
  {
    setTitle( title );
    return self();
  }

  @JsProperty
  protected native void setTitle( String title );

  @JsProperty
  public native String getType();

  @JsProperty
  protected native void setType( @Nonnull @MagicConstant( valuesFromClass = InputType.class ) String type );

  @JsProperty
  public native String getTranslate();

  @JsOverlay
  @Nonnull
  public final T translate( @Nonnull @MagicConstant( valuesFromClass = YesNo.class ) final String s )
  {
    setTranslate( s );
    return self();
  }

  @JsProperty
  protected native void setTranslate( String translate );

  @JsProperty
  public native String getUseMap();

  @JsProperty
  protected native void setUseMap( String useMap );

  @JsProperty
  public native String getValue();

  @JsProperty
  protected native void setValue( String value );

  @JsProperty
  public native String getWidth();

  @JsProperty
  protected native void setWidth( String width );

  @JsProperty
  public native String getWmode();

  @JsProperty
  protected native void setWmode( String wmode );

  @JsProperty
  public native String getWrap();

  @JsProperty
  protected native void setWrap( String wrap );

  // RDFa Attributes

  @JsProperty
  public native String getAbout();

  @JsProperty
  protected native void setAbout( String about );

  @JsProperty
  public native String getDatatype();

  @JsProperty
  protected native void setDatatype( String datatype );

  @JsProperty
  public native String getInlist();

  @JsProperty
  protected native void setInlist( String inlist );

  @JsProperty
  public native String getPrefix();

  @JsProperty
  protected native void setPrefix( String prefix );

  @JsProperty
  public native String getProperty();

  @JsProperty
  protected native void setProperty( String property );

  @JsProperty
  public native String getResource();

  @JsProperty
  protected native void setResource( String resource );

  @JsProperty
  public native String getTypeof();

  @JsProperty
  protected native void setTypeof( String typeof );

  @JsProperty
  public native String getVocab();

  @JsProperty
  protected native void setVocab( String vocab );

  // Non-standard Attributes

  @JsProperty
  public native String getAutoCapitalize();

  @JsProperty
  protected native void setAutoCapitalize( String autoCapitalize );

  @JsProperty
  public native String getAutoCorrect();

  @JsProperty
  protected native void setAutoCorrect( String autoCorrect );

  @JsProperty
  public native String getAutoSave();

  @JsProperty
  protected native void setAutoSave( String autoSave );

  @JsProperty
  public native String getColor();

  @JsProperty
  protected native void setColor( String color );

  @JsProperty
  public native String getItemProp();

  @JsProperty
  protected native void setItemProp( String itemProp );

  @JsProperty
  public native boolean isItemScope();

  @JsProperty
  protected native void setItemScope( boolean itemScope );

  @JsProperty
  public native String getItemType();

  @JsProperty
  protected native void setItemType( String itemType );

  @JsProperty
  public native String getItemID();

  @JsProperty
  protected native void setItemID( String itemID );

  @JsProperty
  public native String getItemRef();

  @JsProperty
  protected native void setItemRef( String itemRef );

  @JsProperty
  public native int getResults();

  @JsProperty
  protected native void setResults( int results );

  @JsProperty
  public native String getSecurity();

  @JsProperty
  protected native void setSecurity( String security );

  @JsProperty
  public native boolean isUnselectable();

  @JsProperty
  protected native void setUnselectable( boolean unselectable );

  // Clipboard Events

  @JsProperty
  @Nullable
  public native ClipboardEventHandler getOnCopy();

  @JsProperty
  protected native void setOnCopy( @Nullable final ClipboardEventHandler onCopy );

  @JsProperty
  @Nullable
  public native ClipboardEventHandler getOnCut();

  @JsProperty
  protected native void setOnCut( @Nullable final ClipboardEventHandler onCut );

  @JsProperty
  @Nullable
  public native ClipboardEventHandler getOnPaste();

  @JsProperty
  protected native void setOnPaste( @Nullable final ClipboardEventHandler onPaste );

  // Composition Events

  @JsProperty
  @Nullable
  public native CompositionEventHandler getOnCompositionEnd();

  @JsProperty
  protected native void setOnCompositionEnd( @Nullable final CompositionEventHandler onCompositionEnd );

  @JsProperty
  @Nullable
  public native CompositionEventHandler getOnCompositionStart();

  @JsProperty
  protected native void setOnCompositionStart( @Nullable final CompositionEventHandler onCompositionStart );

  @JsProperty
  @Nullable
  public native CompositionEventHandler getOnCompositionUpdate();

  @JsProperty
  protected native void setOnCompositionUpdate( @Nullable final CompositionEventHandler onCompositionUpdate );

  // Focus Events

  @JsProperty
  @Nullable
  public native FocusEventHandler getOnFocus();

  @JsProperty
  protected native void setOnFocus( @Nullable final FocusEventHandler onFocus );

  @JsProperty
  @Nullable
  public native FocusEventHandler getOnBlur();

  @JsProperty
  protected native void setOnBlur( @Nullable final FocusEventHandler onBlur );

  // Form Events

  @JsProperty
  @Nullable
  public native FormEventHandler getOnChange();

  @JsProperty
  protected native void setOnChange( @Nullable final FormEventHandler onChange );

  @JsProperty
  @Nullable
  public native FormEventHandler getOnInput();

  @JsProperty
  protected native void setOnInput( @Nullable final FormEventHandler onInput );

  @JsProperty
  @Nullable
  public native FormEventHandler getOnSubmit();

  @JsProperty
  protected native void setOnSubmit( @Nullable final FormEventHandler onSubmit );

  // Image Events

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnLoad();

  @JsProperty
  protected native void setOnLoad( @Nullable final ReactEventHandler onLoad );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnError();

  @JsProperty
  protected native void setOnError( @Nullable final ReactEventHandler onError );

  // Keyboard Events

  @JsProperty
  @Nullable
  public native KeyboardEventHandler getOnKeyDown();

  @JsProperty
  protected native void setOnKeyDown( @Nullable final KeyboardEventHandler onKeyDown );

  @JsProperty
  @Nullable
  public native KeyboardEventHandler getOnKeyPress();

  @JsProperty
  protected native void setOnKeyPress( @Nullable final KeyboardEventHandler onKeyPress );

  @JsProperty
  @Nullable
  public native KeyboardEventHandler getOnKeyUp();

  @JsProperty
  protected native void setOnKeyUp( @Nullable final KeyboardEventHandler onKeyUp );

  // Media Events

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnAbort();

  @JsProperty
  protected native void setOnAbort( @Nullable final ReactEventHandler onAbort );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnCanPlay();

  @JsProperty
  protected native void setOnCanPlay( @Nullable final ReactEventHandler onCanPlay );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnCanPlayThrough();

  @JsProperty
  protected native void setOnCanPlayThrough( @Nullable final ReactEventHandler onCanPlayThrough );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnDurationChange();

  @JsProperty
  protected native void setOnDurationChange( @Nullable final ReactEventHandler onDurationChange );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnEmptied();

  @JsProperty
  protected native void setOnEmptied( @Nullable final ReactEventHandler onEmptied );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnEncrypted();

  @JsProperty
  protected native void setOnEncrypted( @Nullable final ReactEventHandler onEncrypted );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnEnded();

  @JsProperty
  protected native void setOnEnded( @Nullable final ReactEventHandler onEnded );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnLoadedData();

  @JsProperty
  protected native void setOnLoadedData( @Nullable final ReactEventHandler onLoadedData );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnLoadedMetadata();

  @JsProperty
  protected native void setOnLoadedMetadata( @Nullable final ReactEventHandler onLoadedMetadata );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnLoadStart();

  @JsProperty
  protected native void setOnLoadStart( @Nullable final ReactEventHandler onLoadStart );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnPause();

  @JsProperty
  protected native void setOnPause( @Nullable final ReactEventHandler onPause );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnPlay();

  @JsProperty
  protected native void setOnPlay( @Nullable final ReactEventHandler onPlay );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnPlaying();

  @JsProperty
  protected native void setOnPlaying( @Nullable final ReactEventHandler onPlaying );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnProgress();

  @JsProperty
  protected native void setOnProgress( @Nullable final ReactEventHandler onProgress );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnRateChange();

  @JsProperty
  protected native void setOnRateChange( @Nullable final ReactEventHandler onRateChange );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnSeeked();

  @JsProperty
  protected native void setOnSeeked( @Nullable final ReactEventHandler onSeeked );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnSeeking();

  @JsProperty
  protected native void setOnSeeking( @Nullable final ReactEventHandler onSeeking );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnStalled();

  @JsProperty
  protected native void setOnStalled( @Nullable final ReactEventHandler onStalled );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnSuspend();

  @JsProperty
  protected native void setOnSuspend( @Nullable final ReactEventHandler onSuspend );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnTimeUpdate();

  @JsProperty
  protected native void setOnTimeUpdate( @Nullable final ReactEventHandler onTimeUpdate );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnVolumeChange();

  @JsProperty
  protected native void setOnVolumeChange( @Nullable final ReactEventHandler onVolumeChange );

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnWaiting();

  @JsProperty
  protected native void setOnWaiting( @Nullable final ReactEventHandler onWaiting );

  // MouseEvents

  @JsProperty
  public native MouseEventHandler getOnClick();

  @JsProperty
  protected native void setOnClick( @Nullable final MouseEventHandler onClick );

  @JsProperty
  @Nullable
  public native MouseEventHandler getOnContextMenu();

  @JsProperty
  protected native void setOnContextMenu( @Nullable final MouseEventHandler onContextMenu );

  @JsProperty
  @Nullable
  public native MouseEventHandler getOnDoubleClick();

  @JsProperty
  protected native void setOnDoubleClick( @Nullable final MouseEventHandler onDoubleClick );

  @JsProperty
  @Nullable
  public native DragEventHandler getOnDrag();

  @JsProperty
  protected native void setOnDrag( @Nullable final DragEventHandler onDrag );

  @JsProperty
  @Nullable
  public native DragEventHandler getOnDragEnd();

  @JsProperty
  protected native void setOnDragEnd( @Nullable final DragEventHandler onDragEnd );

  @JsProperty
  @Nullable
  public native DragEventHandler getOnDragEnter();

  @JsProperty
  protected native void setOnDragEnter( @Nullable final DragEventHandler onDragEnter );

  @JsProperty
  @Nullable
  public native DragEventHandler getOnDragExit();

  @JsProperty
  protected native void setOnDragExit( @Nullable final DragEventHandler onDragExit );

  @JsProperty
  @Nullable
  public native DragEventHandler getOnDragLeave();

  @JsProperty
  protected native void setOnDragLeave( @Nullable final DragEventHandler onDragLeave );

  @JsProperty
  @Nullable
  public native DragEventHandler getOnDragOver();

  @JsProperty
  protected native void setOnDragOver( @Nullable final DragEventHandler onDragOver );

  @JsProperty
  @Nullable
  public native DragEventHandler getOnDragStart();

  @JsProperty
  protected native void setOnDragStart( @Nullable final DragEventHandler onDragStart );

  @JsProperty
  @Nullable
  public native DragEventHandler getOnDrop();

  @JsProperty
  protected native void setOnDrop( @Nullable final DragEventHandler onDrop );

  @JsProperty
  @Nullable
  public native MouseEventHandler getOnMouseDown();

  @JsProperty
  protected native void setOnMouseDown( @Nullable final MouseEventHandler onMouseDown );

  @JsProperty
  @Nullable
  public native MouseEventHandler getOnMouseEnter();

  @JsProperty
  protected native void setOnMouseEnter( @Nullable final MouseEventHandler onMouseEnter );

  @JsProperty
  @Nullable
  public native MouseEventHandler getOnMouseLeave();

  @JsProperty
  protected native void setOnMouseLeave( @Nullable final MouseEventHandler onMouseLeave );

  @JsProperty
  @Nullable
  public native MouseEventHandler getOnMouseMove();

  @JsProperty
  protected native void setOnMouseMove( @Nullable final MouseEventHandler onMouseMove );

  @JsProperty
  @Nullable
  public native MouseEventHandler getOnMouseOut();

  @JsProperty
  protected native void setOnMouseOut( @Nullable final MouseEventHandler onMouseOut );

  @JsProperty
  @Nullable
  public native MouseEventHandler getOnMouseOver();

  @JsProperty
  protected native void setOnMouseOver( @Nullable final MouseEventHandler onMouseOver );

  @JsProperty
  @Nullable
  public native MouseEventHandler getOnMouseUp();

  @JsProperty
  protected native void setOnMouseUp( @Nullable final MouseEventHandler onMouseUp );

  // Selection Events

  @JsProperty
  @Nullable
  public native ReactEventHandler getOnSelect();

  @JsProperty
  protected native void setOnSelect( @Nullable final ReactEventHandler onSelect );

  // Touch Events

  @JsProperty
  @Nullable
  public native TouchEventHandler getOnTouchCancel();

  @JsProperty
  protected native void setOnTouchCancel( @Nullable final TouchEventHandler onTouchCancel );

  @JsProperty
  @Nullable
  public native TouchEventHandler getOnTouchEnd();

  @JsProperty
  protected native void setOnTouchEnd( @Nullable final TouchEventHandler onTouchEnd );

  @JsProperty
  @Nullable
  public native TouchEventHandler getOnTouchMove();

  @JsProperty
  protected native void setOnTouchMove( @Nullable final TouchEventHandler onTouchMove );

  @JsProperty
  @Nullable
  public native TouchEventHandler getOnTouchStart();

  @JsProperty
  protected native void setOnTouchStart( @Nullable final TouchEventHandler onTouchStart );

  // UI Events
  @JsProperty
  @Nullable
  public native UIEventHandler getOnScroll();

  @JsProperty
  protected native void setOnScroll( @Nullable final UIEventHandler onScroll );

  // Wheel Events
  @JsProperty
  @Nullable
  public native WheelEventHandler getOnWheel();

  @JsProperty
  protected native void setOnWheel( WheelEventHandler onWheel );

  @JsOverlay
  @Nonnull
  public final T onWheel( @Nullable final WheelEventHandler onWheel )
  {
    setOnWheel( onWheel );
    return self();
  }

  @JsOverlay
  @Nonnull
  protected final T self()
  {
    return Js.uncheckedCast( this );
  }
}
