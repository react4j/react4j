package com.example.constant;

import org.jspecify.annotations.NullMarked;
import react4j.css.Css;

@NullMarked
@Css(Constants.CSS)
abstract class Button {
    abstract String style();
}

final class Constants {
    static final String CSS = "color: red;";
}
