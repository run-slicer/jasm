package run.slicer.jasm.teavm;

import org.teavm.metaprogramming.CompileTime;
import org.teavm.metaprogramming.Meta;
import org.teavm.metaprogramming.ReflectClass;
import org.teavm.metaprogramming.Value;
import org.teavm.metaprogramming.reflect.ReflectField;

import static org.teavm.metaprogramming.Metaprogramming.emit;
import static org.teavm.metaprogramming.Metaprogramming.exit;
import static org.teavm.metaprogramming.Metaprogramming.unsupportedCase;

@CompileTime
public final class MetaAccessors {
    private MetaAccessors() {
    }

    @Meta
    public static native Object getBootstrapMethods(Class<?> cls, Object obj);

    private static void getBootstrapMethods(ReflectClass<Object> cls, Value<Object> obj) {
        if (!cls.getName().equals("org.objectweb.asm.ClassReader")) {
            unsupportedCase();
            return;
        }

        final ReflectField field = cls.getDeclaredField("bootstrapMethodOffsets");
        if (field != null) {
            exit(() -> field.get(obj));
        } else {
            throw new RuntimeException("Could not find bootstrapMethodOffsets field");
        }
    }

    @Meta
    public static native void setOpcodes(Class<?> cls, Object val);

    private static void setOpcodes(ReflectClass<Object> cls, Value<Object> val) {
        if (!cls.getName().equals("me.darknet.assembler.printer.InstructionPrinter")) {
            unsupportedCase();
            return;
        }

        final ReflectField field = cls.getDeclaredField("OPCODES");
        if (field != null) {
            // TeaVM doesn't care about the final modifier thankfully
            emit(() -> field.set(null, val));
        } else {
            throw new RuntimeException("Could not find OPCODES field");
        }
    }
}
