# jasm

A JavaScript port of the [Jasm dis/assembler](https://github.com/jumanji144/Jasm).

## Example

```js
const fs = require("fs");
const { disassemble } = require("./jasm.js"); // get it from the dist/ directory or jsDelivr

const data = fs.readFileSync("./your/package/HelloWorld.class"); // read a class file
console.log(disassemble(data, {
    indent: "    ", // the string that should be used as the indent, defaults to 4 spaces
}));
```

Or see the browser-based proof-of-concept in the [docs](./docs) directory.

## Licensing

The supporting code for this project and the Jasm dis/assembler are licensed under the MIT License
([supporting code](./LICENSE), [Jasm](./LICENSE-JASM)).

_This project is not affiliated with, maintained or endorsed by the Jasm project in any way. Do NOT report issues with this project to the Jasm issue tracker._
