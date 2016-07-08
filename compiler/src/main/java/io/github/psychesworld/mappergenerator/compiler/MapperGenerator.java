package io.github.psychesworld.mappergenerator.compiler;

import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Collection;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

class MapperGenerator {
    private final MessagePrinter messagePrinter;
    private final JavaFilePrinter javaFilePrinter;
    private final TypeUtils typeUtils;

    private final Collection<Mapper> mappers;
    private final Collection<Mapper> mappersWithoutAnnotation;

    MapperGenerator(MessagePrinter messagePrinter,
                    JavaFilePrinter javaFilePrinter,
                    TypeUtils typeUtils,
                    Collection<? extends Element> mapperElements) {
        this.messagePrinter = messagePrinter;
        this.javaFilePrinter = javaFilePrinter;
        this.typeUtils = typeUtils;

        mappers = new ArrayList<>();
        mappersWithoutAnnotation = new ArrayList<>();
        for (Element element : mapperElements) {
//            if (!isValidMapperSource(element)) {
//                return true;
//            }
            TypeElement mapperSource = (TypeElement) element;
            TypeElement mapperTarget = typeUtils.findMapperTarget(mapperSource);
//            if (!isValidMapperTarget(mapperTarget)) {
//                return true;
//            }
            Bean fromBean = Bean.get(typeUtils, mapperSource);
            Bean toBean = Bean.get(typeUtils, mapperTarget);
            mappers.add(Mapper.get(this, fromBean, toBean));
            mappers.add(Mapper.get(this, toBean, fromBean));
        }

    }

    public void generate() {
        for (Mapper mapper : mappers) {
            generate(mapper);
        }
    }

    public void generate(Mapper mapper) {
        if (!mapper.isGenerated()) {
            TypeSpec mapperSpec = mapper.build();
            javaFilePrinter.print(mapperSpec);
        }
    }

    public Mapper getMapper(String name) {
        for (Mapper mapper : mappers) {
            if (mapper.name().equals(name))
                return mapper;
        }
        for (Mapper mapper : mappersWithoutAnnotation) {
            if (mapper.name().equals(name))
                return mapper;
        }
        return null;
    }

    public void addMapper(Mapper mapper){
        mappersWithoutAnnotation.add(mapper);
    }

    public TypeUtils typeUtils(){
        return typeUtils;
    }

//    private boolean isValidMapperSource(Element mapperSource){
//        if (mapperSource == null) {
////            MessagePrinter.get().error();
//            return false;
//        }
//        if (!isClass(mapperSource)) {
////            MessagePrinter.get().errorElement();
//            return false;
//        }
//        return true;
//    }
//
//    private boolean isValidMapperTarget(Element mapperTarget){
//        if (mapperTarget == null) {
////            MessagePrinter.get().error();
//            return false;
//        }
//        if (!isClass(mapperTarget)) {
////            MessagePrinter.get().errorElement();
//            return false;
//        }
//        return true;
//    }
//
//    private boolean isClass(Element element){
//        return element.getKind() == ElementKind.CLASS;
//    }

}
