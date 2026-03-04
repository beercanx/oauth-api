# Kotlin/JS Store
`yarn.lock` is auto-managed by Kotlin/JS Gradle plugin, everything 
else had been added to help Dependabot apply security updates.

## Syncing devDependencies
Because the Kotlin/JS Gradle plugin manages `yarn.lock`, we need to 
manually sync `devDependencies` to keep them up to date.

This can be done by running:
```bash
node reverse-engineer-dependencies.js
```