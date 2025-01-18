package run.slicer.jasm;

import dev.xdark.blw.BytecodeLibrary;
import dev.xdark.blw.asm.AsmBytecodeLibrary;
import dev.xdark.blw.asm.ClassWriterProvider;
import dev.xdark.blw.classfile.ClassFileView;
import dev.xdark.blw.classfile.Method;
import dev.xdark.blw.classfile.generic.GenericClassBuilder;
import me.darknet.assembler.printer.JvmClassPrinter;
import me.darknet.assembler.printer.JvmMethodPrinter;
import me.darknet.assembler.printer.PrintContext;
import org.teavm.jso.JSByRef;
import org.teavm.jso.JSExceptions;
import org.teavm.jso.JSExport;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSPromise;
import org.teavm.jso.core.JSString;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public class Main {
    @JSExport
    public static JSPromise<JSString> disassemble(@JSByRef byte[] b, DisassemblyOptions options) {
        return disassemble0(b, options == null || JSObjects.isUndefined(options) ? JSObjects.create() : options);
    }

    private static JSPromise<JSString> disassemble0(byte[] b, DisassemblyOptions options) {
        return new JSPromise<>((resolve, reject) -> {
            new Thread(() -> {
                try {
                    final var ctx = new PrintContext<>(options.indent());

                    try {
                        if (options.signature() != null) {
                            disassembleMethod(ctx, b, options.signature());
                        } else {
                            disassembleClass(ctx, b);
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }

                    resolve.accept(JSString.valueOf(ctx.toString()));
                } catch (Throwable e) {
                    reject.accept(JSExceptions.getJSException(e));
                }
            }).start();
        });
    }
    
    private static void disassembleClass(PrintContext<?> ctx, byte[] b) throws IOException {
        new JvmClassPrinter(b).print(ctx);
    }

    private static final BytecodeLibrary LIBRARY = new AsmBytecodeLibrary(ClassWriterProvider.flags(2));
    private static void disassembleMethod(PrintContext<?> ctx, byte[] b, String signature) throws IOException {
        final int index = signature.indexOf("(");
        if (index == -1) {
            throw new IllegalArgumentException("Malformed signature");
        }

        final String name = signature.substring(0, index);
        final String desc = signature.substring(index);

        final var builder = new GenericClassBuilder();
        LIBRARY.read(new ByteArrayInputStream(b), builder);

        final ClassFileView view = builder.build();
        for (final Method method : view.methods()) {
            if (method.name().equals(name) && method.type().descriptor().equals(desc)) {
                new JvmMethodPrinter(method).print(ctx);
                return;
            }
        }
    }
}
