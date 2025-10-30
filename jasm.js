const isNode = typeof process !== "undefined";
const wasmPath = async () => {
    const url = new URL("./jasm.wasm", import.meta.url).href;
    return isNode ? await import("node:url").then((mod) => mod.fileURLToPath(url)) : url;
};

let disassembleFunc = null;
export const disassemble = async (b, options) => {
    if (!disassembleFunc) {
        const { load } = await import("./jasm.wasm-runtime.js");
        const { exports } = await load(await wasmPath(), { noAutoImports: true });

        disassembleFunc = exports.disassemble;
    }

    return disassembleFunc(b, options);
};
