package io.github.psychesworld.mappergenerator.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

class Property {
    private static final String[] GETTER_PREFIXS = {"get", "is"};
    private static final String[] SETTER_PREFIXS = {"set"};
    private static final ClassName CLASSNAME_COLLECTION = ClassName.get("java.util", "Collection");
    private static final ClassName CLASSNAME_LIST = ClassName.get("java.util", "List");

//    private static final ClassName SET_CLASSNAME = ClassName.get("java.util", "Set");

    protected final TypeUtils typeUtils;

    private final String name;
    private final String setterName;
    private final String getterName;
    private final TypeMirror typeMirror;
    private final TypeName typeName;

    protected ClassName className;
    protected TypeElement typeElement;

//    private VariableElement variableElement;

    static Property get(TypeUtils typeUtils, VariableElement variableElement, TypeElement beanTypeElement) {
        return new Property(typeUtils, variableElement, beanTypeElement);
    }

    private Property(TypeUtils typeUtils, VariableElement variableElement, TypeElement beanTypeElement) {
        this.typeUtils = typeUtils;
        this.name = variableElement.getSimpleName().toString();
        this.setterName = setterName(beanTypeElement, this.name);
        this.getterName = getterName(beanTypeElement, this.name);
        this.typeMirror = variableElement.asType();
        this.typeName = TypeName.get(typeMirror);
//        this.variableElement = variableElement;

        if (!typeName.isPrimitive()) {
            this.typeElement = typeUtils.asTypeElement(typeMirror);
            this.className = ClassName.get(typeElement);
        }
    }

    protected Property(Property property) {
        this.typeUtils = property.typeUtils;
        this.name = property.name;
        this.setterName = property.setterName;
        this.getterName = property.getterName;
        this.typeMirror = property.typeMirror;
        this.typeName = property.typeName;

        this.className = property.className;
        this.typeElement = property.typeElement;
    }

    public String name() {
        return name;
    }

    public boolean hasGetter() {
        return getterName.length() > 0;
    }

    public String getterName() {
        return getterName;
    }

    public boolean hasSetter() {
        return setterName.length() > 0;
    }

    public String setterName() {
        return setterName;
    }

    public boolean isTypeEquals(Property property) {
        return typeName.equals(property.typeName);
    }

    public boolean isPrimitiveType() {
        return typeName.isPrimitive();
    }

    public boolean isCollectionType() {
        if(isPrimitiveType())
            return false;
        if(ClassName.get(typeElement).equals(CLASSNAME_COLLECTION))
            return true;
        List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
        for (TypeMirror typeMirror : interfaces) {
            TypeElement typeElement = typeUtils.asTypeElement(typeMirror);
            ClassName className = ClassName.get(typeElement);
            if(className.equals(CLASSNAME_COLLECTION)) {
                return true;
            }
        }
        return false;
    }

    public boolean isListType(){
        if(isPrimitiveType())
            return false;
        if(ClassName.get(typeElement).equals(CLASSNAME_LIST))
            return true;
        List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
        for (TypeMirror typeMirror : interfaces) {
            TypeElement typeElement = typeUtils.asTypeElement(typeMirror);
            ClassName className = ClassName.get(typeElement);
            if(className.equals(CLASSNAME_LIST)) {
                return true;
            }
        }
        return false;
    }

//    public List<ClassName> generics() {
//        List<ClassName> genericTypes = new ArrayList<>();
//        DeclaredType declaredType = (DeclaredType) typeMirror;
//        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
//        for (TypeMirror typeArgument : typeArguments) {
//            ClassName className = ClassName.get(typeUtils.asTypeElement(typeArgument));
//            genericTypes.add(className);
//        }
//        return genericTypes;
//    }

    public List<TypeElement> generics(){
        List<TypeElement> genericTypes = new ArrayList<>();
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        for (TypeMirror typeArgument : typeArguments) {
            genericTypes.add(typeUtils.asTypeElement(typeArgument));
        }
        return genericTypes;
    }

    public PropertyBean toBean() {
        return new PropertyBean(this);
    }

    public TypeName typeName() {
        return typeName;
    }

    private static String getterName(TypeElement beanTypeElement, String propertyName) {
        for (Element enclosedElement : beanTypeElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) enclosedElement;
                String methodName = method.getSimpleName().toString();
                for (String getterPrefix : GETTER_PREFIXS) {
                    if (methodName.startsWith(getterPrefix)) {
                        String withoutPrefixMethodName = methodName.substring(getterPrefix.length());
                        if (withoutPrefixMethodName.toLowerCase().equals(propertyName.toLowerCase())) {
                            return methodName;
                        }
                    }
                }
            }
        }
        return "";
    }

    private static String setterName(TypeElement beanTypeElement, String propertyName) {
        for (Element enclosedElement : beanTypeElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) enclosedElement;
                String methodName = method.getSimpleName().toString();
                for (String setterPrefix : SETTER_PREFIXS) {
                    if (methodName.startsWith(setterPrefix)) {
                        String withoutPrefixMethodName = methodName.substring(setterPrefix.length());
                        if (withoutPrefixMethodName.toLowerCase().equals(propertyName.toLowerCase())) {
                            return methodName;
                        }
                    }
                }
            }
        }
        return "";
    }
}
