package io.github.psychesworld.mappergenerator.compiler;

import com.squareup.javapoet.ClassName;

import java.util.List;

import javax.lang.model.element.TypeElement;

interface Bean {
    static Bean get(TypeUtils typeUtils, TypeElement element) {
        return new NormalBean(typeUtils, element);
    }

//    static Bean get(Property property){
//        return new
//    }

    ClassName className();
    String simpleClassName();
    List<Property> properties();
    boolean hasProperty(String name);
    Property property(String name);
    boolean isBean();
}
