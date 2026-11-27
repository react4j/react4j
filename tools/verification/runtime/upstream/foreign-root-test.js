'use strict';
it('routes foreign unmount to the owning runtime and clears ownership', () => {
  jest.resetModules();
  const React = require('react');
  const owner = require('react-dom');
  let unmounts = 0;
  class Child extends React.Component {
    componentWillUnmount() { unmounts++; }
    render() { return <span>owned</span>; }
  }
  const container = document.createElement('div');
  const mount = () => owner.render(<Child />, container);
  jest.resetModules();
  const caller = require('react-dom');
  expect(caller).not.toBe(owner);
  const foreignUnmount = () => caller.unmountComponentAtNode(container);
  const ownerUnmount = () => owner.unmountComponentAtNode(container);
  const warnedUnmount = () => {
    let result;
    expect(() => { result = foreignUnmount(); }).toWarnDev(
      "Warning: unmountComponentAtNode(): The node you're attempting to unmount was rendered by another copy of React.",
      {withoutStack: true},
    );
    return result;
  };
  if (owner.verifyForeignRootOwnership) {
    owner.verifyForeignRootOwnership(mount, warnedUnmount, foreignUnmount, ownerUnmount,
      () => container.firstChild === null, () => unmounts);
    return;
  }
  mount();
  expect(warnedUnmount()).toBe(true);
  expect(container.firstChild).toBe(null);
  expect(unmounts).toBe(1);
  expect(caller.unmountComponentAtNode(container)).toBe(false);
  expect(owner.unmountComponentAtNode(container)).toBe(false);
  mount();
  expect(owner.unmountComponentAtNode(container)).toBe(true);
  expect(container.firstChild).toBe(null);
  expect(unmounts).toBe(2);
  expect(caller.unmountComponentAtNode(container)).toBe(false);
});

it('attaches host refs to the same owner across two renderer copies', () => {
  jest.resetModules();
  const React = require('react');
  const owner = require('react-dom');
  jest.resetModules();
  const caller = require('react-dom');
  expect(caller).not.toBe(owner);
  const local = document.createElement('div');
  const remote = document.createElement('div');

  class Indirection extends React.Component {
    componentDidUpdate() {
      caller.render(this.props.remote, remote);
    }
    render() {
      return this.props.local;
    }
  }
  class Parent extends React.Component {
    render() {
      return <Indirection local={<div ref="local" />} remote={<div ref="remote" />} />;
    }
  }

  let instance;
  const mount = () => { instance = owner.render(<Parent />, local); };
  const update = () => owner.render(<Parent />, local);
  const localRef = () => instance.refs.local;
  const remoteRef = () => instance.refs.remote;
  try {
    if (owner.verifyForeignRootRefs) {
      owner.verifyForeignRootRefs(mount, update,
        () => localRef().tagName === 'DIV' && localRef() === local.firstChild,
        () => remoteRef().tagName === 'DIV' && remoteRef() === remote.firstChild,
        () => remoteRef() === undefined);
    } else {
      mount();
      expect(localRef().tagName).toBe('DIV');
      expect(localRef()).toBe(local.firstChild);
      expect(remoteRef()).toBeUndefined();
      update();
      expect(localRef().tagName).toBe('DIV');
      expect(localRef()).toBe(local.firstChild);
      expect(remoteRef().tagName).toBe('DIV');
      expect(remoteRef()).toBe(remote.firstChild);
    }
  } finally {
    caller.unmountComponentAtNode(remote);
    owner.unmountComponentAtNode(local);
  }
});
