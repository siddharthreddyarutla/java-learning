# spacing


| Scenario / Rule                                    | Spacing Requirement                          | ✅ Correct Example                                 | ❌ Wrong Example                                   |
| -------------------------------------------------- | -------------------------------------------- | ------------------------------------------------- | ------------------------------------------------- |
| **Between logical blocks**                         | ✅ One blank line                             | `fetchData();`<br><br>`processData();`            | `fetchData(); processData();`                     |
| **Between unrelated operations**                   | ✅ Add a blank line when shifting context     | `list.add(item);`<br><br>`persist(list);`         | `list.add(item); persist(list);`                  |
| **Inside tightly related code (same logic)**       | ❌ No blank line                              | `list.add(item);`<br>`list.size();`               | `list.add(item);`<br><br>`list.size();`           |
| **After variable declarations**                    | ✅ One blank line                             | `int size = list.size();`<br><br>`process(size);` | `int size = list.size(); process(size);`          |
| **Before comments**                                | ✅ One blank line                             | `saveRecord();`<br><br>`// post-save trigger`     | `saveRecord(); // post-save trigger`              |
| **Inside `if / else / try / catch / loop` blocks** | ❌ No blank line at the start or end of block | `if (valid) {`<br>`    process();`<br>`}`         | `if (valid) {`<br><br>`    process();`<br><br>`}` |
| **After closing brace of control structure**       | ✅ One blank line                             | `}`<br><br>`nextOperation();`                     | `} nextOperation();`                              |
| **Method parameters (long lists)**                 | ✅ Each parameter on separate line            | ```java                                           |                                                   |
| method(                                            |                                              |                                                   |                                                   |
