let disassembleFunc = null;

export const disassemble = async (b, options) => {
    if (!disassembleFunc) {
        const { load } = await import("./runtime.js");
        const { exports } = await load(new URL("./jasm.wasm", import.meta.url).href);

        disassembleFunc = exports.disassemble;
    }

    return disassembleFunc(b, options);
};
