let runtimeView;
/** @return {!Object} */
RuntimeBrowserTest.component = function() {
  const react = window['React'];
  return class extends react['Component'] {
    constructor(props) {
      super(props);
      runtimeView = this;
      this.state = {count: 1};
    }
    render() { return react.createElement('span', null, this.props.label + ':' + this.state.count); }
  };
};
/** @return {!Object} */
RuntimeBrowserTest.view = function() { return runtimeView; };
/** @return {!Object} */
RuntimeBrowserTest.emptyView = function() { return new window['React']['Component'](null); };
/** @param {!Object} view @return {!Object} */
RuntimeBrowserTest.props = function(view) { return view.props; };
/** @param {!Object} view @return {!Object} */
RuntimeBrowserTest.state = function(view) { return view.state; };
/** @param {*} element @param {!Element} root */
RuntimeBrowserTest.render = function(element, root) { window['ReactDOM'].render(element, root); };
/** @param {!Element} root */
RuntimeBrowserTest.unmount = function(root) { window['ReactDOM'].unmountComponentAtNode(root); };
