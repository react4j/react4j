IsolatedTimingTest.captureTimeline = function(exercise) {
  const previous = Object.getOwnPropertyDescriptor(globalThis, 'performance');
  const marks = new Set();
  const stacks = new Map();
  const measures = new Set();
  const invalid = [];
  const labels = [];
  const completed = () => {
    if (marks.size || measures.size || stacks.size || invalid.length) {
      throw new Error('User Timing did not clean up: ' +
        [...marks, ...measures, ...stacks.keys(), ...invalid].join(', '));
    }
  };
  const owner = name => name.slice(name.lastIndexOf(' [root '));
  const recorder = {
    mark(name) {
      if (marks.has(name)) invalid.push('Duplicate mark: ' + name);
      marks.add(name);
      const root = owner(name);
      if (!stacks.has(root)) stacks.set(root, []);
      stacks.get(root).push(name);
    },
    measure(label, mark) {
      if (!marks.has(mark)) invalid.push('Missing mark: ' + mark);
      const root = owner(mark);
      const stack = stacks.get(root);
      if (!stack || stack.pop() !== mark) invalid.push('Wrong measurement nesting: ' + mark);
      if (stack && !stack.length) stacks.delete(root);
      labels.push(label);
      measures.add(label);
    },
    clearMarks(name) {
      marks.delete(name);
    },
    clearMeasures(label) {
      measures.delete(label);
    },
  };
  Object.defineProperty(globalThis, 'performance', {configurable: true, value: recorder});
  try {
    exercise({
      labels: () => labels.slice(),
      completed,
      reset() {
        completed();
        labels.length = 0;
      },
    });
  } finally {
    if (previous) Object.defineProperty(globalThis, 'performance', previous);
    else delete globalThis.performance;
  }
};
