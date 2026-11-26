(async () => {
  const entry = document.querySelector('input[placeholder]');
  const labels = () => [...document.querySelectorAll('#app ul label')];
  const checkboxes = () => [...document.querySelectorAll('#app li input[type=checkbox]')];
  async function until(test) {
    const deadline = performance.now() + 2000;
    while (!test()) {
      if (performance.now() > deadline) throw Error('Timed out waiting for TodoMVC update');
      await new Promise(resolve => setTimeout(resolve, 0));
    }
  }
  const start = performance.now();
  for (let i = 0; i < 50; i++) {
    Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set.call(entry, 'Row ' + i);
    entry.dispatchEvent(new Event('input', {bubbles:true}));
    entry.dispatchEvent(new KeyboardEvent('keydown', {keyCode:13, which:13, bubbles:true}));
    await until(() => entry.value === '');
  }
  const inserted = performance.now();
  const first = labels()[0];
  const boxes = checkboxes();
  for (let i = 0; i < boxes.length; i += 2) boxes[i].click();
  await until(() => document.querySelector('#app article footer > span')?.textContent === '25 items left');
  const toggled = performance.now();
  for (let i = 0; i < 1000; i++) {
    const key = new KeyboardEvent('keydown', {key:'ArrowLeft', keyCode:37, which:37, bubbles:true, cancelable:true});
    if (!entry.dispatchEvent(key)) throw Error('Navigation key was unexpectedly cancelled');
  }
  const dispatched = performance.now();
  return {titles:labels().map(label => label.textContent), completed:checkboxes().filter(box => box.checked).length,
    retained:first === labels()[0], entry:entry.value, timings:{
      loadMs:performance.getEntriesByType('navigation')[0].loadEventEnd,
      add50Ms:inserted - start, toggle25Ms:toggled - inserted, keydown1000Ms:dispatched - toggled}};
})()
