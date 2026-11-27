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
