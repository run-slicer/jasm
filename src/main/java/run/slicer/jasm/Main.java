package run.slicer.jasm;

import me.darknet.assembler.printer.JvmClassPrinter;
import me.darknet.assembler.printer.PrintContext;
import org.teavm.jso.JSByRef;
import org.teavm.jso.JSExport;
import org.teavm.jso.core.JSObjects;

import java.io.IOException;
import java.io.UncheckedIOException;

public class Main {
    @JSExport
    public static String disassemble(@JSByRef byte[] b, DisassemblyOptions options) {
        return disassemble0(b, options == null || JSObjects.isUndefined(options) ? JSObjects.create() : options);
    }

    private static String disassemble0(byte[] b, DisassemblyOptions options) {
        final var ctx = new PrintContext<>(options.indent());

        try {
            new JvmClassPrinter(b).print(ctx);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return ctx.toString();
    }
}
