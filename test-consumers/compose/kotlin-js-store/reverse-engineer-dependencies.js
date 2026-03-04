#!/usr/bin/env node

/**
 * reverse-engineer-deps.js
 *
 * Parses a yarn.lock (v1) file and determines which packages are top-level
 * dependencies (i.e., not depended upon by any other package in the lockfile).
 * Outputs a package.json-style dependencies block.
 *
 * Usage:
 *   node reverse-engineer-deps.js [path/to/yarn.lock]
 *
 * Defaults to ./yarn.lock if no argument is provided.
 */

const fs = require('fs');
const path = require('path');

const lockfilePath = process.argv[2] || path.join(__dirname, 'yarn.lock');

if (!fs.existsSync(lockfilePath)) {
  console.error(`Error: yarn.lock not found at ${lockfilePath}`);
  process.exit(1);
}

const content = fs.readFileSync(lockfilePath, 'utf8');
const lines = content.split('\n');

// --- Parse the yarn.lock into structured entries ---

const entries = []; // { keys: string[], version: string, deps: [] }
let current = null;
let inDependencies = false;
let inOptionalDependencies = false;

for (const line of lines) {
  if (line.startsWith('#') || line.trim() === '') {
    if (current && line.trim() === '') {
      entries.push(current);
      current = null;
      inDependencies = false;
      inOptionalDependencies = false;
    }
    continue;
  }

  if (!line.startsWith(' ')) {
    if (current) {
      entries.push(current);
    }
    inDependencies = false;
    inOptionalDependencies = false;

    const keyLine = line.replace(/:$/, '').trim();
    const keys = keyLine.split(',').map(k => k.trim().replace(/^"|"$/g, ''));

    current = { keys, version: '', deps: [] };
    continue;
  }

  if (!current) continue;

  const trimmed = line.trim();

  if (trimmed.startsWith('version ')) {
    current.version = trimmed.replace(/^version\s+"/, '').replace(/"$/, '');
  } else if (trimmed === 'dependencies:') {
    inDependencies = true;
    inOptionalDependencies = false;
  } else if (trimmed === 'optionalDependencies:') {
    inDependencies = false;
    inOptionalDependencies = true;
  } else if (trimmed.startsWith('resolved ') || trimmed.startsWith('integrity ')) {
    // metadata, skip
  } else if (inDependencies || inOptionalDependencies) {
    // Dependency lines:  "package-name" "^1.0.0"
    // or npm alias:      wrap-ansi-cjs "npm:wrap-ansi@^7.0.0"
    const depMatch = trimmed.match(/^"?([^"\s]+)"?\s+"?([^"]+)"?$/);
    if (depMatch) {
      current.deps.push({ name: depMatch[1], range: depMatch[2] });
    }
  }
}
if (current) {
  entries.push(current);
}

// --- Build a set of all package names that are depended upon by something ---

const depended = new Set();

for (const entry of entries) {
  for (const dep of entry.deps) {
    // Add the dependency name as-is (e.g., "wrap-ansi-cjs", "string-width")
    depended.add(dep.name);

    // For npm: aliases like "npm:wrap-ansi@^7.0.0", also mark the alias target
    if (dep.range.startsWith('npm:')) {
      const aliasTarget = dep.range.substring(4); // "wrap-ansi@^7.0.0"
      const atIdx = aliasTarget.lastIndexOf('@');
      if (atIdx > 0) {
        depended.add(aliasTarget.substring(0, atIdx)); // "wrap-ansi"
      }
    }
  }
}

// --- Collect all alias names that appear in entry keys ---
// Keys like "wrap-ansi-cjs@npm:wrap-ansi@^7.0.0" indicate an alias resolution.
// These should never appear as top-level dependencies.

const aliasNames = new Set();

for (const entry of entries) {
  for (const key of entry.keys) {
    if (key.includes('@npm:')) {
      const name = extractPackageName(key);
      aliasNames.add(name);
    }
  }
}

// --- Find top-level packages ---

function extractPackageName(key) {
  // Handle npm: aliases: "wrap-ansi-cjs@npm:wrap-ansi@^7.0.0"
  // The package name is everything before the first "@npm:" or before the last "@"
  const npmIdx = key.indexOf('@npm:');
  if (npmIdx > 0) {
    return key.substring(0, npmIdx);
  }
  // Handle scoped packages: @scope/name@version
  const atIdx = key.lastIndexOf('@');
  if (atIdx <= 0) return key;
  return key.substring(0, atIdx);
}

// Group entries by package name
const packageMap = new Map();

for (const entry of entries) {
  for (const key of entry.keys) {
    const name = extractPackageName(key);
    // Skip npm: alias keys entirely
    if (key.includes('@npm:')) continue;
    if (!packageMap.has(name)) {
      packageMap.set(name, { version: entry.version, keys: entry.keys });
    }
  }
}

// Determine top-level
const topLevel = {};

for (const [name, info] of packageMap) {
  // Skip if depended upon by another package
  if (depended.has(name)) continue;
  // Skip npm: alias names
  if (aliasNames.has(name)) continue;

  const key = info.keys.find(k => !k.includes('@npm:')) || info.keys[0];
  const rawRange = key.substring(name.length + 1);

  let specifier;
  if (rawRange.includes('github:') || rawRange.includes('://')) {
    specifier = rawRange;
  } else {
    specifier = info.version;
  }

  topLevel[name] = specifier;
}

// --- Output ---

const sorted = Object.keys(topLevel).sort();
const devDependencies = {};
for (const k of sorted) {
  devDependencies[k] = topLevel[k];
}

const packageJson = {
  name: "kotlin-js-store",
  private: true,
  description: "Auto-managed by Kotlin/JS Gradle plugin",
  devDependencies
};

console.log(JSON.stringify(packageJson, null, 2));