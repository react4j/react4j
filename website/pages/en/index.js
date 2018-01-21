const React = require('react');

const siteConfig = require(process.cwd() + '/siteConfig.js');

class Button extends React.Component {
  render() {
    return (
      <div className="pluginWrapper buttonWrapper">
        <a className="button" href={this.props.href} target={this.props.target}>{this.props.children}</a>
      </div>
    );
  }
}

Button.defaultProps = {
  target: '_self'
};

class Index extends React.Component {
  render() {
    return (
      <div>
        <div className="homeContainer">
          <div className="homeSplashFade">
            <div className="wrapper homeWrapper">
              <div className="projectLogo">
                <img src={siteConfig.baseUrl + 'img/logo.svg'}/>
              </div>
              <div className="inner">
                <h2 className="projectTitle">
                  {siteConfig.title}
                  <small>{siteConfig.tagline}</small>
                </h2>
                <div className="section promoSection">
                  <div className="promoRow">
                    <div className="pluginRowBlock">
                      <Button href="/docs/overview.html">Overview</Button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

module.exports = Index;
