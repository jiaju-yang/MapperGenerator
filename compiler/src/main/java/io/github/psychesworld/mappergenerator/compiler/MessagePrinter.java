package io.github.psychesworld.mappergenerator.compiler;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

class MessagePrinter {
    private final Messager messager;

    MessagePrinter(Messager messager) {
        this.messager = messager;
    }

    void error() {
        messager.printMessage(Diagnostic.Kind.ERROR, "Unknow error.");
    }

    void error(String errorMsg) {
        messager.printMessage(Diagnostic.Kind.ERROR, errorMsg);
    }

    void errorElement() {
        messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated with @MapTo.");
    }

    void print(String msg) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }
}
