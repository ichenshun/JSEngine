### 这个项目，是为了测试在使用AI辅助编程的条件下，手撸一个JSEngine需要多长时间？

### 使用了哪些AI工具？
- ChatGpt3.5：可以免费使用
- Bing Chat：这个可以免费蹭到ChatGpt4版本
- CodeGeex：可以在IDEA中免费使用的AI自动补全插件，这个插件是Bing Chat推荐给我的

### 为什么用Kotlin？
因为我用了很多年的Java，Java的板正的冗长语法，让我感觉快要窒息，我喜欢Kotlin带给我的自由感觉。

### JavaScript的语法定义（这是受到ChatGpt给我生成的代码的启发，通过Google搜索到的）
https://github.com/antlr/grammars-v4/blob/master/javascript/ecmascript/JavaScript/ECMAScript.g4

### 目前进展
实现了基本的词法分析和语法分析，构建了基本的AST。还在完善中

### 后续计划
实现执行器，可以直接执行AST

### 再后续计划
看看是否要将AST翻译成中间字节码，执行器执行中间字节码。
