# Style Guides

As of yet, I have not found an official style guide document for the Vega team. FOLIO as a whole,
however, mandates a two-space indentation style.

## Prettier Configuration

There is a `.prettierrc` in this repo with some basic settings (really just two-space indentation).

To use prettier on the codebase, you must have the following NPM modules globally available and in
your PATH:

```sh
npm install -g --save-dev prettier prettier-xml prettier-plugin-sql
```

## React

All JSX tags with two or more attributes must be separated on multiple lines, with the closing `>`
separate:

```jsx
<MyComponent
  prop1="foo"
  prop2="bar"
>
```

Self-closing is fine, provided that it follows the same guide (except with a closing `/>`).

CSS classes should be written in `camelCase`.
