/** @type {!React.Component} */
let runtimeView;
/** @return {!Object} */
RuntimeBrowserTest.component = function() {
  const react = window['React'];
  return class extends React.Component {
    constructor(props) {
      super(props);
      runtimeView = this;
      this.state = {'count': 1};
    }
    render() { return react.createElement('span', null, this.props['label'] + ':' + this.state['count']); }
  };
};
/** @return {!React.Component} */
RuntimeBrowserTest.view = function() { return runtimeView; };
/** @return {!React.Component} */
RuntimeBrowserTest.emptyView = function() { return new React.Component(null); };
/** @param {!React.Component} view @return {!Object} */
RuntimeBrowserTest.props = function(view) { return view.props; };
/** @param {!React.Component} view @return {!Object} */
RuntimeBrowserTest.state = function(view) { return view.state; };
/** @param {*} element @param {!Element} root */
RuntimeBrowserTest.render = function(element, root) { window['ReactDOM'].render(element, root); };
/** @param {!Element} root */
RuntimeBrowserTest.unmount = function(root) { window['ReactDOM'].unmountComponentAtNode(root); };
