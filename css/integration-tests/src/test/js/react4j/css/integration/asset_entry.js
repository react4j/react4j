goog.module('react4j.css.integration.AssetLink');
const Entry = goog.require('react4j.css.integration.AssetFixtures');
/** @return {string} */
window['assetIcon'] = () => Entry.icon();
/** @return {string} */
window['assetShadowed'] = () => Entry.shadowed();
window['ordinaryAssetText'] = 'asset:react4j/css/integration/asset.svg';
const Styles=goog.require('react4j.css.integration.CssAssetStyles');
/** @return {string} */
window['assetStyles']=()=>Styles.style();
