package io.github.psychesworld.mappergenerator.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import io.github.psychesworld.mappergenerator.annotations.mapper.AbstractMapper;

class Mapper {
    private final static String MAP_METHOD_NAME = "map";
    private final static String REVERSE_MAP_METHOD_NAME = "reverseMap";
    private final static String MAPPER_INFIX = "_To_";
    private final static String LIST_ITEM_NAME = "item";
    private final static ClassName CLASSNAME_ARRAYLIST = ClassName.get("java.util", "ArrayList");

    private final MapperGenerator mapperGenerator;

    private final String mapperName;
    private final Bean t1Bean;
    private final Bean t2Bean;
    private final String t1Name;
    private final String t2Name;

    //Javapoet class builder
    private final TypeSpec.Builder classBuilder;
    private final MethodSpec.Builder constructionMethodBuilder;
    private final List<FieldSpec.Builder> fieldBuilders = new ArrayList<>();
    private final MethodSpec.Builder mapMethodBuilder;
//    private final MethodSpec.Builder reverseMapMethodBuilder;

    private boolean isGenerated = false;

    public static Mapper get(MapperGenerator mapperGenerator, Bean fromBean, Bean toBean) {
        return new Mapper(mapperGenerator, fromBean, toBean);
    }

    private Mapper(MapperGenerator mapperGenerator, Bean t1, Bean t2) {
        this.mapperGenerator = mapperGenerator;
        this.mapperName = buildMapperName(t1, t2);
        this.t1Bean = t1;
        this.t2Bean = t2;
        this.t1Name = replaceFirstCharToLowerCase(t1.simpleClassName());
        this.t2Name = replaceFirstCharToLowerCase(t2.simpleClassName());

        classBuilder = TypeSpec.classBuilder(mapperName)
                .superclass(ParameterizedTypeName.get(
                        ClassName.get(AbstractMapper.class),
                        t1.className(),
                        t2.className()))
                .addModifiers(Modifier.PUBLIC);
        constructionMethodBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        mapMethodBuilder = MethodSpec.methodBuilder(MAP_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(t1.className(), t1Name)
                .returns(t2.className());
//        reverseMapMethodBuilder = MethodSpec.methodBuilder(REVERSE_MAP_METHOD_NAME)
//                .addAnnotation(Override.class)
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(t2.className(), t2Name)
//                .returns(t1.className());
    }

    public TypeSpec build() {
        buildMapMethodBody(t1Bean, t2Bean);
//        buildReverseMapMethodBody(t2Bean, t1Bean);
        for (FieldSpec.Builder builder : fieldBuilders) {
            classBuilder.addField(builder.build());
        }
        classBuilder.addMethod(constructionMethodBuilder.build());
        classBuilder.addMethod(mapMethodBuilder.build());
//        classBuilder.addMethod(reverseMapMethodBuilder.build());
        isGenerated = true;
        return classBuilder.build();
    }

    public String name() {
        return mapperName;
    }

    public ClassName className() {
        return ClassName.get(JavaFilePrinter.MAPPER_PACKAGE, mapperName);
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    private void buildMapMethodBody(Bean fromBean, Bean toBean) {
        String fromBeanName = t1Name;
        String toBeanName = t2Name;
        buildNewObjectStatement(mapMethodBuilder, toBean.className(), toBeanName);
        for (Property fromProperty : fromBean.properties()) {
            Property toProperty = toBean.property(fromProperty.name());
            if (toProperty != null) {
                buildPropertyStatement(mapMethodBuilder, fromBeanName, toBeanName, fromProperty,
                        toProperty);
            }
        }
        buildReturnStatement(mapMethodBuilder, toBeanName);
    }

//    private void buildReverseMapMethodBody(Bean fromBean, Bean toBean) {
//        String fromBeanName = t2Name;
//        String toBeanName = t1Name;
//        buildNewObjectStatement(reverseMapMethodBuilder, toBean.className(), toBeanName);
//        for (Property toProperty : toBean.properties()) {
//            Property fromProperty = fromBean.property(toProperty.name());
//            if (fromProperty != null) {
//                buildPropertyStatement(reverseMapMethodBuilder, fromBeanName, toBeanName,
//                        fromProperty, toProperty);
//            }
//        }
//        buildReturnStatement(reverseMapMethodBuilder, toBeanName);
//    }

    private void buildPropertyStatement(MethodSpec.Builder methodBuilder,
                                        String fromBeanName,
                                        String toBeanName,
                                        Property fromProperty,
                                        Property toProperty) {
        if (fromProperty.isTypeEquals(toProperty)) {
            if (toProperty.hasSetter() && fromProperty.hasGetter()) {
                buildSetStatement(methodBuilder,
                        toBeanName, toProperty.setterName(), fromBeanName, fromProperty.getterName());
            }
        } else if (fromProperty.isCollectionType()) {
            if (fromProperty.isListType()) {
                String toListName = toProperty.name();
                buildNewCollectionStatement(methodBuilder, toProperty.typeName(), toListName, CLASSNAME_ARRAYLIST);
                TypeElement fromTypeElement = fromProperty.generics().get(0);
                TypeElement toTypeElement = toProperty.generics().get(0);
                ClassName fromClassName = ClassName.get(fromTypeElement);
                ClassName toClassName = ClassName.get(toTypeElement);
                String fromVariableName;
                if (fromClassName.isPrimitive() || fromClassName.isBoxedPrimitive()) {
                    fromVariableName = "item";
                } else {
                    fromVariableName = replaceFirstCharToLowerCase(fromClassName.simpleName());
                }
                buildListForEachStartStatement(methodBuilder, fromClassName, fromVariableName, fromBeanName, fromProperty.getterName());

                if (fromClassName.equals(toClassName)) {
                    buildAddStatement(methodBuilder, toListName, fromVariableName);
                } else {
                    Bean newFromBean = Bean.get(mapperGenerator.typeUtils(), fromTypeElement);
                    Bean newToBean = Bean.get(mapperGenerator.typeUtils(), toTypeElement);
                    String mapperVariableName = replaceFirstCharToLowerCase(buildMapperName(newFromBean, newToBean));
                    buildNewMapper(newFromBean, newToBean);
                    buildMapperSetStatement(methodBuilder, toListName, mapperVariableName, fromVariableName);
                }

                buildForEachEndStatement(methodBuilder);
                if (toProperty.hasSetter()) {
                    buildSetStatement(methodBuilder, toBeanName, toProperty.setterName(), toProperty.name());
                }
            }

        } else {
//                    if (fromProperty.isPrimitiveType() || toProperty.isPrimitiveType()) {
//                        StringBuilder errorMsgBuilder = new StringBuilder();
//                        errorMsgBuilder
//                                .append("Error types of ")
//                                .append(fromProperty.name())
//                                .append(" in ")
//                                .append(fromBean.className().toString())
//                                .append(" and ")
//                                .append(toBean.className().toString());
//                        MessagePrinter.get().error(errorMsgBuilder.toString());
//                        System.exit(0);
//                    }
            Bean newFromBean = fromProperty.toBean();
            Bean newToBean = toProperty.toBean();
            String mapperVariableName = replaceFirstCharToLowerCase(buildMapperName(newFromBean, newToBean));
            buildNewMapper(newFromBean, newToBean);
            if (toProperty.hasSetter() && fromProperty.hasGetter()) {
                buildMapperSetStatement(methodBuilder, toBeanName, toProperty.setterName(), fromBeanName,
                        fromProperty.getterName(), mapperVariableName);
            }
        }
    }

    private static void buildSetStatement(MethodSpec.Builder methodBuilder,
                                          String toName,
                                          String toSetterName,
                                          String fromName,
                                          String fromGetterName) {
        methodBuilder.addStatement("$L.$L($L.$L())", toName, toSetterName, fromName, fromGetterName);
    }

//    private static void buildSetStatement(MethodSpec.Builder methodBuilder,
//                                          String toName,
//                                          Property toProperty,
//                                          String fromName,
//                                          Property fromProperty,
//                                          String newMapperName) {
//        if (toProperty.hasSetter() && fromProperty.hasGetter()) {
//            methodBuilder.addStatement("$L.$L($L.map($L.$L()))", toName, toProperty.setterName(), newMapperName, fromName, fromProperty.getterName());
//        }
//    }

    private static void buildMapperSetStatement(MethodSpec.Builder methodBuilder,
                                                String toName,
                                                String toSetter,
                                                String fromName,
                                                String fromGetter,
                                                String mapperName) {
        methodBuilder.addStatement("$L.$L($L.map($L.$L()))", toName, toSetter, mapperName, fromName, fromGetter);
    }

    private static void buildSetStatement(MethodSpec.Builder methodBuilder,
                                          String toName,
                                          String setterName,
                                          String variableName) {
        methodBuilder.addStatement("$L.$L($L)", toName, setterName, variableName);
    }

    private static void buildMapperSetStatement(MethodSpec.Builder methodBuilder,
                                                String toCollectionName,
                                                String mapperName,
                                                String fromVariableName) {
        methodBuilder.addStatement("$L.add($L.map($L))", toCollectionName, mapperName, fromVariableName);
    }

    private static void buildAddStatement(MethodSpec.Builder methodBuilder,
                                          String toCollectionName,
                                          String fromName) {
        methodBuilder.addStatement("$L.add($L)", toCollectionName, fromName);
    }

    private static void buildNewObjectStatement(MethodSpec.Builder methodBuilder,
                                                ClassName className,
                                                String variableName) {
        methodBuilder.addStatement("$T $L = new $T()", className, variableName, className);
    }

    private void buildNewMapper(Bean fromBean, Bean toBean) {
        String newMapperName = buildMapperName(fromBean,
                toBean);
        Mapper newMapper = mapperGenerator.getMapper(newMapperName);
//        if (newMapper == null) {
//            newMapperName = buildMapperName(toBean, fromBean);
//            newMapper = mapperGenerator.getMapper(newMapperName);
//        }
        if (newMapper != null) {
            mapperGenerator.generate(newMapper);
        } else {
            newMapper = Mapper.get(mapperGenerator, fromBean, toBean);
            mapperGenerator.addMapper(newMapper);
            mapperGenerator.generate(newMapper);
        }
        String newMapperVariableName = replaceFirstCharToLowerCase(newMapperName);
        buildNewField(fieldBuilders, newMapper.className(), newMapperVariableName);
        buildConstructionParameter(constructionMethodBuilder, newMapper.className(), newMapperVariableName);
    }

    private static void buildNewCollectionStatement(MethodSpec.Builder methodBuilder,
                                                    TypeName declareTypeName,
                                                    String variableName,
                                                    TypeName realTypeName) {
        methodBuilder.addStatement("$T $L = new $T<>()", declareTypeName, variableName, realTypeName);
    }

    private static void buildListForEachStartStatement(MethodSpec.Builder methodBuilder,
                                                       TypeName fromTypeName,
                                                       String fromName,
                                                       String fromParentName,
                                                       String fromGetterName) {
        methodBuilder.beginControlFlow("for ($T $L : $L.$L())", fromTypeName, fromName, fromParentName, fromGetterName);
    }

    private static void buildForEachEndStatement(MethodSpec.Builder methodBuilder) {
        methodBuilder.endControlFlow();
    }

    private static void buildNewField(List<FieldSpec.Builder> fieldBuilders, ClassName newMapperClassName, String newMapperVariableName) {
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(newMapperClassName, newMapperVariableName, Modifier.PRIVATE);
        fieldBuilders.add(fieldBuilder);
    }

    private static void buildConstructionParameter(MethodSpec.Builder constructionMethodBuilder,
                                                   ClassName newMapperClassName, String newMapperVariableName) {
        constructionMethodBuilder.addParameter(newMapperClassName, newMapperVariableName);
        constructionMethodBuilder.addStatement("this.$L = $L", newMapperVariableName, newMapperVariableName);
    }

    private static void buildReturnStatement(MethodSpec.Builder methodBuilder, String returnName) {
        methodBuilder.addStatement("return $L", returnName);
    }

    private static String replaceFirstCharToLowerCase(String string) {
        return string.substring(0, 1).toLowerCase().concat(string.substring(1));
    }

    private static String buildMapperName(Bean from, Bean to) {
        return from.simpleClassName() + MAPPER_INFIX + to.simpleClassName();
    }
}
