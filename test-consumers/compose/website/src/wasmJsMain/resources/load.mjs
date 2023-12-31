import {instantiate} from './compose-website.uninstantiated.mjs';

await wasmSetup;

await instantiate({skia: Module['asm']});