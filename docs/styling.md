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

## SonarLint

[SonarLint](https://sonarlint.org/) is a wonderful IDE plugin to lint your code. I recommend
enabling the following rules:

```json
"sonarlint.rules": {
  "java:S1698": { "level": "on" },
  "java:S2208": { "level": "on" },
  "java:S3658": { "level": "on" },
  "java:S5867": { "level": "on" },
  "java:S2211": { "level": "on" },
  "java:S2693": { "level": "on" },
  "java:S1774": { "level": "on" },
  "java:S1213": { "level": "on" },
  "java:S5977": { "level": "on" },
  "java:S3414": { "level": "on" },
  "java:S3578": { "level": "on" },
  "java:S2698": { "level": "on" },
  "java:S2196": { "level": "on" },
  "java:S1132": { "level": "on" },
  "java:S122": { "level": "on" },
  "java:S4288": { "level": "on" },
  "java:S1942": { "level": "on" },
  "java:S1641": { "level": "on" },
  "java:S4248": { "level": "on" },
  "java:S1176": { "level": "on" },
  "java:S2301": { "level": "on" },
  "java:S3423": { "level": "on" },
  "java:S3047": { "level": "on" },
  "java:S2197": { "level": "on" },
  "java:S5793": { "level": "on" },
  "java:S3242": { "level": "on" },
  "java:S3749": { "level": "on" },
  "java:S2039": { "level": "on" },
  "java:S2164": { "level": "on" },
  "java:S109": { "level": "on" },
  "java:S2701": { "level": "on" },
  "java:S864": { "level": "on" },
  "java:S2260": { "level": "on" },
  "java:S881": { "level": "on" },
  "java:S1244": { "level": "on" },
  "java:S2309": { "level": "on" },
  "java:S1996": { "level": "on" },
  "java:S113": { "level": "on" },
  "java:S1939": { "level": "on" },
  "java:S1147": { "level": "on" },
  "java:S1166": { "level": "on" },
  "java:S888": { "level": "on" },
  "java:S4551": { "level": "on" },
  "java:S121": { "level": "on" },
  "java:S1699": { "level": "on" },
  "java:S1107": { "level": "on" },
  "java:S139": { "level": "on" },
  "java:S1943": { "level": "on" },
  "java:S1258": { "level": "on" },
  "java:S1105": { "level": "on" },
  "java:S1694": { "level": "on" },
  "java:S118": { "level": "on" },
  "java:S3366": { "level": "on" },
  "java:S1109": { "level": "on" },
  "java:S1151": { "level": "on" },
  "java:S2325": { "level": "on" },
  "java:S3553": { "level": "on" },
  "java:S1696": { "level": "on" },
  "java:S1695": { "level": "on" },
  "java:S2096": { "level": "on" },
  "java:S2221": { "level": "on" },
  "java:S2162": { "level": "on" },
  "java:S2308": { "level": "on" }
}
```
