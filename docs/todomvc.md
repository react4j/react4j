---
title: TodoMVC
---

![TodoMVC](/img/todomvc.svg)

The [TodoMVC](http://todomvc.com/) project specified a standard project that aims to help you select
frontend framework. The functionality is fixed as is the look and feel so that it easier to compare
and contrast the different implementations of the application using different approaches.

The React4j project has implemented several variants of the TodoMVC application using different
combinations of features. The goal is to give you an idea about what an application built using
React4j looks like.

<table width="100%">
  <caption>TodoMVC variants</caption>
  <thead>
  <tr>
    <th class="rotate"><div><span>Branch</span></div></th>
    <th class="rotate" style="width: 90px"><div><span>Status</span></div></th>
    <th class="rotate"><div><span>Production Build</span></div></th>
    <th class="rotate"><div><span>Development Build</span></div></th>
    <th class="rotate"><div><span>Features</span></div></th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td><a href="https://github.com/react4j/react4j-todomvc/tree/raw">raw</a></td>
    <td>
      <a href="https://travis-ci.org/react4j/react4j-todomvc">
        <img src="https://secure.travis-ci.org/react4j/react4j-todomvc.png?branch=raw" width="90px" height="20px" alt="Build Status" style="max-width:100%;">
      </a>
    </td>
    <td><a href="/todomvc/raw/todomvc"><img src="/img/external-link-symbol.svg" width=20px" /></a></td>
    <td><a href="/todomvc/raw/todomvc_dev"><img src="/img/external-link-symbol.svg" width=20px" /></a></td>
    <td>
      <ul>
        <li>Build System: <a href="https://buildr.apache.org">Buildr</a></li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><a href="https://github.com/react4j/react4j-todomvc/tree/arez">arez</a></td>
    <td>
      <a href="https://travis-ci.org/react4j/react4j-todomvc">
        <img src="https://secure.travis-ci.org/react4j/react4j-todomvc.png?branch=arez" width="90px" height="20px" alt="Build Status" style="max-width:100%;">
      </a>
    </td>
    <td><a href="/todomvc/arez/todomvc"><img src="/img/external-link-symbol.svg" width=20px" /></a></td>
    <td><a href="/todomvc/arez/todomvc_dev"><img src="/img/external-link-symbol.svg" width=20px" /></a></td>
    <td>
      <ul>
        <li>State Management: <a href="https://arez.github.io">Arez</a></li>
        <li>Build System: <a href="https://buildr.apache.org">Buildr</a></li>
      </ul>
    </td>
  </tr>
  <tr>
      <td><a href="https://github.com/react4j/react4j-todomvc/tree/sting">sting</a></td>
      <td>
        <a href="https://travis-ci.org/react4j/react4j-todomvc">
          <img src="https://secure.travis-ci.org/react4j/react4j-todomvc.png?branch=sting" width="90px" height="20px" alt="Build Status" style="max-width:100%;">
        </a>
      </td>
      <td><a href="/todomvc/sting/todomvc"><img src="/img/external-link-symbol.svg" width=20px" /></a></td>
      <td><a href="/todomvc/sting/todomvc_dev"><img src="/img/external-link-symbol.svg" width=20px" /></a></td>
      <td>
        <ul>
          <li>Dependency Injection: <a href="https://sting-ioc.github.io/">Sting</a></li>
          <li>State Management: <a href="https://arez.github.io">Arez</a></li>
          <li>Build System: <a href="https://buildr.apache.org">Buildr</a></li>
        </ul>
      </td>
    </tr>
    <tr>
    <td><a href="https://github.com/react4j/react4j-todomvc/tree/dagger">dagger</a></td>
    <td>
      <a href="https://travis-ci.org/react4j/react4j-todomvc">
        <img src="https://secure.travis-ci.org/react4j/react4j-todomvc.png?branch=dagger" width="90px" height="20px" alt="Build Status" style="max-width:100%;">
      </a>
    </td>
    <td><a href="/todomvc/dagger/todomvc"><img src="/img/external-link-symbol.svg" width=20px" /></a></td>
    <td><a href="/todomvc/dagger/todomvc_dev"><img src="/img/external-link-symbol.svg" width=20px" /></a></td>
    <td>
      <ul>
        <li>Dependency Injection: <a href="https://google.github.io/dagger">Dagger2</a></li>
        <li>State Management: <a href="https://arez.github.io">Arez</a></li>
        <li>Build System: <a href="https://buildr.apache.org">Buildr</a></li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><a href="https://github.com/react4j/react4j-todomvc/tree/sting_maven">sting_maven</a></td>
    <td>
      <a href="https://travis-ci.org/react4j/react4j-todomvc">
        <img src="https://secure.travis-ci.org/react4j/react4j-todomvc.png?branch=sting_maven" width="90px" height="20px" alt="Build Status" style="max-width:100%;">
      </a>
    </td>
    <td></td>
    <td></td>
    <td>
      <ul>
        <li>Dependency Injection: <a href="https://sting-ioc.github.io/">Sting</a></li>
        <li>State Management: <a href="https://arez.github.io">Arez</a></li>
        <li>Build System: <a href="https://maven.apache.org">Maven</a></li>
      </ul>
    </td>
  </tr>
  </tbody>
</table>
