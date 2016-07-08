package io.github.psychesworld.mappergenerator.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import io.github.psychesworld.mappergenerator.annotations.MapTo;

class TypeUtils {
    private final Types types;

    TypeUtils(Types types) {
        this.types = types;
    }

    TypeElement asTypeElement(TypeMirror typeMirror) {
        return (TypeElement) types.asElement(typeMirror);
    }

    TypeElement findMapperTarget(Element element){
        MapTo annotation = element.getAnnotation(MapTo.class);
        TypeElement mapperTarget = null;
        try {
            annotation.value();
        } catch (MirroredTypeException mte) {
            mapperTarget = asTypeElement(mte.getTypeMirror());
        }
        return mapperTarget;
    }
}
