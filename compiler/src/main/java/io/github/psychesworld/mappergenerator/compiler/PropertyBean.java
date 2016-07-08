package io.github.psychesworld.mappergenerator.compiler;

import com.squareup.javapoet.ClassName;

import java.util.List;

class PropertyBean extends Property implements Bean {
    private final Bean bean;

    PropertyBean(Property property) {
        super(property);
        bean = Bean.get(property.typeUtils, property.typeElement);
    }

    @Override
    public String simpleClassName() {
        return bean.simpleClassName();
    }

    @Override
    public List<Property> properties() {
        return bean.properties();
    }

    @Override
    public boolean hasProperty(String name) {
        return bean.hasProperty(name);
    }

    @Override
    public Property property(String name) {
        return bean.property(name);
    }

    @Override
    public boolean isBean() {
        return bean.isBean();
    }

    @Override
    public ClassName className() {
        return bean.className();
    }
}
