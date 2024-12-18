declare module "@run-slicer/jasm" {
    export interface DisassemblyConfig {
        indent?: string;

        // single method disassembly
        signature?: string;
    }

    export function disassemble(b: Uint8Array, config?: DisassemblyConfig): Promise<string>;
}
