package react4j.dom.proptypes.html;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;
import react4j.dom.proptypes.cssPropertyTypes.AnimationFillMode;
import react4j.dom.proptypes.cssPropertyTypes.Visibility;

/**
 * This class represents properties of the CSS style object.
 *
 * NOTE: this is very much a work in progress. Further refinements will include
 * defining enumerations for many of the properties
 */
@SuppressWarnings("unused")
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class CssProps {
    @JsProperty
    public native @Nullable String getAnimationFillMode();

    @JsProperty
    public native void setAnimationFillMode(@Nullable String mode);

    @JsOverlay
    public final CssProps animationFillMode(@AnimationFillMode final String mode) {
        setAnimationFillMode(mode);
        return this;
    }

    @JsProperty
    public native @Nullable String getBackgroundColor();

    @JsProperty
    public native void setBackgroundColor(@Nullable String color);

    @JsOverlay
    public final CssProps backgroundColor(@Nullable String color) {
        setBackgroundColor(color);
        return this;
    }

    @JsProperty
    public native int getBoxFlex();

    @JsProperty
    public native void setBoxFlex(int boxFlex);

    @JsOverlay
    public final CssProps boxFlex(int boxFlex) {
        setBoxFlex(boxFlex);
        return this;
    }

    @JsProperty
    public native int getBoxFlexGroup();

    @JsProperty
    public native void setBoxFlexGroup(int boxFlexGroup);

    @JsOverlay
    public final CssProps boxFlexGroup(int boxFlexGroup) {
        setBoxFlexGroup(boxFlexGroup);
        return this;
    }

    @JsProperty
    public native @Nullable String getBottom();

    @JsProperty
    public native void setBottom(@Nullable String bottom);

    @JsOverlay
    public final CssProps bottom(@Nullable String bottom) {
        setBottom(bottom);
        return this;
    }

    @JsProperty
    public native int getColumnCount();

    @JsProperty
    public native void setColumnCount(int columnCount);

    @JsOverlay
    public final CssProps columnCount(int columnCount) {
        setColumnCount(columnCount);
        return this;
    }

    @JsProperty
    public native int getFlex();

    @JsProperty
    public native void setFlex(int flex);

    @JsOverlay
    public final CssProps flex(int flex) {
        setFlex(flex);
        return this;
    }

    @JsProperty
    public native int getFlexGrow();

    @JsProperty
    public native void setFlexGrow(int flexGrow);

    @JsOverlay
    public final CssProps flexGrow(int flexGrow) {
        setFlexGrow(flexGrow);
        return this;
    }

    @JsProperty
    public native int getFlexShrink();

    @JsProperty
    public native void setFlexShrink(int flexShrink);

    @JsOverlay
    public final CssProps flexShrink(int flexShrink) {
        setFlexShrink(flexShrink);
        return this;
    }

    @JsProperty
    public native void setFontWeight(double fontWeight);

    @JsOverlay
    public final CssProps fontWeight(double fontWeight) {
        setFontWeight(fontWeight);
        return this;
    }

    @JsProperty
    public native void setFontWeight(@Nullable String fontWeight);

    @JsOverlay
    public final CssProps fontWeight(@Nullable String fontWeight) {
        setFontWeight(fontWeight);
        return this;
    }

    @JsProperty
    public native double getLineClamp();

    @JsProperty
    public native void setLineClamp(double lineClamp);

    @JsOverlay
    public final CssProps lineClamp(double lineClamp) {
        setLineClamp(lineClamp);
        return this;
    }

    @JsProperty
    public native @Nullable String getLineHeight();

    @JsProperty
    public native void setLineHeight(@Nullable String lineHeight);

    @JsOverlay
    public final CssProps lineHeight(@Nullable String lineHeight) {
        setLineHeight(lineHeight);
        return this;
    }

    @JsProperty
    public native double getOpacity();

    @JsProperty
    public native void setOpacity(double opacity);

    @JsOverlay
    public final CssProps opacity(double opacity) {
        setOpacity(opacity);
        return this;
    }

    @JsProperty
    public native int getOrder();

    @JsProperty
    public native void setOrder(int order);

    @JsOverlay
    public final CssProps order(int order) {
        setOrder(order);
        return this;
    }

    @JsProperty
    public native int getOrphans();

    @JsProperty
    public native void setOrphans(int orphans);

    @JsOverlay
    public final CssProps orphans(int orphans) {
        setOrphans(orphans);
        return this;
    }

    @JsProperty
    public native int getWidows();

    @JsProperty
    public native void setWidows(int widows);

    @JsOverlay
    public final CssProps widows(int widows) {
        setWidows(widows);
        return this;
    }

    @JsProperty(name = "zIndex")
    public native double getZIndex();

    @JsProperty(name = "zIndex")
    public native void setZIndex(double zIndex);

    @JsOverlay
    public final CssProps zIndex(double zIndex) {
        setZIndex(zIndex);
        return this;
    }

    @JsProperty
    public native double getZoom();

    @JsProperty
    public native void setZoom(double zoom);

    @JsOverlay
    public final CssProps zoom(double zoom) {
        setZoom(zoom);
        return this;
    }

    @JsProperty
    public native @Nullable String getFontSize();

    @JsProperty
    public native void setFontSize(@Nullable String fontSize);

    @JsOverlay
    public final CssProps fontSize(@Nullable String fontSize) {
        setFontSize(fontSize);
        return this;
    }

    // SVG-related properties

    @JsProperty
    public native double getFillOpacity();

    @JsProperty
    public native void setFillOpacity(double fillOpacity);

    @JsOverlay
    public final CssProps fillOpacity(double fillOpacity) {
        setFillOpacity(fillOpacity);
        return this;
    }

    @JsProperty
    public native double getStrokeOpacity();

    @JsProperty
    public native void setStrokeOpacity(double strokeOpacity);

    @JsOverlay
    public final CssProps strokeOpacity(double strokeOpacity) {
        setStrokeOpacity(strokeOpacity);
        return this;
    }

    @JsProperty
    public native double getStrokeWidth();

    @JsProperty
    public native void setStrokeWidth(double strokeWidth);

    @JsOverlay
    public final CssProps strokeWidth(double strokeWidth) {
        setStrokeWidth(strokeWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getAlignContent();

    @JsProperty
    public native void setAlignContent(@Nullable String alignContent);

    /**
     * Aligns a flex container's lines within the flex container when there is extra space in the
     * cross-axis, similar to how justify-content aligns individual items within the main-axis.
     */
    @JsOverlay
    public final CssProps alignContent(@Nullable String alignContent) {
        setAlignContent(alignContent);
        return this;
    }

    @JsProperty
    public native @Nullable String getAlignItems();

    @JsProperty
    public native void setAlignItems(@Nullable String alignItems);

    /**
     * Sets the default alignment in the cross axis for all of the flex container's items,
     * including anonymous flex items, similarly to how justify-content aligns items along
     * the main axis.
     */
    @JsOverlay
    public final CssProps alignItems(@Nullable String alignItems) {
        setAlignItems(alignItems);
        return this;
    }

    @JsProperty
    public native @Nullable String getAlignSelf();

    @JsProperty
    public native void setAlignSelf(@Nullable String alignSelf);

    /**
     * Allows the default alignment to be overridden for individual flex items.
     */
    @JsOverlay
    public final CssProps alignSelf(@Nullable String alignSelf) {
        setAlignSelf(alignSelf);
        return this;
    }

    @JsProperty
    public native @Nullable String getAlignmentAdjust();

    @JsProperty
    public native void setAlignmentAdjust(@Nullable String alignmentAdjust);

    /**
     * This property allows precise alignment of elements, such as graphics, that do not have a
     * baseline-table or lack the desired baseline in their baseline-table. With the
     * alignment-adjust property, the position of the baseline identified by the
     * alignment-baseline can be explicitly determined. It also determines precisely the
     * alignment point for each glyph within a textual element.
     */
    @JsOverlay
    public final CssProps alignmentAdjust(@Nullable String alignmentAdjust) {
        setAlignmentAdjust(alignmentAdjust);
        return this;
    }

    @JsProperty
    public native @Nullable String getAlignmentBaseline();

    @JsProperty
    public native void setAlignmentBaseline(@Nullable String alignmentBaseline);

    @JsOverlay
    public final CssProps alignmentBaseline(@Nullable String alignmentBaseline) {
        setAlignmentBaseline(alignmentBaseline);
        return this;
    }

    @JsProperty
    public native @Nullable String getAnimationDelay();

    @JsProperty
    public native void setAnimationDelay(@Nullable String animationDelay);

    /**
     * Defines a length of time to elapse before an animation starts, allowing an animation to
     * begin execution some time after it is applied.
     */
    @JsOverlay
    public final CssProps animationDelay(@Nullable String animationDelay) {
        setAnimationDelay(animationDelay);
        return this;
    }

    @JsProperty
    public native @Nullable String getAnimationDirection();

    @JsProperty
    public native void setAnimationDirection(@Nullable String animationDirection);

    /*
     * Defines whether an animation should run in reverse on some or all cycles.
     */
    @JsOverlay
    public final CssProps animationDirection(@Nullable String animationDirection) {
        setAnimationDirection(animationDirection);
        return this;
    }

    @JsProperty
    public native @Nullable String getAnimationIterationCount();

    @JsProperty
    public native void setAnimationIterationCount(@Nullable String animationIterationCount);

    /**
     * Specifies how many times an animation cycle should play.
     */
    @JsOverlay
    public final CssProps animationIterationCount(@Nullable String animationIterationCount) {
        setAnimationIterationCount(animationIterationCount);
        return this;
    }

    @JsProperty
    public native @Nullable String getAnimationTimingFunction();

    @JsProperty
    public native void setAnimationTimingFunction(String animationTimingFunction);

    @JsOverlay
    public final CssProps animationTimingFunction(final String animationTimingFunction) {
        setAnimationTimingFunction(animationTimingFunction);
        return this;
    }

    @JsProperty
    public native @Nullable String getAnimationDuration();

    @JsProperty
    public native void setAnimationDuration(String animationDuration);

    @JsOverlay
    public final CssProps animationDuration(final String animationDuration) {
        setAnimationDuration(animationDuration);
        return this;
    }

    @JsProperty
    public native @Nullable String getAnimationName();

    @JsProperty
    public native void setAnimationName(@Nullable String animationName);

    /**
     * Defines the list of animations that apply to the element.
     */
    @JsOverlay
    public final CssProps animationName(@Nullable String animationName) {
        setAnimationName(animationName);
        return this;
    }

    @JsProperty
    public native @Nullable String getAnimationPlayState();

    @JsProperty
    public native void setAnimationPlayState(@Nullable String animationPlayState);

    /**
     * Defines whether an animation is running or paused.
     */
    @JsOverlay
    public final CssProps animationPlayState(@Nullable String animationPlayState) {
        setAnimationPlayState(animationPlayState);
        return this;
    }

    @JsProperty
    public native @Nullable String getAppearance();

    @JsProperty
    public native void setAppearance(@Nullable String appearance);

    /**
     * Allows changing the style of any element to platform-based interface elements or vice versa.
     */
    @JsOverlay
    public final CssProps appearance(@Nullable String appearance) {
        setAppearance(appearance);
        return this;
    }

    @JsProperty
    public native @Nullable String getBackfaceVisibility();

    @JsProperty
    public native void setBackfaceVisibility(@Nullable String backfaceVisibility);

    /**
     * Determines whether or not the "back" side of a transformed element is visible when facing
     * the viewer.
     */
    @JsOverlay
    public final CssProps backfaceVisibility(@Nullable String backfaceVisibility) {
        setBackfaceVisibility(backfaceVisibility);
        return this;
    }

    @JsProperty
    public native @Nullable String getBackgroundBlendMode();

    @JsProperty
    public native void setBackgroundBlendMode(@Nullable String backgroundBlendMode);

    /**
     * This property describes how the element's background images should blend with each other
     * and the element's background color. The value is a list of blend modes that corresponds
     * to each background image. Each element in the list will apply to the corresponding
     * element of background-image. If a property doesn't have enough comma-separated values to
     * match the number of layers, the UA must calculate its used value by repeating the list of
     * values until there are enough.
     */
    @JsOverlay
    public final CssProps backgroundBlendMode(@Nullable String backgroundBlendMode) {
        setBackgroundBlendMode(backgroundBlendMode);
        return this;
    }

    @JsProperty
    public native @Nullable String getBackgroundComposite();

    @JsProperty
    public native void setBackgroundComposite(@Nullable String backgroundComposite);

    @JsOverlay
    public final CssProps backgroundComposite(@Nullable String backgroundComposite) {
        setBackgroundComposite(backgroundComposite);
        return this;
    }

    @JsProperty
    public native @Nullable String getBackgroundImage();

    @JsProperty
    public native void setBackgroundImage(@Nullable String backgroundImage);

    /**
     * Applies one or more background images to an element. These can be any valid CSS image,
     * including url() paths to image files or CSS gradients.
     */
    @JsOverlay
    public final CssProps backgroundImage(@Nullable String backgroundImage) {
        setBackgroundImage(backgroundImage);
        return this;
    }

    @JsProperty
    public native @Nullable String getBackgroundOrigin();

    @JsProperty
    public native void setBackgroundOrigin(@Nullable String backgroundOrigin);

    /**
     * Specifies what the background-position property is relative to.
     */
    @JsOverlay
    public final CssProps backgroundOrigin(@Nullable String backgroundOrigin) {
        setBackgroundOrigin(backgroundOrigin);
        return this;
    }

    @JsProperty
    public native @Nullable String getBackgroundPositionX();

    @JsProperty
    public native void setBackgroundPositionX(@Nullable String backgroundPositionX);

    /**
     * Sets the horizontal position of a background image.
     */
    @JsOverlay
    public final CssProps backgroundPositionX(@Nullable String backgroundPositionX) {
        setBackgroundPositionX(backgroundPositionX);
        return this;
    }

    @JsProperty
    public native @Nullable String getBackgroundRepeat();

    @JsProperty
    public native void setBackgroundRepeat(@Nullable String backgroundRepeat);

    /**
     * Background-repeat defines if and how background images will be repeated after they have
     * been sized and positioned
     */
    @JsOverlay
    public final CssProps backgroundRepeat(@Nullable String backgroundRepeat) {
        setBackgroundRepeat(backgroundRepeat);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorder();

    @JsProperty
    public native void setBorder(@Nullable String border);

    /**
     * Shorthand property that defines the different properties of all four sides of an element's
     * border in a single declaration. It can be used to set border-width, border-style and
     * border-color, or a subset of these.
     */
    @JsOverlay
    public final CssProps border(@Nullable String border) {
        setBorder(border);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderRadius();

    @JsProperty
    public native void setBorderRadius(@Nullable String borderRadius);

    @JsOverlay
    public final CssProps borderRadius(@Nullable String borderRadius) {
        setBorderRadius(borderRadius);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderBottomLeftRadius();

    @JsProperty
    public native void setBorderBottomLeftRadius(@Nullable String borderBottomLeftRadius);

    /**
     * Defines the shape of the border of the bottom-left corner.
     */
    @JsOverlay
    public final CssProps borderBottomLeftRadius(@Nullable String borderBottomLeftRadius) {
        setBorderBottomLeftRadius(borderBottomLeftRadius);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderBottomRightRadius();

    @JsProperty
    public native void setBorderBottomRightRadius(@Nullable String borderBottomRightRadius);

    /**
     * Defines the shape of the border of the bottom-right corner.
     */
    @JsOverlay
    public final CssProps borderBottomRightRadius(@Nullable String borderBottomRightRadius) {
        setBorderBottomRightRadius(borderBottomRightRadius);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderBottomWidth();

    @JsProperty
    public native void setBorderBottomWidth(@Nullable String borderBottomWidth);

    /**
     * Sets the width of an element's bottom border. To set all four borders, use the border-width
     * shorthand property which sets the values simultaneously for border-top-width,
     * border-right-width, border-bottom-width, and border-left-width.
     */
    @JsOverlay
    public final CssProps borderBottomWidth(@Nullable String borderBottomWidth) {
        setBorderBottomWidth(borderBottomWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderCollapse();

    @JsProperty
    public native void setBorderCollapse(@Nullable String borderCollapse);

    /**
     * Border-collapse can be used for collapsing the borders between table cells
     */
    @JsOverlay
    public final CssProps borderCollapse(@Nullable String borderCollapse) {
        setBorderCollapse(borderCollapse);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderColor();

    @JsProperty
    public native void setBorderColor(@Nullable String borderColor);

    /**
     * The CSS border-color property sets the color of an element's four borders. This property can
     * have from one to four values, made up of the elementary properties:
     * - border-top-color
     * - border-right-color
     * - border-bottom-color
     * - border-left-color The default color is the currentColor of each of these values.
     * If you provide one value, it sets the color for the element. Two values set the horizontal
     * and vertical values, respectively. Providing three values sets the top, vertical, and bottom
     * values, in that order. Four values set all for sides: top, right, bottom, and left, in that
     * order.
     */
    @JsOverlay
    public final CssProps borderColor(@Nullable String borderColor) {
        setBorderColor(borderColor);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderCornerShape();

    @JsProperty
    public native void setBorderCornerShape(@Nullable String borderCornerShape);

    /**
     * Specifies different corner clipping effects, such as scoop (inner curves), bevel (straight
     * cuts) or notch (cut-off rectangles). Works along with border-radius to specify the size of each
     * corner effect.
     */
    @JsOverlay
    public final CssProps borderCornerShape(@Nullable String borderCornerShape) {
        setBorderCornerShape(borderCornerShape);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderImageSource();

    @JsProperty
    public native void setBorderImageSource(@Nullable String borderImageSource);

    /**
     * The property border-image-source is used to set the image to be used instead of the border
     * style. If this is set to none the border-style is used instead.
     */
    @JsOverlay
    public final CssProps borderImageSource(@Nullable String borderImageSource) {
        setBorderImageSource(borderImageSource);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderImageWidth();

    @JsProperty
    public native void setBorderImageWidth(@Nullable String borderImageWidth);

    /**
     * The border-image-width CSS property defines the offset to use for dividing the border image
     * in nine parts, the top-left corner, central top edge, top-right-corner, central right edge,
     * bottom-right corner, central bottom edge, bottom-left corner, and central right edge. They
     * represent inward distance from the top, right, bottom, and left edges.
     */
    @JsOverlay
    public final CssProps borderImageWidth(@Nullable String borderImageWidth) {
        setBorderImageWidth(borderImageWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderLeft();

    @JsProperty
    public native void setBorderLeft(@Nullable String borderLeft);

    /**
     * Shorthand property that defines the border-width, border-style and border-color of an
     * element's left border in a single declaration. Note that you can use the corresponding
     * longhand properties to set specific individual properties of the left
     * border: border-left-width, border-left-style and border-left-color.
     */
    @JsOverlay
    public final CssProps borderLeft(@Nullable String borderLeft) {
        setBorderLeft(borderLeft);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderLeftColor();

    @JsProperty
    public native void setBorderLeftColor(@Nullable String borderLeftColor);

    /**
     * The CSS border-left-color property sets the color of an element's left border. This page
     * explains the border-left-color value, but often you will find it more convenient to fix
     * the border's left color as part of a shorthand set, either border-left or border-color.
     * Colors can be defined several ways. For more information, see Usage.
     */
    @JsOverlay
    public final CssProps borderLeftColor(@Nullable String borderLeftColor) {
        setBorderLeftColor(borderLeftColor);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderLeftStyle();

    @JsProperty
    public native void setBorderLeftStyle(@Nullable String borderLeftStyle);

    /**
     * Sets the style of an element's left border. To set all four borders, use the shorthand
     * property, border-style. Otherwise, you can set the borders individually with
     * border-top-style, border-right-style, border-bottom-style, border-left-style.
     */
    @JsOverlay
    public final CssProps borderLeftStyle(@Nullable String borderLeftStyle) {
        setBorderLeftStyle(borderLeftStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderLeftWidth();

    @JsProperty
    public native void setBorderLeftWidth(@Nullable String borderLeftWidth);

    /**
     * Sets the width of an element's left border. To set all four borders, use the border-width
     * shorthand property which sets the values simultaneously for border-top-width,
     * border-right-width, border-bottom-width, and border-left-width.
     */
    @JsOverlay
    public final CssProps borderLeftWidth(@Nullable String borderLeftWidth) {
        setBorderLeftWidth(borderLeftWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderRight();

    @JsProperty
    public native void setBorderRight(@Nullable String borderRight);

    /**
     * Shorthand property that defines the border-width, border-style and border-color of an
     * element's right border in a single declaration. Note that you can use the corresponding
     * longhand properties to set specific individual properties of the right border:
     * border-right-width, border-right-style and border-right-color.
     */
    @JsOverlay
    public final CssProps borderRight(@Nullable String borderRight) {
        setBorderRight(borderRight);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderRightColor();

    @JsProperty
    public native void setBorderRightColor(@Nullable String borderRightColor);

    /**
     * Sets the color of an element's right border. This page explains the border-right-color
     * value, but often you will find it more convenient to fix the border's right color as
     * part of a shorthand set, either border-right or border-color.
     * Colors can be defined several ways. For more information, see Usage.
     */
    @JsOverlay
    public final CssProps borderRightColor(@Nullable String borderRightColor) {
        setBorderRightColor(borderRightColor);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderRightStyle();

    @JsProperty
    public native void setBorderRightStyle(@Nullable String borderRightStyle);

    /**
     * Sets the style of an element's right border. To set all four borders, use the shorthand
     * property, border-style. Otherwise, you can set the borders individually with
     * border-top-style, border-right-style, border-bottom-style, border-left-style.
     */
    @JsOverlay
    public final CssProps borderRightStyle(@Nullable String borderRightStyle) {
        setBorderRightStyle(borderRightStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderRightWidth();

    @JsProperty
    public native void setBorderRightWidth(@Nullable String borderRightWidth);

    /**
     * Sets the width of an element's right border. To set all four borders, use the
     * border-width shorthand property which sets the values simultaneously for
     * border-top-width, border-right-width, border-bottom-width, and border-left-width.
     */
    @JsOverlay
    public final CssProps borderRightWidth(@Nullable String borderRightWidth) {
        setBorderRightWidth(borderRightWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderSpacing();

    @JsProperty
    public native void setBorderSpacing(@Nullable String borderSpacing);

    /**
     * Specifies the distance between the borders of adjacent cells.
     */
    @JsOverlay
    public final CssProps borderSpacing(@Nullable String borderSpacing) {
        setBorderSpacing(borderSpacing);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderStyle();

    @JsProperty
    public native void setBorderStyle(@Nullable String borderStyle);

    /**
     * Sets the style of an element's four borders. This property can have from one to four values.
     * With only one value, the value will be applied to all four borders; otherwise, this works
     * as a shorthand property for each of border-top-style, border-right-style,
     * border-bottom-style, border-left-style, where each border style may be assigned a separate value.
     */
    @JsOverlay
    public final CssProps borderStyle(@Nullable String borderStyle) {
        setBorderStyle(borderStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderTop();

    @JsProperty
    public native void setBorderTop(@Nullable String borderTop);

    /**
     * Shorthand property that defines the border-width, border-style and border-color of an
     * element's top border in a single declaration. Note that you can use the corresponding
     * longhand properties to set specific individual properties of the top border:
     * border-top-width, border-top-style and border-top-color.
     */
    @JsOverlay
    public final CssProps borderTop(@Nullable String borderTop) {
        setBorderTop(borderTop);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderTopColor();

    @JsProperty
    public native void setBorderTopColor(@Nullable String borderTopColor);

    /**
     * Sets the color of an element's top border. This page explains the border-top-color value,
     * but often you will find it more convenient to fix the border's top color as part of a
     * shorthand set, either border-top or border-color.
     * Colors can be defined several ways. For more information, see Usage.
     */
    @JsOverlay
    public final CssProps borderTopColor(@Nullable String borderTopColor) {
        setBorderTopColor(borderTopColor);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderTopLeftRadius();

    @JsProperty
    public native void setBorderTopLeftRadius(@Nullable String borderTopLeftRadius);

    /**
     * Sets the rounding of the top-left corner of the element.
     */
    @JsOverlay
    public final CssProps borderTopLeftRadius(@Nullable String borderTopLeftRadius) {
        setBorderTopLeftRadius(borderTopLeftRadius);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderTopRightRadius();

    @JsProperty
    public native void setBorderTopRightRadius(@Nullable String borderTopRightRadius);

    /**
     * Sets the rounding of the top-right corner of the element.
     */
    @JsOverlay
    public final CssProps borderTopRightRadius(@Nullable String borderTopRightRadius) {
        setBorderTopRightRadius(borderTopRightRadius);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderTopStyle();

    @JsProperty
    public native void setBorderTopStyle(@Nullable String borderTopStyle);

    /**
     * Sets the style of an element's top border. To set all four borders, use the shorthand
     * property, border-style. Otherwise, you can set the borders individually with
     * border-top-style, border-right-style, border-bottom-style, border-left-style.
     */
    @JsOverlay
    public final CssProps borderTopStyle(@Nullable String borderTopStyle) {
        setBorderTopStyle(borderTopStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderTopWidth();

    @JsProperty
    public native void setBorderTopWidth(@Nullable String borderTopWidth);

    /**
     * Sets the width of an element's top border. To set all four borders, use the shorthand
     * property, border-style. Otherwise, you can set the borders individually with
     * border-top-style, border-right-style, border-bottom-style, border-left-style.
     */
    @JsOverlay
    public final CssProps borderTopWidth(@Nullable String borderTopWidth) {
        setBorderTopWidth(borderTopWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getBorderWidth();

    @JsProperty
    public native void setBorderWidth(@Nullable String borderWidth);

    /**
     * Sets the width of an element's four borders. This property can have from one to four values.
     * This is a shorthand property for setting values simultaneously for border-top-width,
     * border-right-width, border-bottom-width, and border-left-width.
     */
    @JsOverlay
    public final CssProps borderWidth(@Nullable String borderWidth) {
        setBorderWidth(borderWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getBoxDecorationBreak();

    @JsProperty
    public native void setBoxDecorationBreak(@Nullable String boxDecorationBreak);

    /**
     * Breaks a box into fragments creating new borders, padding and repeating backgrounds or lets
     * it stay as a continuous box on a page break, column break, or, for inline elements, at a
     * line break.
     */
    @JsOverlay
    public final CssProps boxDecorationBreak(@Nullable String boxDecorationBreak) {
        setBoxDecorationBreak(boxDecorationBreak);
        return this;
    }

    @JsProperty
    public native @Nullable String getBoxLineProgression();

    @JsProperty
    public native void setBoxLineProgression(@Nullable String boxLineProgression);

    /**
     * Do not use. This property has been replaced by the flex-wrap property.
     * Gets or sets a value that specifies the direction to add successive rows or columns when
     * the value of box-lines is set to multiple.
     */
    @JsOverlay
    public final CssProps boxLineProgression(@Nullable String boxLineProgression) {
        setBoxLineProgression(boxLineProgression);
        return this;
    }

    @JsProperty
    public native @Nullable String getBoxLines();

    @JsProperty
    public native void setBoxLines(@Nullable String boxLines);

    /**
     * Do not use. This property has been replaced by the flex-wrap property.
     * Gets or sets a value that specifies whether child elements wrap onto multiple lines or
     * columns based on the space available in the object.
     */
    @JsOverlay
    public final CssProps boxLines(@Nullable String boxLines) {
        setBoxLines(boxLines);
        return this;
    }

    @JsProperty
    public native @Nullable String getBoxOrdinalGroup();

    @JsProperty
    public native void setBoxOrdinalGroup(@Nullable String boxOrdinalGroup);

    /**
     * Do not use. This property has been replaced by flex-order.
     * Specifies the ordinal group that a child element of the object belongs to. This ordinal
     * value identifies the display order (along the axis defined by the box-orient property)
     * for the group.
     */
    @JsOverlay
    public final CssProps boxOrdinalGroup(@Nullable String boxOrdinalGroup) {
        setBoxOrdinalGroup(boxOrdinalGroup);
        return this;
    }

    @JsProperty
    public native @Nullable String getBreakAfter();

    @JsProperty
    public native void setBreakAfter(@Nullable String breakAfter);

    /**
     * The CSS break-after property allows you to force a break on multi-column layouts. More
     * specifically, it allows you to force a break after an element. It allows you to determine
     * if a break should occur, and what type of break it should be. The break-after CSS
     * property describes how the page, column or region break behaves after the generated box.
     * If there is no generated box, the property is ignored.
     */
    @JsOverlay
    public final CssProps breakAfter(@Nullable String breakAfter) {
        setBreakAfter(breakAfter);
        return this;
    }

    @JsProperty
    public native @Nullable String getBreakBefore();

    @JsProperty
    public native void setBreakBefore(@Nullable String breakBefore);

    /**
     * Control page/column/region breaks that fall above a block of content
     */
    @JsOverlay
    public final CssProps breakBefore(@Nullable String breakBefore) {
        setBreakBefore(breakBefore);
        return this;
    }

    @JsProperty
    public native @Nullable String getBreakInside();

    @JsProperty
    public native void setBreakInside(@Nullable String breakInside);

    /**
     * Control page/column/region breaks that fall within a block of content
     */
    @JsOverlay
    public final CssProps breakInside(@Nullable String breakInside) {
        setBreakInside(breakInside);
        return this;
    }

    @JsProperty
    public native @Nullable String getClear();

    @JsProperty
    public native void setClear(@Nullable String clear);

    /**
     * The clear CSS property specifies if an element can be positioned next to or must be
     * positioned below the floating elements that precede it in the markup.
     */
    @JsOverlay
    public final CssProps clear(@Nullable String clear) {
        setClear(clear);
        return this;
    }

    @JsProperty
    public native @Nullable String getClipRule();

    @JsProperty
    public native void setClipRule(@Nullable String clipRule);

    /**
     * Clipping crops an graphic, so that only a portion of the graphic is rendered, or filled.
     * This clip-rule property, when used with the clip-path property, defines which clip rule,
     * or algorithm, to use when filling the different parts of a graphics.
     */
    @JsOverlay
    public final CssProps clipRule(@Nullable String clipRule) {
        setClipRule(clipRule);
        return this;
    }

    @JsProperty
    public native @Nullable String getColor();

    @JsProperty
    public native void setColor(@Nullable String color);

    /**
     * The color property sets the color of an element's foreground content (usually text),
     * accepting any standard CSS color from keywords and hex values to RGB(a) and HSL(a).
     */
    @JsOverlay
    public final CssProps color(@Nullable String color) {
        this.setColor(color);
        return this;
    }

    @JsProperty
    public native @Nullable String getColumnFill();

    @JsProperty
    public native void setColumnFill(@Nullable String columnFill);

    /**
     * Specifies how to fill columns (balanced or sequential).
     */
    @JsOverlay
    public final CssProps columnFill(@Nullable String columnFill) {
        setColumnFill(columnFill);
        return this;
    }

    @JsProperty
    public native @Nullable String getColumnGap();

    @JsProperty
    public native void setColumnGap(@Nullable String columnGap);

    /**
     * The column-gap property controls the width of the gap between columns in multi-column
     * elements.
     */
    @JsOverlay
    public final CssProps columnGap(@Nullable String columnGap) {
        setColumnGap(columnGap);
        return this;
    }

    @JsProperty
    public native @Nullable String getColumnRule();

    @JsProperty
    public native void setColumnRule(@Nullable String columnRule);

    /**
     * Sets the width, style, and color of the rule between columns.
     */
    @JsOverlay
    public final CssProps columnRule(@Nullable String columnRule) {
        setColumnRule(columnRule);
        return this;
    }

    @JsProperty
    public native @Nullable String getColumnRuleColor();

    @JsProperty
    public native void setColumnRuleColor(@Nullable String columnRuleColor);

    /**
     * Specifies the color of the rule between columns.
     */
    @JsOverlay
    public final CssProps columnRuleColor(@Nullable String columnRuleColor) {
        setColumnRuleColor(columnRuleColor);
        return this;
    }

    @JsProperty
    public native @Nullable String getColumnRuleWidth();

    @JsProperty
    public native void setColumnRuleWidth(@Nullable String columnRuleWidth);

    /**
     * Specifies the width of the rule between columns.
     */
    @JsOverlay
    public final CssProps columnRuleWidth(@Nullable String columnRuleWidth) {
        setColumnRuleWidth(columnRuleWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getColumnSpan();

    @JsProperty
    public native void setColumnSpan(@Nullable String columnSpan);

    /**
     * The column-span CSS property makes it possible for an element to span across all columns
     * when its value is set to all. An element that spans more than one column is called a
     * spanning element.
     */
    @JsOverlay
    public final CssProps columnSpan(@Nullable String columnSpan) {
        setColumnSpan(columnSpan);
        return this;
    }

    @JsProperty
    public native @Nullable String getColumnWidth();

    @JsProperty
    public native void setColumnWidth(@Nullable String columnWidth);

    /**
     * Specifies the width of columns in multi-column elements.
     */
    @JsOverlay
    public final CssProps columnWidth(@Nullable String columnWidth) {
        setColumnWidth(columnWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getColumns();

    @JsProperty
    public native void setColumns(@Nullable String columns);

    /**
     * This property is a shorthand property for setting column-width and/or column-count.
     */
    @JsOverlay
    public final CssProps columns(@Nullable String columns) {
        setColumns(columns);
        return this;
    }

    @JsProperty
    public native @Nullable String getCounterIncrement();

    @JsProperty
    public native void setCounterIncrement(@Nullable String counterIncrement);

    /**
     * The counter-increment property accepts one or more names of counters (identifiers), each
     * one optionally followed by an integer which specifies the value by which the counter
     * should be incremented (e.g. if the value is 2, the counter increases by 2 each time it is
     * invoked).
     */
    @JsOverlay
    public final CssProps counterIncrement(@Nullable String counterIncrement) {
        setCounterIncrement(counterIncrement);
        return this;
    }

    @JsProperty
    public native @Nullable String getCounterReset();

    @JsProperty
    public native void setCounterReset(@Nullable String counterReset);

    /**
     * The counter-reset property contains a list of one or more names of counters, each one
     * optionally followed by an integer (otherwise, the integer defaults to 0.) Each time the
     * given element is invoked, the counters specified by the property are set to the given
     * integer.
     */
    @JsOverlay
    public final CssProps counterReset(@Nullable String counterReset) {
        setCounterReset(counterReset);
        return this;
    }

    @JsProperty
    public native @Nullable String getCue();

    @JsProperty
    public native void setCue(@Nullable String cue);

    /**
     * The cue property specifies sound files (known as an "auditory icon") to be played by
     * speech media agents before and after presenting an element's content; if only one file
     * is specified, it is played both before and after. The volume at which the file(s)
     * should be played, relative to the volume of the main element, may also be specified.
     * The icon files may also be set separately with the cue-before and cue-after properties.
     */
    @JsOverlay
    public final CssProps cue(@Nullable String cue) {
        setCue(cue);
        return this;
    }

    @JsProperty
    public native @Nullable String getCueAfter();

    @JsProperty
    public native void setCueAfter(@Nullable String cueAfter);

    /**
     * The cue-after property specifies a sound file (known as an "auditory icon") to be played
     * by speech media agents after presenting an element's content; the volume at which the
     * file should be played may also be specified. The shorthand property cue sets cue sounds
     * for both before and after the element is presented.
     */
    @JsOverlay
    public final CssProps cueAfter(@Nullable String cueAfter) {
        setCueAfter(cueAfter);
        return this;
    }

    @JsProperty
    public native @Nullable String getDirection();

    @JsProperty
    public native void setDirection(@Nullable String direction);

    /**
     * The direction CSS property specifies the text direction/writing direction. The rtl is
     * used for Hebrew or Arabic text, the ltr is for other languages.
     */
    @JsOverlay
    public final CssProps direction(@Nullable String direction) {
        setDirection(direction);
        return this;
    }

    @JsProperty
    public native @Nullable String getDisplay();

    @JsProperty
    public native void setDisplay(@Nullable String display);

    /**
     * This property specifies the type of rendering box used for an element. It is a shorthand
     * property for many other display properties.
     */
    @JsOverlay
    public final CssProps display(@Nullable String display) {
        setDisplay(display);
        return this;
    }

    @JsProperty
    public native @Nullable String getFill();

    @JsProperty
    public native void setFill(@Nullable String fill);

    /**
     * The "fill" property paints the interior of the given graphical element. The area to be
     * painted consists of any areas inside the outline of the shape. To determine the inside of
     * the shape, all subpaths are considered, and the interior is determined according to the
     * rules associated with the current value of the "fill-rule" property. The zero-width
     * geometric outline of a shape is included in the area to be painted.
     */
    @JsOverlay
    public final CssProps fill(@Nullable String fill) {
        setFill(fill);
        return this;
    }

    @JsProperty
    public native @Nullable String getFillRule();

    @JsProperty
    public native void setFillRule(@Nullable String fillRule);

    /**
     * The "fill-rule" property indicates the algorithm which is to be used to determine what
     * parts of the canvas are included inside the shape. For a simple, non-intersecting path, it
     * is intuitively clear what region lies "inside"; however, for a more complex path, such as
     * a path that intersects itself or where one subpath encloses another, the interpretation of
     * "inside" is not so obvious.
     * The "fill-rule" property provides two options for how the inside of a shape is determined:
     */
    @JsOverlay
    public final CssProps fillRule(@Nullable String fillRule) {
        setFillRule(fillRule);
        return this;
    }

    @JsProperty
    public native @Nullable String getFilter();

    @JsProperty
    public native void setFilter(@Nullable String filter);

    /**
     * Applies various image processing effects. This property is largely unsupported. See
     * Compatibility section for more information.
     */
    @JsOverlay
    public final CssProps filter(@Nullable String filter) {
        setFilter(filter);
        return this;
    }

    @JsProperty
    public native @Nullable String getFlexBasis();

    @JsProperty
    public native void setFlexBasis(@Nullable String flexBasis);

    /**
     * The flex-basis CSS property describes the initial main size of the flex item before any free
     * space is distributed according to the flex factors described in the flex property
     * (flex-grow and flex-shrink).
     */
    @JsOverlay
    public final CssProps flexBasis(@Nullable String flexBasis) {
        setFlexBasis(flexBasis);
        return this;
    }

    @JsProperty
    public native @Nullable String getFlexDirection();

    @JsProperty
    public native void setFlexDirection(@Nullable String flexDirection);

    /**
     * The flex-direction CSS property describes how flex items are placed in the flex container,
     * by setting the direction of the flex container's main axis.
     */
    @JsOverlay
    public final CssProps flexDirection(@Nullable String flexDirection) {
        setFlexDirection(flexDirection);
        return this;
    }

    @JsProperty
    public native @Nullable String getFlexFlow();

    @JsProperty
    public native void setFlexFlow(@Nullable String flexFlow);

    /**
     * The flex-flow CSS property defines the flex container's main and cross axis. It is a
     * shorthand property for the flex-direction and flex-wrap properties.
     */
    @JsOverlay
    public final CssProps flexFlow(@Nullable String flexFlow) {
        setFlexFlow(flexFlow);
        return this;
    }

    @JsProperty
    public native @Nullable String getFlexItemAlign();

    @JsProperty
    public native void setFlexItemAlign(@Nullable String flexItemAlign);

    /**
     * Do not use. This property has been renamed to align-self
     * Specifies the alignment (perpendicular to the layout axis defined by flex-direction)
     * of child elements of the object.
     */
    @JsOverlay
    public final CssProps flexItemAlign(@Nullable String flexItemAlign) {
        setFlexItemAlign(flexItemAlign);
        return this;
    }

    @JsProperty
    public native @Nullable String getFlexOrder();

    @JsProperty
    public native void setFlexOrder(@Nullable String flexOrder);

    /**
     * Gets or sets a value that specifies the ordinal group that a flexbox element belongs to.
     * This ordinal value identifies the display order for the group.
     */
    @JsOverlay
    public final CssProps flexOrder(@Nullable String flexOrder) {
        setFlexOrder(flexOrder);
        return this;
    }

    @JsProperty(name = "float")
    public native @Nullable String getCssFloat();

    @JsProperty(name = "float")
    public native void setCssFloat(@Nullable String cssFloat);

    /**
     * Elements which have the style float are floated horizontally. These elements can move as
     * far to the left or right of the containing element. All elements after the floating
     * element will flow around it, but elements before the floating element are not impacted. If
     * several floating elements are placed after each other, they will float next to each other
     * as long as there is room.
     */
    @JsOverlay
    public final CssProps cssFloat(@Nullable String cssFloat) {
        setCssFloat(cssFloat);
        return this;
    }

    @JsProperty
    public native @Nullable String getFlowFrom();

    @JsProperty
    public native void setFlowFrom(@Nullable String flowFrom);

    /**
     * Flows content from a named flow (specified by a corresponding flow-into) through selected
     * elements to form a dynamic chain of layout regions.
     */
    @JsOverlay
    public final CssProps flowFrom(@Nullable String flowFrom) {
        setFlowFrom(flowFrom);
        return this;
    }

    @JsProperty
    public native @Nullable String getFont();

    @JsProperty
    public native void setFont(@Nullable String font);

    /**
     * The font property is shorthand that allows you to do one of two things: you can either set
     * up six of the most mature font properties in one line, or you can set one of a choice of
     * keywords to adopt a system font setting.
     */
    @JsOverlay
    public final CssProps font(@Nullable String font) {
        setFont(font);
        return this;
    }

    @JsProperty
    public native @Nullable String getFontFamily();

    @JsProperty
    public native void setFontFamily(@Nullable String fontFamily);

    /**
     * The font-family property allows one or more font family names and/or generic family names
     * to be specified for usage on the selected element(s)' text. The browser then goes through
     * the list; for each character in the selection it applies the first font family that has an
     * available glyph for that character.
     */
    @JsOverlay
    public final CssProps fontFamily(@Nullable String fontFamily) {
        setFontFamily(fontFamily);
        return this;
    }

    @JsProperty
    public native @Nullable String getFontKerning();

    @JsProperty
    public native void setFontKerning(@Nullable String fontKerning);

    /**
     * The font-kerning property allows contextual adjustment of inter-glyph spacing, i.e. the
     * spaces between the characters in text. This property controls metric kerning
     * - that utilizes adjustment data contained in the font. Optical Kerning is not supported as
     * yet.
     */
    @JsOverlay
    public final CssProps fontKerning(@Nullable String fontKerning) {
        setFontKerning(fontKerning);
        return this;
    }

    @JsProperty
    public native @Nullable String getFontSizeAdjust();

    @JsProperty
    public native void setFontSizeAdjust(@Nullable String fontSizeAdjust);

    /**
     * The font-size-adjust property adjusts the font-size of the fallback fonts defined with
     * font-family, so that the x-height is the same no matter what font is used. This preserves
     * the readability of the text when fallback happens.
     */
    @JsOverlay
    public final CssProps fontSizeAdjust(@Nullable String fontSizeAdjust) {
        setFontSizeAdjust(fontSizeAdjust);
        return this;
    }

    @JsProperty
    public native @Nullable String getFontStretch();

    @JsProperty
    public native void setFontStretch(@Nullable String fontStretch);

    /**
     * Allows you to expand or condense the widths for a normal, condensed, or expanded font face.
     */
    @JsOverlay
    public final CssProps fontStretch(@Nullable String fontStretch) {
        setFontStretch(fontStretch);
        return this;
    }

    @JsProperty
    public native @Nullable String getFontStyle();

    @JsProperty
    public native void setFontStyle(@Nullable String fontStyle);

    /**
     * The font-style property allows normal, italic, or oblique faces to be selected. Italic
     * forms are generally cursive in nature while oblique faces are typically sloped versions
     * of the regular face. Oblique faces can be simulated by artificially sloping the glyphs
     * of the regular face.
     */
    @JsOverlay
    public final CssProps fontStyle(@Nullable String fontStyle) {
        setFontStyle(fontStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getFontSynthesis();

    @JsProperty
    public native void setFontSynthesis(@Nullable String fontSynthesis);

    /**
     * This value specifies whether the user agent is allowed to synthesize bold or oblique font
     * faces when a font family lacks bold or italic faces.
     */
    @JsOverlay
    public final CssProps fontSynthesis(@Nullable String fontSynthesis) {
        setFontSynthesis(fontSynthesis);
        return this;
    }

    @JsProperty
    public native @Nullable String getFontVariant();

    @JsProperty
    public native void setFontVariant(@Nullable String fontVariant);

    /**
     * The font-variant property enables you to select the small-caps font within a font family.
     */
    @JsOverlay
    public final CssProps fontVariant(@Nullable String fontVariant) {
        setFontVariant(fontVariant);
        return this;
    }

    @JsProperty
    public native @Nullable String getFontVariantAlternates();

    @JsProperty
    public native void setFontVariantAlternates(@Nullable String fontVariantAlternates);

    /**
     * Fonts can provide alternate glyphs in addition to default glyph for a character. This
     * property provides control over the selection of these alternate glyphs.
     */
    @JsOverlay
    public final CssProps fontVariantAlternates(@Nullable String fontVariantAlternates) {
        setFontVariantAlternates(fontVariantAlternates);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridArea();

    @JsProperty
    public native void setGridArea(@Nullable String gridArea);

    /**
     * Lays out one or more grid items bound by 4 grid lines. Shorthand for setting
     * grid-column-start, grid-column-end, grid-row-start, and grid-row-end in a single
     * declaration.
     */
    @JsOverlay
    public final CssProps gridArea(@Nullable String gridArea) {
        setGridArea(gridArea);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridColumn();

    @JsProperty
    public native void setGridColumn(@Nullable String gridColumn);

    /**
     * Controls a grid item's placement in a grid area, particularly grid position and a grid span.
     * Shorthand for setting grid-column-start and grid-column-end in a single declaration.
     */
    @JsOverlay
    public final CssProps gridColumn(@Nullable String gridColumn) {
        setGridColumn(gridColumn);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridColumnEnd();

    @JsProperty
    public native void setGridColumnEnd(@Nullable String gridColumnEnd);

    /**
     * Controls a grid item's placement in a grid area as well as grid position and a grid span.
     * The grid-column-end property (with grid-row-start, grid-row-end, and grid-column-start)
     * determines a grid item's placement by specifying the grid lines of a grid item's grid area.
     */
    @JsOverlay
    public final CssProps gridColumnEnd(@Nullable String gridColumnEnd) {
        setGridColumnEnd(gridColumnEnd);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridColumnStart();

    @JsProperty
    public native void setGridColumnStart(@Nullable String gridColumnStart);

    /**
     * Determines a grid item's placement by specifying the starting grid lines of a grid item's
     * grid area . A grid item's placement in a grid area consists of a grid position and a grid
     * span. See also ( grid-row-start, grid-row-end, and grid-column-end)
     */
    @JsOverlay
    public final CssProps gridColumnStart(@Nullable String gridColumnStart) {
        setGridColumnStart(gridColumnStart);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridRow();

    @JsProperty
    public native void setGridRow(@Nullable String gridRow);

    /**
     * Gets or sets a value that indicates which row an element within a Grid should appear in.
     * Shorthand for setting grid-row-start and grid-row-end in a single declaration.
     */
    @JsOverlay
    public final CssProps gridRow(@Nullable String gridRow) {
        setGridRow(gridRow);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridRowEnd();

    @JsProperty
    public native void setGridRowEnd(@Nullable String gridRowEnd);

    /**
     * Determines a grid item's placement by specifying the block-end. A grid item's placement in
     * a grid area consists of a grid position and a grid span. The grid-row-end property
     * (with grid-row-start, grid-column-start, and grid-column-end) determines a grid item's
     * placement by specifying the grid lines of a grid item's grid area.
     */
    @JsOverlay
    public final CssProps gridRowEnd(@Nullable String gridRowEnd) {
        setGridRowEnd(gridRowEnd);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridRowPosition();

    @JsProperty
    public native void setGridRowPosition(@Nullable String gridRowPosition);

    /**
     * Specifies a row position based upon an integer location, string value, or desired row size.
     * css/properties/grid-row is used as short-hand for grid-row-position and grid-row-position
     */
    @JsOverlay
    public final CssProps gridRowPosition(@Nullable String gridRowPosition) {
        setGridRowPosition(gridRowPosition);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridRowSpan();

    @JsProperty
    public native void setGridRowSpan(@Nullable String gridRowSpan);

    @JsOverlay
    public final CssProps gridRowSpan(@Nullable String gridRowSpan) {
        setGridRowSpan(gridRowSpan);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridTemplateAreas();

    @JsProperty
    public native void setGridTemplateAreas(@Nullable String gridTemplateAreas);

    /**
     * Specifies named grid areas which are not associated with any particular grid item, but can
     * be referenced from the grid-placement properties. The syntax of the grid-template-areas
     * property also provides a visualization of the structure of the grid, making the overall
     * layout of the grid container easier to understand.
     */
    @JsOverlay
    public final CssProps gridTemplateAreas(@Nullable String gridTemplateAreas) {
        setGridTemplateAreas(gridTemplateAreas);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridTemplateColumns();

    @JsProperty
    public native void setGridTemplateColumns(@Nullable String gridTemplateColumns);

    /**
     * Specifies (with grid-template-rows) the line names and track sizing functions of the grid.
     * Each sizing function can be specified as a length, a percentage of the grid container's
     * size, a measurement of the contents occupying the column or row, or a fraction of the free
     * space in the grid.
     */
    @JsOverlay
    public final CssProps gridTemplateColumns(@Nullable String gridTemplateColumns) {
        setGridTemplateColumns(gridTemplateColumns);
        return this;
    }

    @JsProperty
    public native @Nullable String getGridTemplateRows();

    @JsProperty
    public native void setGridTemplateRows(@Nullable String gridTemplateRows);

    /**
     * Specifies (with grid-template-columns) the line names and track sizing functions of the
     * grid. Each sizing function can be specified as a length, a percentage of the grid
     * container's size, a measurement of the contents occupying the column or row, or a
     * fraction of the free space in the grid.
     */
    @JsOverlay
    public final CssProps gridTemplateRows(@Nullable String gridTemplateRows) {
        setGridTemplateRows(gridTemplateRows);
        return this;
    }

    @JsProperty
    public native @Nullable String getHeight();

    @JsProperty
    public native void setHeight(@Nullable String height);

    /**
     * Sets the height of an element. The content area of the element height does not include
     * the padding, border, and margin of the element.
     */
    @JsOverlay
    public final CssProps height(@Nullable String height) {
        setHeight(height);
        return this;
    }

    @JsProperty
    public native @Nullable String getHyphenateLimitChars();

    @JsProperty
    public native void setHyphenateLimitChars(@Nullable String hyphenateLimitChars);

    /**
     * Specifies the minimum number of characters in a hyphenated word
     */
    @JsOverlay
    public final CssProps hyphenateLimitChars(@Nullable String hyphenateLimitChars) {
        setHyphenateLimitChars(hyphenateLimitChars);
        return this;
    }

    @JsProperty
    public native @Nullable String getHyphenateLimitLines();

    @JsProperty
    public native void setHyphenateLimitLines(@Nullable String hyphenateLimitLines);

    /**
     * Indicates the maximum number of successive hyphenated lines in an element. The "no-limit"
     * value means that there is no limit.
     */
    @JsOverlay
    public final CssProps hyphenateLimitLines(@Nullable String hyphenateLimitLines) {
        setHyphenateLimitLines(hyphenateLimitLines);
        return this;
    }

    @JsProperty
    public native @Nullable String getHyphenateLimitZone();

    @JsProperty
    public native void setHyphenateLimitZone(@Nullable String hyphenateLimitZone);

    /**
     * Specifies the maximum amount of trailing whitespace (before justification) that may be
     * left in a line before hyphenation is triggered to pull part of a word from the next
     * line back up into the current one.
     */
    @JsOverlay
    public final CssProps hyphenateLimitZone(@Nullable String hyphenateLimitZone) {
        setHyphenateLimitZone(hyphenateLimitZone);
        return this;
    }

    @JsProperty
    public native @Nullable String getHyphens();

    @JsProperty
    public native void setHyphens(@Nullable String hyphens);

    /**
     * Specifies whether or not words in a sentence can be split by the use of a manual or
     * automatic hyphenation mechanism.
     */
    @JsOverlay
    public final CssProps hyphens(@Nullable String hyphens) {
        setHyphens(hyphens);
        return this;
    }

    @JsProperty
    public native @Nullable String getImeMode();

    @JsProperty
    public native void setImeMode(@Nullable String imeMode);

    @JsOverlay
    public final CssProps imeMode(@Nullable String imeMode) {
        setImeMode(imeMode);
        return this;
    }

    @JsProperty
    public native @Nullable String getLayoutGrid();

    @JsProperty
    public native void setLayoutGrid(@Nullable String layoutGrid);

    @JsOverlay
    public final CssProps layoutGrid(@Nullable String layoutGrid) {
        setLayoutGrid(layoutGrid);
        return this;
    }

    @JsProperty
    public native @Nullable String getLayoutGridChar();

    @JsProperty
    public native void setLayoutGridChar(@Nullable String layoutGridChar);

    @JsOverlay
    public final CssProps layoutGridChar(@Nullable String layoutGridChar) {
        setLayoutGridChar(layoutGridChar);
        return this;
    }

    @JsProperty
    public native @Nullable String getLayoutGridLine();

    @JsProperty
    public native void setLayoutGridLine(@Nullable String layoutGridLine);

    @JsOverlay
    public final CssProps layoutGridLine(@Nullable String layoutGridLine) {
        setLayoutGridLine(layoutGridLine);
        return this;
    }

    @JsProperty
    public native @Nullable String getLayoutGridMode();

    @JsProperty
    public native void setLayoutGridMode(@Nullable String layoutGridMode);

    @JsOverlay
    public final CssProps layoutGridMode(@Nullable String layoutGridMode) {
        setLayoutGridMode(layoutGridMode);
        return this;
    }

    @JsProperty
    public native @Nullable String getLayoutGridType();

    @JsProperty
    public native void setLayoutGridType(@Nullable String layoutGridType);

    @JsOverlay
    public final CssProps layoutGridType(@Nullable String layoutGridType) {
        setLayoutGridType(layoutGridType);
        return this;
    }

    @JsProperty
    public native @Nullable String getLeft();

    @JsProperty
    public native void setLeft(@Nullable String left);

    /**
     * Sets the left edge of an element
     */
    @JsOverlay
    public final CssProps left(@Nullable String left) {
        setLeft(left);
        return this;
    }

    @JsProperty
    public native @Nullable String getLetterSpacing();

    @JsProperty
    public native void setLetterSpacing(@Nullable String letterSpacing);

    /**
     * The letter-spacing CSS property specifies the spacing behavior between text characters.
     */
    @JsOverlay
    public final CssProps letterSpacing(@Nullable String letterSpacing) {
        setLetterSpacing(letterSpacing);
        return this;
    }

    @JsProperty
    public native @Nullable String getListStyle();

    @JsProperty
    public native void setListStyle(@Nullable String listStyle);

    /**
     * Shorthand property that sets the list-style-type, list-style-position and list-style-image
     * properties in one declaration.
     */
    @JsOverlay
    public final CssProps listStyle(@Nullable String listStyle) {
        setListStyle(listStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getListStyleImage();

    @JsProperty
    public native void setListStyleImage(@Nullable String listStyleImage);

    /**
     * This property sets the image that will be used as the list item marker. When the image is
     * available, it will replace the marker set with the 'list-style-type' marker. That also
     * means that if the image is not available, it will show the style specified by
     * list-style-property
     */
    @JsOverlay
    public final CssProps listStyleImage(@Nullable String listStyleImage) {
        setListStyleImage(listStyleImage);
        return this;
    }

    @JsProperty
    public native @Nullable String getListStylePosition();

    @JsProperty
    public native void setListStylePosition(@Nullable String listStylePosition);

    /**
     * Specifies if the list-item markers should appear inside or outside the content flow.
     */
    @JsOverlay
    public final CssProps listStylePosition(@Nullable String listStylePosition) {
        setListStylePosition(listStylePosition);
        return this;
    }

    @JsProperty
    public native @Nullable String getListStyleType();

    @JsProperty
    public native void setListStyleType(@Nullable String listStyleType);

    /**
     * Specifies the type of list-item marker in a list.
     */
    @JsOverlay
    public final CssProps listStyleType(@Nullable String listStyleType) {
        setListStyleType(listStyleType);
        return this;
    }

    @JsProperty
    public native @Nullable String getMargin();

    @JsProperty
    public native void setMargin(@Nullable String margin);

    /**
     * The margin property is shorthand to allow you to set all four margins of an element at once.
     * Its equivalent longhand properties are margin-top, margin-right, margin-bottom and
     * margin-left. Negative values are also allowed.
     */
    @JsOverlay
    public final CssProps margin(@Nullable String margin) {
        setMargin(margin);
        return this;
    }

    @JsProperty
    public native @Nullable String getMarginBottom();

    @JsProperty
    public native void setMarginBottom(@Nullable String marginBottom);

    /**
     * margin-bottom sets the bottom margin of an element.
     */
    @JsOverlay
    public final CssProps marginBottom(@Nullable String marginBottom) {
        setMarginBottom(marginBottom);
        return this;
    }

    @JsProperty
    public native @Nullable String getMarginLeft();

    @JsProperty
    public native void setMarginLeft(@Nullable String marginLeft);

    /**
     * margin-left sets the left margin of an element.
     */
    @JsOverlay
    public final CssProps marginLeft(@Nullable String marginLeft) {
        setMarginLeft(marginLeft);
        return this;
    }

    @JsProperty
    public native @Nullable String getMarginRight();

    @JsProperty
    public native void setMarginRight(@Nullable String marginRight);

    /**
     * margin-right sets the right margin of an element.
     */
    @JsOverlay
    public final CssProps marginRight(@Nullable String marginRight) {
        setMarginRight(marginRight);
        return this;
    }

    @JsProperty
    public native @Nullable String getMarginTop();

    @JsProperty
    public native void setMarginTop(@Nullable String marginTop);

    /**
     * margin-top sets the top margin of an element.
     */
    @JsOverlay
    public final CssProps marginTop(@Nullable String marginTop) {
        setMarginTop(marginTop);
        return this;
    }

    @JsProperty
    public native @Nullable String getMask();

    @JsProperty
    public native void setMask(@Nullable String mask);

    /**
     * This property is shorthand for setting mask-image, mask-mode, mask-repeat, mask-position,
     * mask-clip, mask-origin, mask-composite and mask-size. Omitted values are set to their
     * original properties' initial values.
     */
    @JsOverlay
    public final CssProps mask(@Nullable String mask) {
        setMask(mask);
        return this;
    }

    @JsProperty
    public native @Nullable String getMaskBorder();

    @JsProperty
    public native void setMaskBorder(@Nullable String maskBorder);

    /**
     * This property is shorthand for setting mask-border-source, mask-border-slice,
     * mask-border-width, mask-border-outset, and mask-border-repeat. Omitted values are set to
     * their original properties' initial values.
     */
    @JsOverlay
    public final CssProps maskBorder(@Nullable String maskBorder) {
        setMaskBorder(maskBorder);
        return this;
    }

    @JsProperty
    public native @Nullable String getMaskBorderRepeat();

    @JsProperty
    public native void setMaskBorderRepeat(@Nullable String maskBorderRepeat);

    /**
     * This property specifies how the images for the sides and the middle part of the mask image
     * are scaled and tiled. The first keyword applies to the horizontal sides, the second one
     * applies to the vertical ones. If the second keyword is absent, it is assumed to be the
     * same as the first, similar to the CSS border-image-repeat property.
     */
    @JsOverlay
    public final CssProps maskBorderRepeat(@Nullable String maskBorderRepeat) {
        setMaskBorderRepeat(maskBorderRepeat);
        return this;
    }

    @JsProperty
    public native @Nullable String getMaskBorderSlice();

    @JsProperty
    public native void setMaskBorderSlice(@Nullable String maskBorderSlice);

    /**
     * This property specifies inward offsets from the top, right, bottom, and left edges of the
     * mask image, dividing it into nine regions: four corners, four edges, and a middle. The
     * middle image part is discarded and treated as fully transparent black unless the fill
     * keyword is present. The four values set the top, right, bottom and left offsets in that
     * order, similar to the CSS border-image-slice property.
     */
    @JsOverlay
    public final CssProps maskBorderSlice(@Nullable String maskBorderSlice) {
        setMaskBorderSlice(maskBorderSlice);
        return this;
    }

    @JsProperty
    public native @Nullable String getMaskBorderSource();

    @JsProperty
    public native void setMaskBorderSource(@Nullable String maskBorderSource);

    /**
     * Specifies an image to be used as a mask. An image that is empty, fails to download, is
     * non-existent, or cannot be displayed is ignored and does not mask the element.
     */
    @JsOverlay
    public final CssProps maskBorderSource(@Nullable String maskBorderSource) {
        setMaskBorderSource(maskBorderSource);
        return this;
    }

    @JsProperty
    public native @Nullable String getMaskBorderWidth();

    @JsProperty
    public native void setMaskBorderWidth(@Nullable String maskBorderWidth);

    /**
     * This property sets the width of the mask box image, similar to the CSS border-image-width
     * property.
     */
    @JsOverlay
    public final CssProps maskBorderWidth(@Nullable String maskBorderWidth) {
        setMaskBorderWidth(maskBorderWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getMaskClip();

    @JsProperty
    public native void setMaskClip(@Nullable String maskClip);

    /**
     * Determines the mask painting area, which defines the area that is affected by the mask.
     * The painted content of an element may be restricted to this area.
     */
    @JsOverlay
    public final CssProps maskClip(@Nullable String maskClip) {
        setMaskClip(maskClip);
        return this;
    }

    @JsProperty
    public native @Nullable String getMaskOrigin();

    @JsProperty
    public native void setMaskOrigin(@Nullable String maskOrigin);

    /**
     * For elements rendered as a single box, specifies the mask positioning area. For elements
     * rendered as multiple boxes (e.g., inline boxes on several lines, boxes on several pages)
     * specifies which boxes box-decoration-break operates on to determine the mask positioning
     * area(s).
     */
    @JsOverlay
    public final CssProps maskOrigin(@Nullable String maskOrigin) {
        setMaskOrigin(maskOrigin);
        return this;
    }

    @JsProperty
    public native @Nullable String getMaxFontSize();

    @JsProperty
    public native void setMaxFontSize(@Nullable String maxFontSize);

    /**
     * This property must not be used. It is no longer included in any standard or standard track
     * specification, nor is it implemented in any browser. It is only used when the
     * text-align-last property is set to size. It controls allowed adjustments of font-size to
     * fit line content.
     */
    @JsOverlay
    public final CssProps maxFontSize(@Nullable String maxFontSize) {
        setMaxFontSize(maxFontSize);
        return this;
    }

    @JsProperty
    public native @Nullable String getMaxHeight();

    @JsProperty
    public native void setMaxHeight(@Nullable String maxHeight);

    /**
     * Sets the maximum height for an element. It prevents the height of the element to exceed
     * the specified value. If min-height is specified and is greater than max-height, max-height
     * is overridden.
     */
    @JsOverlay
    public final CssProps maxHeight(@Nullable String maxHeight) {
        setMaxHeight(maxHeight);
        return this;
    }

    @JsProperty
    public native @Nullable String getMaxWidth();

    @JsProperty
    public native void setMaxWidth(@Nullable String maxWidth);

    /**
     * Sets the maximum width for an element. It limits the width property to be larger than the
     * value specified in max-width.
     */
    @JsOverlay
    public final CssProps maxWidth(@Nullable String maxWidth) {
        setMaxWidth(maxWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getMinHeight();

    @JsProperty
    public native void setMinHeight(@Nullable String minHeight);

    /**
     * Sets the minimum height for an element. It prevents the height of the element to be
     * smaller than the specified value. The value of min-height overrides both max-height and
     * height.
     */
    @JsOverlay
    public final CssProps minHeight(@Nullable String minHeight) {
        setMinHeight(minHeight);
        return this;
    }

    @JsProperty
    public native @Nullable String getMinWidth();

    @JsProperty
    public native void setMinWidth(@Nullable String minWidth);

    /**
     * Sets the minimum width of an element. It limits the width property to be not smaller
     * than the value specified in min-width.
     */
    @JsOverlay
    public final CssProps minWidth(@Nullable String minWidth) {
        setMinWidth(minWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getOutline();

    @JsProperty
    public native void setOutline(@Nullable String outline);

    /**
     * The CSS outline property is a shorthand property for setting one or more of the
     * individual outline properties outline-style, outline-width and outline-color in a single
     * rule. In most cases the use of this shortcut is preferable and more convenient.
     * Outlines differ from borders in the following ways:
     * - Outlines do not take up space, they are drawn above the content.
     * - Outlines may be non-rectangular. They are rectangular in Gecko/Firefox.
     * Internet Explorer attempts to place the smallest contiguous outline around
     * all elements or shapes that are indicated to have an outline. Opera draws a
     * non-rectangular shape around a construct.
     */
    @JsOverlay
    public final CssProps outline(@Nullable String outline) {
        setOutline(outline);
        return this;
    }

    @JsProperty
    public native @Nullable String getOutlineColor();

    @JsProperty
    public native void setOutlineColor(@Nullable String outlineColor);

    /**
     * The outline-color property sets the color of the outline of an element. An outline is a
     * line that is drawn around elements, outside the border edge, to make the element stand out.
     */
    @JsOverlay
    public final CssProps outlineColor(@Nullable String outlineColor) {
        setOutlineColor(outlineColor);
        return this;
    }

    @JsProperty
    public native @Nullable String getOutlineOffset();

    @JsProperty
    public native void setOutlineOffset(@Nullable String outlineOffset);

    /**
     * The outline-offset property offsets the outline and draw it beyond the border edge.
     */
    @JsOverlay
    public final CssProps outlineOffset(@Nullable String outlineOffset) {
        setOutlineOffset(outlineOffset);
        return this;
    }

    @JsProperty
    public native @Nullable String getOverflow();

    @JsProperty
    public native void setOverflow(@Nullable String overflow);

    /**
     * The overflow property controls how extra content exceeding the bounding box of an element
     * is rendered. It can be used in conjunction with an element that has a fixed width and
     * height, to eliminate text-induced page distortion.
     */
    @JsOverlay
    public final CssProps overflow(@Nullable String overflow) {
        setOverflow(overflow);
        return this;
    }

    @JsProperty
    public native @Nullable String getOverflowStyle();

    @JsProperty
    public native void setOverflowStyle(@Nullable String overflowStyle);

    /**
     * Specifies the preferred scrolling methods for elements that overflow.
     */
    @JsOverlay
    public final CssProps overflowStyle(@Nullable String overflowStyle) {
        setOverflowStyle(overflowStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getOverflowX();

    @JsProperty
    public native void setOverflowX(@Nullable String overflowX);

    /**
     * The overflow-x property is a specific case of the generic overflow property. It controls
     * how extra content exceeding the x-axis of the bounding box of an element is rendered.
     */
    @JsOverlay
    public final CssProps overflowX(@Nullable String overflowX) {
        setOverflowX(overflowX);
        return this;
    }

    @JsProperty
    public native @Nullable String getPadding();

    @JsProperty
    public native void setPadding(@Nullable String padding);

    /**
     * The padding optional CSS property sets the required padding space on one to four sides of
     * an element. The padding area is the space between an element and its border. Negative
     * values are not allowed but decimal values are permitted. The element size is treated as
     * fixed, and the content of the element shifts toward the center as padding is increased.
     * The padding property is a shorthand to avoid setting each side separately (padding-top,
     * padding-right, padding-bottom, padding-left).
     */
    @JsOverlay
    public final CssProps padding(@Nullable String padding) {
        setPadding(padding);
        return this;
    }

    @JsProperty
    public native @Nullable String getPaddingBottom();

    @JsProperty
    public native void setPaddingBottom(@Nullable String paddingBottom);

    /**
     * The padding-bottom CSS property of an element sets the padding space required on the
     * bottom of an element. The padding area is the space between the content of the element and
     * its border. Contrary to margin-bottom values, negative values of padding-bottom are
     * invalid.
     */
    @JsOverlay
    public final CssProps paddingBottom(@Nullable String paddingBottom) {
        setPaddingBottom(paddingBottom);
        return this;
    }

    @JsProperty
    public native @Nullable String getPaddingLeft();

    @JsProperty
    public native void setPaddingLeft(@Nullable String paddingLeft);

    /**
     * The padding-left CSS property of an element sets the padding space required on the left
     * side of an element. The padding area is the space between the content of the element and
     * its border. Contrary to margin-left values, negative values of padding-left are invalid.
     */
    @JsOverlay
    public final CssProps paddingLeft(@Nullable String paddingLeft) {
        setPaddingLeft(paddingLeft);
        return this;
    }

    @JsProperty
    public native @Nullable String getPaddingRight();

    @JsProperty
    public native void setPaddingRight(@Nullable String paddingRight);

    /**
     * The padding-right CSS property of an element sets the padding space required on the right
     * side of an element. The padding area is the space between the content of the element and
     * its border. Contrary to margin-right values, negative values of padding-right are invalid.
     */
    @JsOverlay
    public final CssProps paddingRight(@Nullable String paddingRight) {
        setPaddingRight(paddingRight);
        return this;
    }

    @JsProperty
    public native @Nullable String getPaddingTop();

    @JsProperty
    public native void setPaddingTop(@Nullable String paddingTop);

    /**
     * The padding-top CSS property of an element sets the padding space required on the top of
     * an element. The padding area is the space between the content of the element and its
     * border. Contrary to margin-top values, negative values of padding-top are invalid.
     */
    @JsOverlay
    public final CssProps paddingTop(@Nullable String paddingTop) {
        setPaddingTop(paddingTop);
        return this;
    }

    @JsProperty
    public native @Nullable String getPageBreakAfter();

    @JsProperty
    public native void setPageBreakAfter(@Nullable String pageBreakAfter);

    /**
     * The page-break-after property is supported in all major browsers. With CSS3, page-break-*
     * properties are only aliases of the break-* properties. The CSS3 Fragmentation spec defines
     * breaks for all CSS box fragmentation.
     */
    @JsOverlay
    public final CssProps pageBreakAfter(@Nullable String pageBreakAfter) {
        setPageBreakAfter(pageBreakAfter);
        return this;
    }

    @JsProperty
    public native @Nullable String getPageBreakBefore();

    @JsProperty
    public native void setPageBreakBefore(@Nullable String pageBreakBefore);

    /**
     * The page-break-after property is supported in all major browsers. With CSS3, page-break-*
     * properties are only aliases of the break-* properties. The CSS3 Fragmentation spec defines
     * breaks for all CSS box fragmentation.
     */
    @JsOverlay
    public final CssProps pageBreakBefore(@Nullable String pageBreakBefore) {
        setPageBreakBefore(pageBreakBefore);
        return this;
    }

    @JsProperty
    public native @Nullable String getPageBreakInside();

    @JsProperty
    public native void setPageBreakInside(@Nullable String pageBreakInside);

    /**
     * Sets the page-breaking behavior inside an element. With CSS3, page-break-* properties
     * are only aliases of the break-* properties. The CSS3 Fragmentation spec defines breaks
     * for all CSS box fragmentation.
     */
    @JsOverlay
    public final CssProps pageBreakInside(@Nullable String pageBreakInside) {
        setPageBreakInside(pageBreakInside);
        return this;
    }

    @JsProperty
    public native @Nullable String getPause();

    @JsProperty
    public native void setPause(@Nullable String pause);

    /**
     * The pause property determines how long a speech media agent should pause before and after
     * presenting an element. It is a shorthand for the pause-before and pause-after properties.
     */
    @JsOverlay
    public final CssProps pause(@Nullable String pause) {
        setPause(pause);
        return this;
    }

    @JsProperty
    public native @Nullable String getPauseAfter();

    @JsProperty
    public native void setPauseAfter(@Nullable String pauseAfter);

    /**
     * The pause-after property determines how long a speech media agent should pause after
     * presenting an element. It may be replaced by the shorthand property pause, which sets
     * pause time before and after.
     */
    @JsOverlay
    public final CssProps pauseAfter(@Nullable String pauseAfter) {
        setPauseAfter(pauseAfter);
        return this;
    }

    @JsProperty
    public native @Nullable String getPauseBefore();

    @JsProperty
    public native void setPauseBefore(@Nullable String pauseBefore);

    /**
     * The pause-before property determines how long a speech media agent should pause before
     * presenting an element. It may be replaced by the shorthand property pause, which sets
     * pause time before and after.
     */
    @JsOverlay
    public final CssProps pauseBefore(@Nullable String pauseBefore) {
        setPauseBefore(pauseBefore);
        return this;
    }

    @JsProperty
    public native @Nullable String getPerspective();

    @JsProperty
    public native void setPerspective(@Nullable String perspective);

    /**
     * The perspective property defines how far an element is placed from the view on the
     * z-axis, from the screen to the viewer.
     *
     * Perspective defines how an object is viewed. In graphic arts, perspective is the
     * representation on a flat surface of what the viewer's eye would see in a 3D space.
     * (See Wikipedia for more information about graphical perspective and for related
     * illustrations.)
     *
     * The illusion of perspective on a flat surface, such as a computer screen, is created
     * by projecting points on the flat surface as they would appear if the flat surface
     * were a window through which the viewer was looking at the object. In discussion of
     * virtual environments, this flat surface is called a projection plane.
     */
    @JsOverlay
    public final CssProps perspective(@Nullable String perspective) {
        setPerspective(perspective);
        return this;
    }

    @JsProperty
    public native @Nullable String getPerspectiveOrigin();

    @JsProperty
    public native void setPerspectiveOrigin(@Nullable String perspectiveOrigin);

    /**
     * The perspective-origin property establishes the origin for the perspective property. It
     * effectively sets the X and Y position at which the viewer appears to be looking at the
     * children of the element.
     *
     * When used with perspective, perspective-origin changes the appearance of an object, as
     * if a viewer were looking at it from a different origin. An object appears differently
     * if a viewer is looking directly at it versus looking at it from below, above, or from
     * the side. Thus, the perspective-origin is like a vanishing point.
     *
     * The default value of perspective-origin is 50% 50%. This displays an object as if the
     * viewer's eye were positioned directly at the center of the screen, both top-to-bottom
     * and left-to-right. A value of 0% 0% changes the object as if the viewer was looking
     * toward the top left angle. A value of 100% 100% changes the appearance as if viewed
     * toward the bottom right angle.
     */
    @JsOverlay
    public final CssProps perspectiveOrigin(@Nullable String perspectiveOrigin) {
        setPerspectiveOrigin(perspectiveOrigin);
        return this;
    }

    @JsProperty
    public native @Nullable String getPointerEvents();

    @JsProperty
    public native void setPointerEvents(@Nullable String pointerEvents);

    /**
     * The pointer-events property allows you to control whether an element can be the target
     * for the pointing device (e.g, mouse, pen) events.
     */
    @JsOverlay
    public final CssProps pointerEvents(@Nullable String pointerEvents) {
        setPointerEvents(pointerEvents);
        return this;
    }

    @JsProperty
    public native @Nullable String getPosition();

    @JsProperty
    public native void setPosition(@Nullable String position);

    /**
     * The position property controls the type of positioning used by an element within its
     * parent elements. The effect of the position property depends on a lot of factors, for
     * example the position property of parent elements.
     */
    @JsOverlay
    public final CssProps position(@Nullable String position) {
        setPosition(position);
        return this;
    }

    @JsProperty
    public native @Nullable String getQuotes();

    @JsProperty
    public native void setQuotes(@Nullable String quotes);

    /**
     * Sets the type of quotation marks for embedded quotations.
     */
    @JsOverlay
    public final CssProps quotes(@Nullable String quotes) {
        setQuotes(quotes);
        return this;
    }

    @JsProperty
    public native @Nullable String getRegionFragment();

    @JsProperty
    public native void setRegionFragment(@Nullable String regionFragment);

    /**
     * Controls whether the last region in a chain displays additional 'overset' content
     * according its default overflow property, or if it displays a fragment of content as
     * if it were flowing into a subsequent region.
     */
    @JsOverlay
    public final CssProps regionFragment(@Nullable String regionFragment) {
        setRegionFragment(regionFragment);
        return this;
    }

    @JsProperty
    public native @Nullable String getRestAfter();

    @JsProperty
    public native void setRestAfter(@Nullable String restAfter);

    /**
     * The rest-after property determines how long a speech media agent should pause after
     * presenting an element's main content, before presenting that element's exit cue sound.
     * It may be replaced by the shorthand property rest, which sets rest time before and after.
     */
    @JsOverlay
    public final CssProps restAfter(@Nullable String restAfter) {
        setRestAfter(restAfter);
        return this;
    }

    @JsProperty
    public native @Nullable String getRestBefore();

    @JsProperty
    public native void setRestBefore(@Nullable String restBefore);

    /**
     * The rest-before property determines how long a speech media agent should pause after
     * presenting an intro cue sound for an element, before presenting that element's main
     * content. It may be replaced by the shorthand property rest, which sets rest time before
     * and after.
     */
    @JsOverlay
    public final CssProps restBefore(@Nullable String restBefore) {
        setRestBefore(restBefore);
        return this;
    }

    @JsProperty
    public native @Nullable String getRight();

    @JsProperty
    public native void setRight(@Nullable String right);

    /**
     * Specifies the position an element in relation to the right side of the containing element.
     */
    @JsOverlay
    public final CssProps right(@Nullable String right) {
        setRight(right);
        return this;
    }

    @JsProperty
    public native @Nullable String getRubyAlign();

    @JsProperty
    public native void setRubyAlign(@Nullable String rubyAlign);

    @JsOverlay
    public final CssProps rubyAlign(@Nullable String rubyAlign) {
        setRubyAlign(rubyAlign);
        return this;
    }

    @JsProperty
    public native @Nullable String getRubyPosition();

    @JsProperty
    public native void setRubyPosition(@Nullable String rubyPosition);

    @JsOverlay
    public final CssProps rubyPosition(@Nullable String rubyPosition) {
        setRubyPosition(rubyPosition);
        return this;
    }

    @JsProperty
    public native @Nullable String getShapeImageThreshold();

    @JsProperty
    public native void setShapeImageThreshold(@Nullable String shapeImageThreshold);

    /**
     * Defines the alpha channel threshold used to extract a shape from an image. Can be thought
     * of as a "minimum opacity" threshold; that is, a value of 0.5 means that the shape will
     * enclose all the pixels that are more than 50% opaque.
     */
    @JsOverlay
    public final CssProps shapeImageThreshold(@Nullable String shapeImageThreshold) {
        setShapeImageThreshold(shapeImageThreshold);
        return this;
    }

    @JsProperty
    public native @Nullable String getShapeInside();

    @JsProperty
    public native void setShapeInside(@Nullable String shapeInside);

    /**
     * A future level of CSS Shapes will define a shape-inside property, which will define a
     * shape to wrap content within the element. See Editor's Draft
     */
    @JsOverlay
    public final CssProps shapeInside(@Nullable String shapeInside) {
        setShapeInside(shapeInside);
        return this;
    }

    @JsProperty
    public native @Nullable String getShapeMargin();

    @JsProperty
    public native void setShapeMargin(@Nullable String shapeMargin);

    /**
     * Adds a margin to a shape-outside. In effect, defines a new shape that is the smallest
     * contour around all the points that are the shape-margin distance outward perpendicular
     * to each point on the underlying shape. For points where a perpendicular direction is
     * not defined (e.g., a triangle corner), takes all points on a circle centered at the
     * point and with a radius of the shape-margin distance. This property accepts only
     * non-negative values.
     */
    @JsOverlay
    public final CssProps shapeMargin(@Nullable String shapeMargin) {
        setShapeMargin(shapeMargin);
        return this;
    }

    @JsProperty
    public native @Nullable String getShapeOutside();

    @JsProperty
    public native void setShapeOutside(@Nullable String shapeOutside);

    /**
     * Declares a shape around which text should be wrapped, with possible modifications
     * from the shape-margin property. The shape defined by shape-outside and shape-margin
     * changes the geometry of a float element's float area.
     */
    @JsOverlay
    public final CssProps shapeOutside(@Nullable String shapeOutside) {
        setShapeOutside(shapeOutside);
        return this;
    }

    @JsProperty
    public native @Nullable String getSpeak();

    @JsProperty
    public native void setSpeak(@Nullable String speak);

    /**
     * The speak property determines whether or not a speech synthesizer will read aloud
     * the contents of an element.
     */
    @JsOverlay
    public final CssProps speak(@Nullable String speak) {
        setSpeak(speak);
        return this;
    }

    @JsProperty
    public native @Nullable String getSpeakAs();

    @JsProperty
    public native void setSpeakAs(@Nullable String speakAs);

    /**
     * The speak-as property determines how the speech synthesizer interprets the content:
     * words as whole words or as a sequence of letters, numbers as a numerical value or a
     * sequence of digits, punctuation as pauses in speech or named punctuation characters.
     */
    @JsOverlay
    public final CssProps speakAs(@Nullable String speakAs) {
        setSpeakAs(speakAs);
        return this;
    }

    @JsProperty
    public native @Nullable String getTabSize();

    @JsProperty
    public native void setTabSize(@Nullable String tabSize);

    /**
     * The tab-size CSS property is used to customise the width of a tab (U+0009) character.
     */
    @JsOverlay
    public final CssProps tabSize(@Nullable String tabSize) {
        setTabSize(tabSize);
        return this;
    }

    @JsProperty
    public native @Nullable String getTableLayout();

    @JsProperty
    public native void setTableLayout(@Nullable String tableLayout);

    /**
     * The 'table-layout' property controls the algorithm used to lay out the table cells,
     * rows, and columns.
     */
    @JsOverlay
    public final CssProps tableLayout(@Nullable String tableLayout) {
        setTableLayout(tableLayout);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextAlign();

    @JsProperty
    public native void setTextAlign(@Nullable String textAlign);

    /**
     * The text-align CSS property describes how inline content like text is aligned in
     * its parent block element. text-align does not control the alignment of block
     * elements itself, only their inline content.
     */
    @JsOverlay
    public final CssProps textAlign(@Nullable String textAlign) {
        setTextAlign(textAlign);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextAlignLast();

    @JsProperty
    public native void setTextAlignLast(@Nullable String textAlignLast);

    /**
     * The text-align-last CSS property describes how the last line of a block element or
     * a line before line break is aligned in its parent block element.
     */
    @JsOverlay
    public final CssProps textAlignLast(@Nullable String textAlignLast) {
        setTextAlignLast(textAlignLast);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextDecoration();

    @JsProperty
    public native void setTextDecoration(@Nullable String textDecoration);

    /**
     * The text-decoration CSS property is used to set the text formatting to underline,
     * overline, line-through or blink.
     *
     * underline and overline decorations are positioned under the text, line-through over it.
     */
    @JsOverlay
    public final CssProps textDecoration(@Nullable String textDecoration) {
        setTextDecoration(textDecoration);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextDecorationColor();

    @JsProperty
    public native void setTextDecorationColor(@Nullable String textDecorationColor);

    /**
     * Sets the color of any text decoration, such as underlines, overlines, and strike throughs.
     */
    @JsOverlay
    public final CssProps textDecorationColor(@Nullable String textDecorationColor) {
        setTextDecorationColor(textDecorationColor);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextDecorationLine();

    @JsProperty
    public native void setTextDecorationLine(@Nullable String textDecorationLine);

    /**
     * Sets what kind of line decorations are added to an element, such as underlines, overlines, etc.
     */
    @JsOverlay
    public final CssProps textDecorationLine(@Nullable String textDecorationLine) {
        setTextDecorationLine(textDecorationLine);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextDecorationLineThrough();

    @JsProperty
    public native void setTextDecorationLineThrough(@Nullable String textDecorationLineThrough);

    @JsOverlay
    public final CssProps textDecorationLineThrough(@Nullable String textDecorationLineThrough) {
        setTextDecorationLineThrough(textDecorationLineThrough);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextDecorationNone();

    @JsProperty
    public native void setTextDecorationNone(@Nullable String textDecorationNone);

    @JsOverlay
    public final CssProps textDecorationNone(@Nullable String textDecorationNone) {
        setTextDecorationNone(textDecorationNone);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextDecorationOverline();

    @JsProperty
    public native void setTextDecorationOverline(@Nullable String textDecorationOverline);

    @JsOverlay
    public final CssProps textDecorationOverline(@Nullable String textDecorationOverline) {
        setTextDecorationOverline(textDecorationOverline);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextDecorationSkip();

    @JsProperty
    public native void setTextDecorationSkip(@Nullable String textDecorationSkip);

    /**
     * Specifies what parts of an element's content are skipped over when applying any text decoration.
     */
    @JsOverlay
    public final CssProps textDecorationSkip(@Nullable String textDecorationSkip) {
        setTextDecorationSkip(textDecorationSkip);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextDecorationStyle();

    @JsProperty
    public native void setTextDecorationStyle(@Nullable String textDecorationStyle);

    /**
     * This property specifies the style of the text decoration line drawn on the specified element.
     * The intended meaning for the values are the same as those of the border-style-properties.
     */
    @JsOverlay
    public final CssProps textDecorationStyle(@Nullable String textDecorationStyle) {
        setTextDecorationStyle(textDecorationStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextDecorationUnderline();

    @JsProperty
    public native void setTextDecorationUnderline(@Nullable String textDecorationUnderline);

    @JsOverlay
    public final CssProps textDecorationUnderline(@Nullable String textDecorationUnderline) {
        setTextDecorationUnderline(textDecorationUnderline);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextEmphasis();

    @JsProperty
    public native void setTextEmphasis(@Nullable String textEmphasis);

    /**
     * The text-emphasis property will apply special emphasis marks to the elements text. Slightly
     * similar to the text-decoration property only that this property can have affect on the
     * line-height. It also is noted that this is shorthand for text-emphasis-style and for
     * text-emphasis-color.
     */
    @JsOverlay
    public final CssProps textEmphasis(@Nullable String textEmphasis) {
        setTextEmphasis(textEmphasis);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextEmphasisColor();

    @JsProperty
    public native void setTextEmphasisColor(@Nullable String textEmphasisColor);

    /**
     * The text-emphasis-color property specifies the foreground color of the emphasis marks.
     */
    @JsOverlay
    public final CssProps textEmphasisColor(@Nullable String textEmphasisColor) {
        setTextEmphasisColor(textEmphasisColor);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextEmphasisStyle();

    @JsProperty
    public native void setTextEmphasisStyle(@Nullable String textEmphasisStyle);

    /**
     * The text-emphasis-style property applies special emphasis marks to an element's text.
     */
    @JsOverlay
    public final CssProps textEmphasisStyle(@Nullable String textEmphasisStyle) {
        setTextEmphasisStyle(textEmphasisStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextHeight();

    @JsProperty
    public native void setTextHeight(@Nullable String textHeight);

    /**
     * This property helps determine an inline box's block-progression dimension, derived from
     * the text-height and font-size properties for non-replaced elements, the height or the
     * width for replaced elements, and the stacked block-progression dimension for
     * inline-block elements. The block-progression dimension determines the position of the
     * padding, border and margin for the element.
     */
    @JsOverlay
    public final CssProps textHeight(@Nullable String textHeight) {
        setTextHeight(textHeight);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextIndent();

    @JsProperty
    public native void setTextIndent(@Nullable String textIndent);

    /**
     * Specifies the amount of space horizontally that should be left on the first line of the
     * text of an element. This horizontal spacing is at the beginning of the first line and
     * is in respect to the left edge of the containing block box.
     */
    @JsOverlay
    public final CssProps textIndent(@Nullable String textIndent) {
        setTextIndent(textIndent);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextJustifyTrim();

    @JsProperty
    public native void setTextJustifyTrim(@Nullable String textJustifyTrim);

    @JsOverlay
    public final CssProps textJustifyTrim(@Nullable String textJustifyTrim) {
        setTextJustifyTrim(textJustifyTrim);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextKashidaSpace();

    @JsProperty
    public native void setTextKashidaSpace(@Nullable String textKashidaSpace);

    @JsOverlay
    public final CssProps textKashidaSpace(@Nullable String textKashidaSpace) {
        setTextKashidaSpace(textKashidaSpace);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextLineThroughWidth();

    @JsProperty
    public native void setTextLineThroughWidth(@Nullable String textLineThroughWidth);

    /**
     * Specifies the line width for the line-through text decoration.
     */
    @JsOverlay
    public final CssProps textLineThroughWidth(@Nullable String textLineThroughWidth) {
        setTextLineThroughWidth(textLineThroughWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextOverflow();

    @JsProperty
    public native void setTextOverflow(@Nullable String textOverflow);

    /**
     * The text-overflow shorthand CSS property determines how overflowed content that is not
     * displayed is signaled to the users. It can be clipped, display an ellipsis (HORIZONTAL ELLIPSIS)
     * or a Web author-defined string. It covers the two long-hand
     * properties text-overflow-mode and text-overflow-ellipsis
     */
    @JsOverlay
    public final CssProps textOverflow(@Nullable String textOverflow) {
        setTextOverflow(textOverflow);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextOverline();

    @JsProperty
    public native void setTextOverline(@Nullable String textOverline);

    /**
     * The text-overline property is the shorthand for the text-overline-style,
     * text-overline-width, text-overline-color, and text-overline-mode properties.
     */
    @JsOverlay
    public final CssProps textOverline(@Nullable String textOverline) {
        setTextOverline(textOverline);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextOverlineColor();

    @JsProperty
    public native void setTextOverlineColor(@Nullable String textOverlineColor);

    /**
     * Specifies the line color for the overline text decoration.
     */
    @JsOverlay
    public final CssProps textOverlineColor(@Nullable String textOverlineColor) {
        setTextOverlineColor(textOverlineColor);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextOverlineMode();

    @JsProperty
    public native void setTextOverlineMode(@Nullable String textOverlineMode);

    /**
     * Sets the mode for the overline text decoration, determining whether the text decoration
     * affects the space characters or not.
     */
    @JsOverlay
    public final CssProps textOverlineMode(@Nullable String textOverlineMode) {
        setTextOverlineMode(textOverlineMode);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextOverlineStyle();

    @JsProperty
    public native void setTextOverlineStyle(@Nullable String textOverlineStyle);

    /**
     * Specifies the line style for overline text decoration.
     */
    @JsOverlay
    public final CssProps textOverlineStyle(@Nullable String textOverlineStyle) {
        setTextOverlineStyle(textOverlineStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextOverlineWidth();

    @JsProperty
    public native void setTextOverlineWidth(@Nullable String textOverlineWidth);

    /**
     * Specifies the line width for the overline text decoration.
     */
    @JsOverlay
    public final CssProps textOverlineWidth(@Nullable String textOverlineWidth) {
        setTextOverlineWidth(textOverlineWidth);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextRendering();

    @JsProperty
    public native void setTextRendering(@Nullable String textRendering);

    /**
     * The text-rendering CSS property provides information to the browser about how to optimize
     * when rendering text. Options are: legibility, speed or geometric precision.
     */
    @JsOverlay
    public final CssProps textRendering(@Nullable String textRendering) {
        setTextRendering(textRendering);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextShadow();

    @JsProperty
    public native void setTextShadow(@Nullable String textShadow);

    /**
     * The CSS text-shadow property applies one or more drop shadows to the text and
     * text-decorations of an element. Each shadow is specified as an offset from the text,
     * along with optional color and blur radius values.
     */
    @JsOverlay
    public final CssProps textShadow(@Nullable String textShadow) {
        setTextShadow(textShadow);
        return this;
    }

    @JsProperty
    public native @Nullable String getTextTransform();

    @JsProperty
    public native void setTextTransform(@Nullable String textTransform);

    /**
     * This property transforms text for styling purposes. (It has no effect on the underlying
     * content.)
     */
    @JsOverlay
    public final CssProps textTransform(@Nullable String textTransform) {
        setTextTransform(textTransform);
        return this;
    }

    @JsProperty
    public native @Nullable String getTop();

    @JsProperty
    public native void setTop(@Nullable String top);

    /**
     * This property specifies how far an absolutely positioned box's top margin edge is offset
     * below the top edge of the box's containing block. For relatively positioned boxes, the
     * offset is with respect to the top edges of the box itself (i.e., the box is given a
     * position in the normal flow, then offset from that position according to these properties).
     */
    @JsOverlay
    public final CssProps top(@Nullable String top) {
        setTop(top);
        return this;
    }

    @JsProperty
    public native @Nullable String getTouchAction();

    @JsProperty
    public native void setTouchAction(@Nullable String touchAction);

    /**
     * Determines whether touch input may trigger default behavior supplied by the user agent,
     * such as panning or zooming.
     */
    @JsOverlay
    public final CssProps touchAction(@Nullable String touchAction) {
        setTouchAction(touchAction);
        return this;
    }

    @JsProperty
    public native @Nullable String getTransform();

    @JsProperty
    public native void setTransform(@Nullable String transform);

    /**
     * CSS transforms allow elements styled with CSS to be transformed in two-dimensional or
     * three-dimensional space. Using this property, elements can be translated, rotated, scaled,
     * and skewed. The value list may consist of 2D and/or 3D transform values.
     */
    @JsOverlay
    public final CssProps transform(@Nullable String transform) {
        setTransform(transform);
        return this;
    }

    @JsProperty
    public native @Nullable String getTransformOrigin();

    @JsProperty
    public native void setTransformOrigin(@Nullable String transformOrigin);

    /**
     * This property defines the origin of the transformation axes relative to the element to
     * which the transformation is applied.
     */
    @JsOverlay
    public final CssProps transformOrigin(@Nullable String transformOrigin) {
        setTransformOrigin(transformOrigin);
        return this;
    }

    @JsProperty
    public native @Nullable String getTransformOriginZ();

    @JsProperty
    public native void setTransformOriginZ(@Nullable String transformOriginZ);

    /**
     * This property allows you to define the relative position of the origin of the transformation
     * grid along the z-axis.
     */
    @JsOverlay
    public final CssProps transformOriginZ(@Nullable String transformOriginZ) {
        setTransformOriginZ(transformOriginZ);
        return this;
    }

    @JsProperty
    public native @Nullable String getTransformStyle();

    @JsProperty
    public native void setTransformStyle(@Nullable String transformStyle);

    /**
     * This property specifies how nested elements are rendered in 3D space relative to their parent.
     */
    @JsOverlay
    public final CssProps transformStyle(@Nullable String transformStyle) {
        setTransformStyle(transformStyle);
        return this;
    }

    @JsProperty
    public native @Nullable String getTransition();

    @JsProperty
    public native void setTransition(@Nullable String transition);

    /**
     * The transition CSS property is a shorthand property for transition-property,
     * transition-duration, transition-timing-function, and transition-delay. It allows to define
     * the transition between two states of an element.
     */
    @JsOverlay
    public final CssProps transition(@Nullable String transition) {
        setTransition(transition);
        return this;
    }

    @JsProperty
    public native @Nullable String getTransitionDelay();

    @JsProperty
    public native void setTransitionDelay(@Nullable String transitionDelay);

    /**
     * Defines when the transition will start. A value of "0s" means the transition will execute
     * as soon as the property is changed. Otherwise, the value specifies an offset from the
     * moment the property is changed, and the transition will delay execution by that offset.
     */
    @JsOverlay
    public final CssProps transitionDelay(@Nullable String transitionDelay) {
        setTransitionDelay(transitionDelay);
        return this;
    }

    @JsProperty
    public native @Nullable String getTransitionDuration();

    @JsProperty
    public native void setTransitionDuration(@Nullable String transitionDuration);

    /**
     * The 'transition-duration' property specifies the length of time a transition animation
     * takes to complete.
     */
    @JsOverlay
    public final CssProps transitionDuration(@Nullable String transitionDuration) {
        setTransitionDuration(transitionDuration);
        return this;
    }

    @JsProperty
    public native @Nullable String getTransitionProperty();

    @JsProperty
    public native void setTransitionProperty(@Nullable String transitionProperty);

    /**
     * The 'transition-property' property specifies the name of the CSS property to which the
     * transition is applied.
     */
    @JsOverlay
    public final CssProps transitionProperty(@Nullable String transitionProperty) {
        setTransitionProperty(transitionProperty);
        return this;
    }

    @JsProperty
    public native @Nullable String getTransitionTimingFunction();

    @JsProperty
    public native void setTransitionTimingFunction(@Nullable String transitionTimingFunction);

    /**
     * Sets the pace of action within a transition
     */
    @JsOverlay
    public final CssProps transitionTimingFunction(@Nullable String transitionTimingFunction) {
        setTransitionTimingFunction(transitionTimingFunction);
        return this;
    }

    /**
     * The unicode-bidi CSS property specifies the level of embedding with respect to the
     * bidirectional algorithm.
     */
    @JsProperty
    public native @Nullable String getUnicodeBidi();

    @JsProperty
    public native void setUnicodeBidi(@Nullable String unicodeBidi);

    @JsOverlay
    public final CssProps unicodeBidi(@Nullable String unicodeBidi) {
        setUnicodeBidi(unicodeBidi);
        return this;
    }

    @JsProperty
    public native @Nullable String getUnicodeRange();

    @JsProperty
    public native void setUnicodeRange(@Nullable String unicodeRange);

    /**
     * unicode-range allows you to set a specific range of characters to be downloaded from a
     * font (embedded using @font-face) and made available for use on the current page.
     */
    @JsOverlay
    public final CssProps unicodeRange(@Nullable String unicodeRange) {
        setUnicodeRange(unicodeRange);
        return this;
    }

    @JsProperty
    public native @Nullable String getUserFocus();

    @JsProperty
    public native void setUserFocus(@Nullable String userFocus);

    /**
     * This is for all the high level UX stuff.
     */
    @JsOverlay
    public final CssProps userFocus(@Nullable String userFocus) {
        setUserFocus(userFocus);
        return this;
    }

    @JsProperty
    public native @Nullable String getUserInput();

    @JsProperty
    public native void setUserInput(@Nullable String userInput);

    /**
     * For inputing user content
     */
    @JsOverlay
    public final CssProps userInput(@Nullable String userInput) {
        setUserInput(userInput);
        return this;
    }

    @JsProperty
    public native @Nullable String getVerticalAlign();

    @JsProperty
    public native void setVerticalAlign(@Nullable String verticalAlign);

    /**
     * The vertical-align property controls how inline elements or text are vertically aligned
     * compared to the baseline. If this property is used on table-cells it controls the vertical
     * alignment of content of the table cell.
     */
    @JsOverlay
    public final CssProps verticalAlign(@Nullable String verticalAlign) {
        setVerticalAlign(verticalAlign);
        return this;
    }

    @JsProperty
    public native @Nullable String getVisibility();

    @JsProperty
    public native void setVisibility(@Nullable String visibility);

    /**
     * The visibility property specifies whether the boxes generated by an element are rendered.
     */
    @JsOverlay
    public final CssProps visibility(@Visibility final String visibility) {
        setVisibility(visibility);
        return this;
    }

    @JsProperty
    public native @Nullable String getVoiceBalance();

    @JsProperty
    public native void setVoiceBalance(@Nullable String voiceBalance);

    /**
     * The voice-balance property sets the apparent position (in stereo sound) of the synthesized
     * voice for spoken media.
     */
    @JsOverlay
    public final CssProps voiceBalance(@Nullable String voiceBalance) {
        setVoiceBalance(voiceBalance);
        return this;
    }

    @JsProperty
    public native @Nullable String getVoiceDuration();

    @JsProperty
    public native void setVoiceDuration(@Nullable String voiceDuration);

    /**
     * The voice-duration property allows the author to explicitly set the amount of time it should
     * take a speech synthesizer to read an element's content, for example to allow the speech to
     * be synchronized with other media. With a value of auto (the default) the length of time it
     * takes to read the content is determined by the content itself and the voice-rate property.
     */
    @JsOverlay
    public final CssProps voiceDuration(@Nullable String voiceDuration) {
        setVoiceDuration(voiceDuration);
        return this;
    }

    @JsProperty
    public native @Nullable String getVoiceFamily();

    @JsProperty
    public native void setVoiceFamily(@Nullable String voiceFamily);

    /**
     * The voice-family property sets the speaker's voice used by a speech media agent to read an
     * element. The speaker may be specified as a named character (to match a voice option in the
     * speech reading software) or as a generic description of the age and gender of the voice.
     * Similar to the font-family property for visual media, a comma-separated list of fallback
     * options may be given in case the speech reader does not recognize the character name or
     * cannot synthesize the requested combination of generic properties.
     */
    @JsOverlay
    public final CssProps voiceFamily(@Nullable String voiceFamily) {
        setVoiceFamily(voiceFamily);
        return this;
    }

    @JsProperty
    public native @Nullable String getVoicePitch();

    @JsProperty
    public native void setVoicePitch(@Nullable String voicePitch);

    /**
     * The voice-pitch property sets pitch or tone (high or low) for the synthesized speech when
     * reading an element; the pitch may be specified absolutely or relative to the normal pitch
     * for the voice-family used to read the text.
     */
    @JsOverlay
    public final CssProps voicePitch(@Nullable String voicePitch) {
        setVoicePitch(voicePitch);
        return this;
    }

    @JsProperty
    public native @Nullable String getVoiceRange();

    @JsProperty
    public native void setVoiceRange(@Nullable String voiceRange);

    /**
     * The voice-range property determines how much variation in pitch or tone will be created by
     * the speech synthesize when reading an element. Emphasized text, grammatical structures and
     * punctuation may all be rendered as changes in pitch, this property determines how strong
     * or obvious those changes are; large ranges are associated with enthusiastic or emotional
     * speech, while small ranges are associated with flat or mechanical speech.
     */
    @JsOverlay
    public final CssProps voiceRange(@Nullable String voiceRange) {
        setVoiceRange(voiceRange);
        return this;
    }

    @JsProperty
    public native @Nullable String getVoiceRate();

    @JsProperty
    public native void setVoiceRate(@Nullable String voiceRate);

    /**
     * The voice-rate property sets the speed at which the voice synthesized by a speech media
     * agent will read content.
     */
    @JsOverlay
    public final CssProps voiceRate(@Nullable String voiceRate) {
        setVoiceRate(voiceRate);
        return this;
    }

    @JsProperty
    public native @Nullable String getVoiceStress();

    @JsProperty
    public native void setVoiceStress(@Nullable String voiceStress);

    /**
     * The voice-stress property sets the level of vocal emphasis to be used for synthesized
     * speech reading the element.
     */
    @JsOverlay
    public final CssProps voiceStress(@Nullable String voiceStress) {
        setVoiceStress(voiceStress);
        return this;
    }

    @JsProperty
    public native @Nullable String getVoiceVolume();

    @JsProperty
    public native void setVoiceVolume(@Nullable String voiceVolume);

    /**
     * The voice-volume property sets the volume for spoken content in speech media. It replaces
     * the deprecated volume property.
     */
    @JsOverlay
    public final CssProps voiceVolume(@Nullable String voiceVolume) {
        setVoiceVolume(voiceVolume);
        return this;
    }

    @JsProperty
    public native @Nullable String getWhiteSpace();

    @JsProperty
    public native void setWhiteSpace(@Nullable String whiteSpace);

    /**
     * The white-space property controls whether and how white space inside the element is
     * collapsed, and whether lines may wrap at unforced "soft wrap" opportunities.
     */
    @JsOverlay
    public final CssProps whiteSpace(@Nullable String whiteSpace) {
        setWhiteSpace(whiteSpace);
        return this;
    }

    @JsProperty
    public native @Nullable String getWidth();

    @JsProperty
    public native void setWidth(@Nullable String width);

    /**
     * Specifies the width of the content area of an element. The content area of the element
     * width does not include the padding, border, and margin of the element.
     */
    @JsOverlay
    public final CssProps width(@Nullable String width) {
        setWidth(width);
        return this;
    }

    @JsProperty
    public native @Nullable String getWordBreak();

    @JsProperty
    public native void setWordBreak(@Nullable String wordBreak);

    /**
     * The word-break property is often used when there is long generated content that is
     * strung together without and spaces or hyphens to beak apart. A common case of this is when
     * there is a long URL that does not have any hyphens. This case could potentially cause the
     * breaking of the layout as it could extend past the parent element.
     */
    @JsOverlay
    public final CssProps wordBreak(@Nullable String wordBreak) {
        setWordBreak(wordBreak);
        return this;
    }

    @JsProperty
    public native @Nullable String getWordSpacing();

    @JsProperty
    public native void setWordSpacing(@Nullable String wordSpacing);

    /**
     * The word-spacing CSS property specifies the spacing behavior between "words".
     */
    @JsOverlay
    public final CssProps wordSpacing(@Nullable String wordSpacing) {
        setWordSpacing(wordSpacing);
        return this;
    }

    @JsProperty
    public native @Nullable String getWordWrap();

    @JsProperty
    public native void setWordWrap(@Nullable String wordWrap);

    /**
     * An alias of css/properties/overflow-wrap, word-wrap defines whether to break words when
     * the content exceeds the boundaries of its container.
     */
    @JsOverlay
    public final CssProps wordWrap(@Nullable String wordWrap) {
        setWordWrap(wordWrap);
        return this;
    }

    @JsProperty
    public native @Nullable String getWrapFlow();

    @JsProperty
    public native void setWrapFlow(@Nullable String wrapFlow);

    /**
     * Specifies how exclusions affect inline content within block-level elements. Elements lay
     * out their inline content in their content area but wrap around exclusion areas.
     */
    @JsOverlay
    public final CssProps wrapFlow(@Nullable String wrapFlow) {
        setWrapFlow(wrapFlow);
        return this;
    }

    @JsProperty
    public native @Nullable String getWrapMargin();

    @JsProperty
    public native void setWrapMargin(@Nullable String wrapMargin);

    /**
     * Set the value that is used to offset the inner wrap shape from other shapes. Inline
     * content that intersects a shape with this property will be pushed by this shape's margin.
     */
    @JsOverlay
    public final CssProps wrapMargin(@Nullable String wrapMargin) {
        setWrapMargin(wrapMargin);
        return this;
    }

    @JsProperty
    public native @Nullable String getWritingMode();

    @JsProperty
    public native void setWritingMode(@Nullable String writingMode);

    /**
     * writing-mode specifies if lines of text are laid out horizontally or vertically, and the
     * direction which lines of text and blocks progress.
     */
    @JsOverlay
    public final CssProps writingMode(@Nullable String writingMode) {
        setWritingMode(writingMode);
        return this;
    }

    // WebKit specific styles

    @JsProperty(name = "WebkitMask")
    public native @Nullable String getWebKitMask();

    @JsProperty(name = "WebkitMask")
    public native void setWebKitMask(@Nullable String mask);

    @JsOverlay
    public final CssProps webkitMask(@Nullable String mask) {
        setWebKitMask(mask);
        return this;
    }

    @JsProperty(name = "WebkitMaskSize")
    public native @Nullable String getWebKitMaskSize();

    @JsProperty(name = "WebkitMaskSize")
    public native void setWebKitMaskSize(@Nullable String mask);

    @JsOverlay
    public final CssProps webkitMaskSize(@Nullable String mask) {
        setWebKitMaskSize(mask);
        return this;
    }
}
