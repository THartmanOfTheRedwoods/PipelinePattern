# The Pipeline Design Pattern

A plug and play, **immutable**, **type-safe** implementation of the Pipeline Pattern for Java,
modelled piece for piece on [League\Pipeline](https://github.com/thephpleague/pipeline) for PHP.

* [Design Pattern Notes](https://thartmanoftheredwoods.github.io/CIS-18/pipeline.html)
* [The Pipeline Pattern — for fun and awesomeness](THE_PIPELINE_PATTERN.md) (article)
* [Example Pipeline Usages](tests/PipelineExClient.java)

## Pipeline Pattern

The pipeline pattern allows you to easily compose sequential stages by chaining stages. A
pipeline consists of zero, one, or multiple stages. When a pipeline processes a payload, the
payload is passed to the first stage, and from then on the result of each stage is passed on to
the next. In its simplest form the execution chain is a loop:

```java
T result = payload;
for (Function<? super T, ? extends T> stage : stages) {
    result = stage.apply(result);
}
return result;
```

which is the same as `stage3.apply(stage2.apply(stage1.apply(payload)))`.

## Requirements and installation

Java 17 or newer, no runtime dependencies.

```bash
mvn install
```

```xml
<dependency>
    <groupId>io.github.thartmanoftheredwoods</groupId>
    <artifactId>pipeline-pattern</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or simply copy `src/pipes` into your project.

## Usage

A stage is anything that can act as a `Function<T, T>`: a class implementing
`StageInterface<T>`, a lambda, a method reference or another pipeline.

```java
PipelineInterface<Integer> pipeline = new Pipeline<Integer>()
        .pipe(payload -> payload * 10);
```

### Class based stages

```java
public class TimesTwoStage implements StageInterface<Integer> {
    @Override
    public Integer apply(Integer payload) {
        return payload * 2;
    }
}

public class AddOneStage implements StageInterface<Integer> {
    @Override
    public Integer apply(Integer payload) {
        return payload + 1;
    }
}

PipelineInterface<Integer> pipeline = new Pipeline<Integer>()
        .pipe(new TimesTwoStage())
        .pipe(new AddOneStage());

pipeline.process(10); // 21
```

### Immutability

`pipe` never modifies a pipeline; it returns a **new** pipeline with the extra stage. Pipelines
can therefore be shared and reused freely (and are thread-safe as long as their stages are).

```java
PipelineInterface<Integer> timesTwo = new Pipeline<Integer>().pipe(new TimesTwoStage());
PipelineInterface<Integer> timesTwoPlusOne = timesTwo.pipe(new AddOneStage());

timesTwo.process(10);        // 20
timesTwoPlusOne.process(10); // 21
```

### Re-usable pipelines

`PipelineInterface<T>` extends `StageInterface<T>`, so a pipeline can be used as a stage:

```java
PipelineInterface<Order> createOrderPipe = new Pipeline<Order>()
        .pipe(new CreateOrder())
        .pipe(new GenerateInvoice());

PipelineInterface<Order> pipeline = new Pipeline<Order>()
        .pipe(createOrderPipe)
        .pipe(new ProcessPayment());
```

### Pipeline builders

Because pipelines are immutable, a (mutable) builder collects stages so a pipeline can be
composed step by step, e.g. conditionally, and built at any time. Each `build` is a snapshot.

```java
PipelineBuilderInterface<Order> builder = new PipelineBuilder<Order>()
        .add(new CreateOrder());

if (order.getOrigin().equals("New Zealand")) {
    builder.add(new PreBookCarrier());
}

PipelineInterface<Order> pipeline = builder.add(new ProcessPayment()).build();
```

### Processors

A processor decides how the stages are invoked. Pass one to the `Pipeline` constructor or to
`build(processor)`; `null` (or no processor) means `FingersCrossedProcessor`.

* `FingersCrossedProcessor<T>` (default) runs every stage.
* `InterruptibleProcessor<T>(Predicate<? super T> check)` stops as soon as `check` returns
  `false` after a stage.

```java
new Pipeline<Integer>(new InterruptibleProcessor<>(p -> p < 3), p -> p * 2, p -> p + 1)
        .process(2); // 4: the second stage never runs
```

Implement `ProcessorInterface<T>` to create your own strategy.

### Exception handling

The library is completely transparent with exceptions: it never catches or silences them. Any
`RuntimeException` thrown by a stage stops the pipeline and reaches the caller of `process`.
`StageException` is provided as a convenient, unchecked type to throw.

```java
try {
    pipeline.process(order);
} catch (StageException e) {
    // Handle the exception.
}
```

## League\Pipeline compatibility

| League\Pipeline (PHP)                              | PipelinePattern (Java)                                  |
|----------------------------------------------------|---------------------------------------------------------|
| `StageInterface::__invoke($payload)`               | `StageInterface<T>.apply(T)` (a `UnaryOperator<T>`)      |
| any `callable` stage                               | any `Function<? super T, ? extends T>`                  |
| `PipelineInterface extends StageInterface`         | `PipelineInterface<T> extends StageInterface<T>`        |
| `pipe()` clones (immutable)                        | `pipe()` returns a new pipeline (immutable)             |
| `new Pipeline(?ProcessorInterface, callable ...)`  | `new Pipeline<T>(ProcessorInterface<T> or null, stages...)` |
| `process($payload)` / `$pipeline($payload)`        | `process(payload)` / `apply(payload)`                   |
| `PipelineBuilder::add()` / `build(?processor)`     | `PipelineBuilder<T>.add()` / `build()` / `build(processor)` |
| `FingersCrossedProcessor`, `InterruptibleProcessor`| `FingersCrossedProcessor<T>`, `InterruptibleProcessor<T>` |
| exceptions are never caught                        | exceptions are never caught (+ optional `StageException`) |

Java differences by design: the payload type is checked at compile time through generics, and
a stage maps a `T` to a `T` (use a common payload type, such as your own `Payload` or `Order`
class, when stages need to carry different pieces of data).

## Project layout, tests and examples

| Path                     | Contents                                                        |
|--------------------------|-----------------------------------------------------------------|
| `src/pipes`              | the library                                                     |
| `tests/pipes`            | JUnit tests, including ports of League\Pipeline's own tests     |
| `tests/PipelineExClient.java`, `tests/stages`, `tests/ecomm` | runnable example client and its e-commerce stages |
| `tests/article`          | every code example from `THE_PIPELINE_PATTERN.md`, compiled and run; `ArticleSnippetsTest` fails if the article and the code drift apart |

Run the tests:

```bash
mvn test
```

Run the example client:

```bash
mvn -q test-compile && java -cp target/classes:target/test-classes PipelineExClient
```

## Upgrading from the earlier version

* `Pipeline`, `PipelineInterface`, `StageInterface`, `ProcessorInterface`, the processors and
  the builder are now generic: `new Pipeline<Order>()`, `StageInterface<Order>`, ...
* `pipes.payloads.Payload` was removed. Stages receive and return the payload type directly
  (`Order apply(Order order)`), so the `Class` token wrapper is no longer needed.
* `pipe()` no longer mutates the pipeline; use its return value.
* `PipelineBuilder.build()` no longer requires a processor.
