# Changelog

## Auto Mortar 1.1 *07/01/15*

 * Big rewrite of internal API, better stability, performances and error handling. Use processor-workflow under the hood
 * New: Change maven dependency groupId from `com.github.lukaspili` to **`com.github.lukaspili.automortar`**
 * Fix: Remove Mortar and Dagger2 dependencies
 * Remove: Butterknife config option
 * BREAKING CHANGE: `ScreenComponentFactory` has now a parameterized type, and `createComponent` takes only 1 dependency as parameter
 * BREAKING CHANGE: The package of the generated screen changed, you need to clean and re-import the generated screen classes


## Auto Mortar 1.0 

- Rename project to Auto Mortar
- Rely on Auto Dagger2 for dagger generation
- Change naming policy for screens: `XyzPresenter` generates `XyzScreen`
- Rename `@MVP` to `@AutoScreen`
- Remove `@WithComponent` and `@WithInjector`
- Remove base view generation