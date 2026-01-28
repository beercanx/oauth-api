import { pluginReact } from '@rsbuild/plugin-react';
import { pluginTypeCheck } from "@rsbuild/plugin-type-check";

// noinspection JSUnusedGlobalSymbols
export default {
    plugins: [
        pluginTypeCheck(),
        pluginReact(),
    ],
    html: {
        template: './src/index.html',
    },
    output: {
        assetPrefix: '/assets/authentication',
        module: true,
        distPath: 'build',
    },
};