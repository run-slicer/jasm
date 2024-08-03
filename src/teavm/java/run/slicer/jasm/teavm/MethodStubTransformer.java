package run.slicer.jasm.teavm;

import org.teavm.model.*;
import org.teavm.model.instructions.ExitInstruction;
import org.teavm.model.instructions.InvocationType;
import org.teavm.model.instructions.InvokeInstruction;

public class MethodStubTransformer implements ClassHolderTransformer {
    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        switch (cls.getName()) {
            case "dev.xdark.blw.asm.internal.InternalAsmLibrary" -> {
                this.stubVoid(cls.getMethod(new MethodDescriptor("<clinit>", void.class)));
            }
            case "me.darknet.assembler.printer.InstructionPrinter" -> {
                this.stubWithCall(
                        cls.getMethod(new MethodDescriptor("<clinit>", void.class)),
                        new MethodReference(
                                "run.slicer.jasm.teavm.MethodDelegates",
                                "me_darknet_assembler_printer_InstructionPrinter_LTclinitGT",
                                ValueType.VOID
                        )
                );
            }
        }
    }

    private void stubVoid(MethodHolder method) {
        final Program program = this.newProgram(method.parameterCount());
        final BasicBlock block = program.createBasicBlock();

        block.add(new ExitInstruction());

        method.setProgram(program);
    }

    private void stubWithCall(MethodHolder method, MethodReference target) {
        final Program program = this.newProgram(method.parameterCount());

        final BasicBlock block = program.createBasicBlock();

        final var invokeInsn = new InvokeInstruction();
        invokeInsn.setType(InvocationType.VIRTUAL);
        invokeInsn.setMethod(target);
        block.add(invokeInsn);

        block.add(new ExitInstruction());

        method.setProgram(program);
    }

    private Program newProgram(int parameterCount) {
        parameterCount++; // type var

        final Program program = new Program();
        for (int i = 0; i < parameterCount; i++) {
            program.createVariable();
        }

        return program;
    }
}
