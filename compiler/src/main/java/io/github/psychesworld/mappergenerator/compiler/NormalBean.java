package io.github.psychesworld.mappergenerator.compiler;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

class NormalBean implements Bean {
    private final TypeUtils typeUtils;
    private final List<Property> properties;
    private final ClassName className;

    NormalBean(TypeUtils typeUtils, TypeElement typeElement) {
        this.typeUtils = typeUtils;
        properties = new ArrayList<>();
        className = ClassName.get(typeElement);
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) enclosedElement;
                properties.add(Property.get(typeUtils, field, typeElement));
            }
        }
    }

    @Override
    public ClassName className() {
        return className;
    }

    @Override
    public String simpleClassName() {
        return className.simpleName();
    }

    @Override
    public List<Property> properties() {
        return properties;
    }

    @Override
    public boolean hasProperty(String name) {
        for (Property property : properties()) {
            if (property.name().equals(name))
                return true;
        }
        return false;
    }

    @Override
    public Property property(String name) {
        for (Property property : properties()) {
            if (property.name().equals(name))
                return property;
        }
        return null;
    }

    @Override
    public boolean isBean() {
        return properties.size() > 0;
    }
}
