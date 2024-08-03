package run.slicer.jasm.teavm;

import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

public class JASMPlugin implements TeaVMPlugin {
    @Override
    public void install(TeaVMHost host) {
        host.add(new MethodStubTransformer());
        host.add(new MethodDelegationTransformer());
    }
}
