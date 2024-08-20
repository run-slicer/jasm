declare module "@run-slicer/jasm" {
    export interface DisassemblyConfig {
        indent?: string;
    }

    export function disassemble(b: Uint8Array, config?: DisassemblyConfig): Promise<string>;
}
