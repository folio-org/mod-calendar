# Style Guides

As of yet, I have not found an official style guide document for the Vega team. FOLIO as a whole,
however, mandates a two-space indentation style.

## Prettier Configuration

There is a `.prettierrc` in this repo with some basic settings (really just two-space indentation).

A simple `pre-commit` hook can be used to ensure these guidelines are being followed:

```sh
#!/bin/bash
set -euxo pipefail

RED="\033[1;31m"
GREEN="\033[1;32m"
NC="\033[0m"

files_to_lint=$(git diff --cached --diff-filter=d --name-only | egrep '\.(java|md|xml|sql|json|yaml|yml)$')

prettier --write $files_to_lint

git add -f $files_to_lint
```

To use this, you must have the following globally available and in your PATH (`npx` is a slow
alternative, too):

```sh
npm install -g --save-dev prettier prettier-xml
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
