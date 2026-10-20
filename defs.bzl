"""Public React4j build rules, macros and providers."""

load("//css:defs.bzl", _WebBundleInfo = "WebBundleInfo", _css_bundle = "css_bundle", _css_js_binary = "css_js_binary", _web_assets = "web_assets", _web_bundle = "web_bundle")
load("//tools/browser:rules.bzl", _BrowserApplicationInfo = "BrowserApplicationInfo", _browser_application = "browser_application", _browser_application_variant = "browser_application_variant", _browser_test = "browser_test")

visibility("public")

WebBundleInfo = _WebBundleInfo
css_bundle = _css_bundle
css_js_binary = _css_js_binary
web_assets = _web_assets
web_bundle = _web_bundle

BrowserApplicationInfo = _BrowserApplicationInfo
browser_application = _browser_application
browser_application_variant = _browser_application_variant
browser_test = _browser_test
