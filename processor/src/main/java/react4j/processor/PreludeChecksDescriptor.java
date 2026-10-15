package react4j.processor;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

record PreludeChecksDescriptor(Element element, TypeMirror type, ObserveMode observeMode) {}
