// MIT License
//
// Copyright (c) 2024 Yuta Saito
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

export function parseImports(moduleBytes) {
    if (moduleBytes instanceof Uint8Array) {
        // no cast needed
    } else if (moduleBytes instanceof ArrayBuffer) {
        // cast ArrayBuffer to Uint8Array
        moduleBytes = new Uint8Array(moduleBytes);
    } else if (moduleBytes.buffer instanceof ArrayBuffer) {
        // cast TypedArray or DataView to Uint8Array
        moduleBytes = new Uint8Array(moduleBytes.buffer);
    } else {
        throw new Error("Argument must be a buffer source, like Uint8Array or ArrayBuffer");
    }
    const parseState = new ParseState(moduleBytes);
    parseMagicNumber(parseState);
    parseVersion(parseState);

    const imports = [];

    while (parseState.hasMoreBytes()) {
        const sectionId = parseState.readByte();
        const sectionSize = parseState.readUnsignedLEB128();
        switch (sectionId) {
            case 2: {
                // Ok, found import section
                const importCount = parseState.readUnsignedLEB128();
                for (let i = 0; i < importCount; i++) {
                    const module = parseState.readName();
                    const name = parseState.readName();
                    const type = parseState.readByte();
                    switch (type) {
                        case 0x00:
                            parseState.readUnsignedLEB128(); // index
                            imports.push({ module, name, kind: "function" });
                            break;
                        case 0x01:
                            imports.push({ module, name, kind: "table" });
                            parseTableType(parseState);
                            break;
                        case 0x02:
                            imports.push({ module, name, kind: "memory" });
                            parseLimits(parseState);
                            break;
                        case 0x03:
                            imports.push({ module, name, kind: "global" });
                            parseGlobalType(parseState)
                            break;
                        default:
                            throw new Error(`Unknown import descriptor type ${type}`);
                    }
                }
                // Skip the rest of the module
                return imports;
            }
            default: {
                parseState.skipBytes(sectionSize);
                break;
            }
        }
    }
    return [];
}

class ParseState {
    constructor(moduleBytes) {
        this.moduleBytes = moduleBytes;
        this.offset = 0;
        this.textDecoder = new TextDecoder("utf-8");
    }

    hasMoreBytes() {
        return this.offset < this.moduleBytes.length;
    }

    readByte() {
        return this.moduleBytes[this.offset++];
    }

    skipBytes(count) {
        this.offset += count;
    }

    /// Read unsigned LEB128 integer
    readUnsignedLEB128() {
        let result = 0;
        let shift = 0;
        let byte;
        do {
            byte = this.readByte();
            result |= (byte & 0x7F) << shift;
            shift += 7;
        } while (byte & 0x80);
        return result;
    }

    readName() {
        const nameLength = this.readUnsignedLEB128();
        const nameBytes = this.moduleBytes.slice(this.offset, this.offset + nameLength);
        const name = this.textDecoder.decode(nameBytes);
        this.offset += nameLength;
        return name;
    }

    assertBytes(expected) {
        const baseOffset = this.offset;
        const expectedLength = expected.length;
        for (let i = 0; i < expectedLength; i++) {
            if (this.moduleBytes[baseOffset + i] !== expected[i]) {
                throw new Error(`Expected ${expected} at offset ${baseOffset}`);
            }
        }
        this.offset += expectedLength;
    }
}

function parseMagicNumber(parseState) {
    const expected = [0x00, 0x61, 0x73, 0x6D];
    parseState.assertBytes(expected);
}

function parseVersion(parseState) {
    const expected = [0x01, 0x00, 0x00, 0x00];
    parseState.assertBytes(expected);
}

function parseTableType(parseState) {
    parseState.skipBytes(1);
    parseLimits(parseState);
}

function parseLimits(parseState) {
    const flags = parseState.readByte();
    parseState.readUnsignedLEB128(); // minimum
    const hasMaximum = flags & 1;
    if (hasMaximum) {
        parseState.readUnsignedLEB128(); // maximum
    }
}

function parseGlobalType(parseState) {
    parseValueType(parseState);
    parseState.skipBytes(1);
}

function parseValueType(parseState) {
    parseState.skipBytes(1);
}
