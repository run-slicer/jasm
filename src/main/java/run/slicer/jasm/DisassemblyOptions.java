package run.slicer.jasm;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public interface DisassemblyOptions extends JSObject {
    @JSBody(script = "return this.indent || '    ';") // 4 spaces
    String indent();
}
