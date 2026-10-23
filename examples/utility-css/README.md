# Utility CSS example

Run from the repository root:

```sh
tools/examples.sh serve utility-css development
```

Open [localhost:3000](http://localhost:3000), then click **Save changes** to switch
the button's utility classes. Use `production` instead of `development` to run
the optimized application with compressed CSS names and unused rules removed.

[SaveButton.java](src/main/java/react4j/utilitydemo/SaveButton.java) is an ordinary
stateful React4j view. Its nested styles use `@UtilityStyles` to select
[Utilities.java](src/main/java/react4j/utilitydemo/Utilities.java), which loads the
supplied [utilities.css](src/main/java/react4j/utilitydemo/utilities.css) file.
The small stylesheet uses familiar utility names and needs no generator.

The example shows four parts of the authoring contract:

- `@UtilityCss` methods select checked class lists, including a responsive utility.
- The button combines its base getter with either its ready or saved getter;
  modifier getters do not inherit the base utility list.
- Local `@Css` gives the button a pill shape, overriding the utility's rounded corners.
- An explicit `@CssImport` selects global defaults independently of the utility library.

The unselected `unused-outline` utility makes pruning visible in the production
CSS. See the [CSS authoring guide](../../css/README.md#utility-libraries) for
input requirements and supported syntax.
