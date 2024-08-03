package run.slicer.jasm.teavm;

import dev.xdark.blw.asm.internal.InternalAsmLibrary;
import dev.xdark.blw.asm.internal.Util;
import dev.xdark.blw.constant.Constant;
import dev.xdark.blw.type.InvokeDynamic;
import dev.xdark.blw.type.MethodHandle;
import dev.xdark.blw.type.Types;
import me.darknet.assembler.printer.InstructionPrinter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;

import java.util.ArrayList;
import java.util.List;

import static run.slicer.jasm.teavm.MetaAccessors.getBootstrapMethods;
import static run.slicer.jasm.teavm.MetaAccessors.setOpcodes;

public final class MethodDelegates {
    private static final String[] MNEMONICS = new String[]{
            "nop", "aconst_null", "iconst_m1", "iconst_0", "iconst_1", "iconst_2", "iconst_3", "iconst_4", "iconst_5", "lconst_0",
            "lconst_1", "fconst_0", "fconst_1", "fconst_2", "dconst_0", "dconst_1", "bipush", "sipush", "ldc", "ldc_w",
            "ldc2_w", "iload", "lload", "fload", "dload", "aload", "iload_0", "iload_1", "iload_2", "iload_3",
            "lload_0", "lload_1", "lload_2", "lload_3", "fload_0", "fload_1", "fload_2", "fload_3", "dload_0", "dload_1",
            "dload_2", "dload_3", "aload_0", "aload_1", "aload_2", "aload_3", "iaload", "laload", "faload", "daload",
            "aaload", "baload", "caload", "saload", "istore", "lstore", "fstore", "dstore", "astore", "istore_0",
            "istore_1", "istore_2", "istore_3", "lstore_0", "lstore_1", "lstore_2", "lstore_3", "fstore_0", "fstore_1", "fstore_2",
            "fstore_3", "dstore_0", "dstore_1", "dstore_2", "dstore_3", "astore_0", "astore_1", "astore_2", "astore_3", "iastore",
            "lastore", "fastore", "dastore", "aastore", "bastore", "castore", "sastore", "pop", "pop2", "dup",  "dup_x1", "dup_x2",
            "dup2", "dup2_x1", "dup2_x2", "swap", "iadd", "ladd", "fadd", "dadd",  "isub", "lsub", "fsub", "dsub", "imul", "lmul", "fmul",
            "dmul", "idiv", "ldiv",  "fdiv", "ddiv", "irem", "lrem", "frem", "drem", "ineg", "lneg", "fneg", "dneg",  "ishl", "lshl",
            "ishr", "lshr", "iushr", "lushr", "iand", "land", "ior", "lor",  "ixor", "lxor", "iinc", "i2l", "i2f", "i2d", "l2i", "l2f",
            "l2d", "f2i", "f2l", "f2d", "d2i", "d2l", "d2f", "i2b", "i2c", "i2s", "lcmp", "fcmpl",  "fcmpg", "dcmpl",  "dcmpg",
            "ifeq", "ifne", "iflt", "ifge", "ifgt", "ifle", "if_icmpeq",  "if_icmpne", "if_icmplt", "if_icmpge",  "if_icmpgt",
            "if_icmple", "if_acmpeq", "if_acmpne", "goto", "jsr", "ret",  "tableswitch", "lookupswitch", "ireturn",  "lreturn",
            "freturn", "dreturn", "areturn", "return", "getstatic", "putstatic", "getfield", "putfield",  "invokevirtual",
            "invokespecial", "invokestatic", "invokeinterface", "invokedynamic", "new", "newarray", "anewarray",  "arraylength",
            "athrow", "checkcast", "instanceof", "monitorenter", "monitorexit", "wide", "multianewarray", "ifnull", "ifnonnull",
            "goto_w", "jsr_w"
    };

    private MethodDelegates() {
    }

    public static InvokeDynamic dev_xdark_blw_asm_internal_InternalAsmLibrary_readInvokeDynamic(InternalAsmLibrary ignored, ClassReader cr, int cpInfoOffset, char[] charBuffer) {
        int nameAndTypeCpInfoOffset = cr.getItem(cr.readUnsignedShort(cpInfoOffset + 2));
        String name = cr.readUTF8(nameAndTypeCpInfoOffset, charBuffer);
        String descriptor = cr.readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
        int[] bootstrapMethodOffsets = (int[]) getBootstrapMethods(cr.getClass(), cr); // change: remove VarHandle
        int bootstrapMethodOffset = bootstrapMethodOffsets[cr.readUnsignedShort(cpInfoOffset)];
        MethodHandle methodHandle = Util.wrapMethodHandle((Handle) cr.readConst(cr.readUnsignedShort(bootstrapMethodOffset), charBuffer));
        int argCount = cr.readUnsignedShort(bootstrapMethodOffset + 2);
        List<Constant> args = new ArrayList<>(argCount);
        bootstrapMethodOffset += 4;
        for (int i = 0; i < argCount; i++) {
            args.add(Util.wrapConstant(cr.readConst(cr.readUnsignedShort(bootstrapMethodOffset), charBuffer)));
            bootstrapMethodOffset += 2;
        }
        return new InvokeDynamic(name, Types.methodType(descriptor), methodHandle, args);
    }

    public static void me_darknet_assembler_printer_InstructionPrinter_LTclinitGT() {
        setOpcodes(InstructionPrinter.class, MNEMONICS);
    }
}
