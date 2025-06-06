let disassembleFunc = null;

export const disassemble = async (b, options) => {
    if (!disassembleFunc) {
        const { TeaVM } = await import("./jasm.wasm-runtime.js");
        const { exports } = await TeaVM.wasmGC.load(new URL("./jasm.wasm", import.meta.url).href);

        disassembleFunc = (b, options) => exports.disassemble(b, options);
    }

    return disassembleFunc(b, options);
};
