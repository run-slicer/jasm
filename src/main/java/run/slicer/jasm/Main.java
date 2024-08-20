package run.slicer.jasm;

import me.darknet.assembler.printer.JvmClassPrinter;
import me.darknet.assembler.printer.PrintContext;
import org.teavm.jso.JSByRef;
import org.teavm.jso.JSExport;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSPromise;
import org.teavm.jso.core.JSString;

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
                        new JvmClassPrinter(b).print(ctx);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }

                    resolve.accept(JSString.valueOf(ctx.toString()));
                } catch (Throwable e) {
                    reject.accept(e);
                }
            }).start();
        });
    }
}
