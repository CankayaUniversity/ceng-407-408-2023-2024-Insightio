module.exports = {
  rules: {
    'prettier/prettier': 'off',
    'svelte/no-at-html-tags': 'off'
  },
  parserOptions: {
    extraFileExtensions: ['.svelte']
  },
  extends: [
    'eslint:recommended',
    'plugin:svelte/recommended',
    '@electron-toolkit',
    '@electron-toolkit/eslint-config-prettier'
  ]
}
