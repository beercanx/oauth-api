import {instantiate} from './compose-website-wasm.uninstantiated.mjs';

await wasmSetup;

let te = null;
try {
    await instantiate({skia: Module['asm']});
} catch (e) {
    te = e;
}

if (te == null) {
    console.log("Initialised")
} else {
    throw te;
}