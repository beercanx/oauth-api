/** @type {import('ts-jest').JestConfigWithTsJest} */
module.exports = {
  resetMocks: false,
  clearMocks: true,
  preset: 'ts-jest',
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: [
    "<rootDir>/src/setupTests.ts"
  ],
};