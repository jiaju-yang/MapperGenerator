package io.github.psychesworld.mappergenerator.compiler;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;

class JavaFilePrinter {
    public final static String MAPPER_PACKAGE = "io.github.psychesworld.mappergenerator.mapper";

    private final static String INDENT_SPACE = "  ";
    private final static String FILE_WARNING = "Generated code from MapperGenerator. Do not modify!";

    private final Filer filer;

    JavaFilePrinter(Filer filer) {
        this.filer = filer;
    }

    public void print(TypeSpec mapper){
        try {
            buildJavaFile(mapper).writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JavaFile buildJavaFile(TypeSpec mapper) {
        return JavaFile.builder(MAPPER_PACKAGE, mapper)
                .indent(INDENT_SPACE)
                .addFileComment(FILE_WARNING)
                .build();
    }
}
