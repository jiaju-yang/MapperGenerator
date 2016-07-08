package io.github.psychesworld.mappergenerator.compiler;

import com.google.auto.service.AutoService;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import io.github.psychesworld.mappergenerator.annotations.MapTo;

@AutoService(Processor.class)
public final class MapperProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        MessagePrinter.init(processingEnv.getMessager());//TODO
        MessagePrinter messagePrinter = new MessagePrinter(processingEnv.getMessager());
        JavaFilePrinter javaFilePrinter = new JavaFilePrinter(processingEnv.getFiler());
        TypeUtils typeUtils = new TypeUtils(processingEnv.getTypeUtils());
        Set<? extends Element> elementsAnnotatedWithMapTo = roundEnv.getElementsAnnotatedWith(MapTo.class);
        new MapperGenerator(messagePrinter, javaFilePrinter, typeUtils, elementsAnnotatedWithMapTo).generate();
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();

        types.add(MapTo.class.getCanonicalName());
//        types.add(MapName.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

}
