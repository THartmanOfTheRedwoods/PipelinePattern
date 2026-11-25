# The Pipeline Pattern — for fun and awesomeness

I wanted to share a little about my favourite design pattern — I literally can not get enough of it.

Think of the ‘Pipeline Pattern’ like a conveyor belt or assembly line that takes an object, modifies it and passes it onto the next class. We do this all the time in engineering. Consider an order transaction in an eCommerce site.

- User places the order
- Payment processor takes the payment
- Invoice is generated and sent to the user
- The order is sent to your ERP system
- The order is packed and shipped
- Customer receives a thank-you email.

While this may be more useful with some kind of state-machine, this clearly shows the concept of a pipe. There is one primary constant in all of these steps — the order. This order Class/Object/Record/TransferObject etc is passed to each phase of the process until completed.

If you’ve created something similar in the past, it’s a pretty good bet that there’s something ugly like this going on in a file called `OrderService.java`.

```java
if (order.getStatus().equals("success")) {
    this.getErpAdapter().sendOrder(order);
}
```

To figure out the whole process, I’d have to go through EVERY line of code to figure out what’s going on! Ain’t no one got time for that!

Here’s one of my favourite quotes from Martin Fowler.

> Any fool can write code that a computer can understand. Good programmers write code that humans can understand.

Now, consider this.

```java
PipelineInterface<Order> pipeline = new Pipeline<Order>()
        .pipe(new CreateOrder())
        .pipe(new ProcessPayment())
        .pipe(new SendInvoice())
        .pipe(new ExportOrder());

pipeline.process(order);
```

Can someone say ‘readability’, please? This is also a huge win for testability and single responsibility — what this means is that each piece of code only does one or two things that it was designed to do, then passes the torch to the next class. To make testing easier, each of these stages can be easily mocked — or even the whole pipeline!

## Definition of the Pipeline Pattern

If you’re still not sure about the definition, here’s a better description from some great algorithm design documentation.

> Each of the sequence of calculations is performed by having the first stage of the pipeline perform the first step, and then the second stage the second step, and so on. As each stage completes a step of a calculation, it passes the calculation-in-progress to the next stage and begins work on the next calculation.

## Implementation in Java

I first fell in love with this pattern in PHP, and you can easily apply it to any language. There are plenty of libraries out there that implement it, but my favourite has always been **PHP League Pipeline**. It’s super simple and FAST. If you’re not already a fan of the PHP League, please go and check it out immediately: their packages are rock-solid, backed by incredible code coverage/testing and have awesome documentation.

From the authors:

> “This package provides a plug and play implementation of the Pipeline Pattern. It’s an architectural pattern which encapsulates sequential processes. When used, it allows you to mix and match operation, and pipelines, to create new execution chains. The pipeline pattern is often compared to a production line, where each stage performs a certain operation on a given payload/subject. Stages can act on, manipulate, decorate, or even replace the payload. If you find yourself passing results from one function to another to complete a series of tasks on a given subject, you might want to convert it into a pipeline.”

I wanted exactly that in Java, so I wrote **PipelinePattern**, a Java port that mirrors League Pipeline’s API piece for piece, and adds the one thing PHP can’t give you: compile-time type safety through generics. If you know one library, you know the other.

| League\Pipeline (PHP)                         | PipelinePattern (Java)                                         |
|-----------------------------------------------|----------------------------------------------------------------|
| `StageInterface::__invoke($payload)`          | `StageInterface<T>.apply(T payload)`                           |
| any `callable` is a stage                     | any `Function<? super T, ? extends T>` (class, lambda, method reference, pipeline) is a stage |
| `(new Pipeline)->pipe($stage)`                | `new Pipeline<T>().pipe(stage)`                                |
| `new Pipeline($processor, ...$stages)`        | `new Pipeline<T>(processor, stages...)`                        |
| `$pipeline->process($payload)` / `$pipeline($payload)` | `pipeline.process(payload)` / `pipeline.apply(payload)` |
| `(new PipelineBuilder)->add($stage)->build()` | `new PipelineBuilder<T>().add(stage).build()`                  |
| `FingersCrossedProcessor` (default)           | `FingersCrossedProcessor<T>` (default)                         |
| `InterruptibleProcessor($check)`              | `InterruptibleProcessor<T>(Predicate<? super T> check)`        |

Just like the League version, pipelines are **immutable** (every `pipe` returns a new pipeline), a `null` processor means “use the default `FingersCrossedProcessor`”, and the library never catches or silences your exceptions.

So.. how do we use it?

### Maven install

The library lives at [https://github.com/THartmanOfTheRedwoods/PipelinePattern](https://github.com/THartmanOfTheRedwoods/PipelinePattern) and needs Java 17 or newer. Clone it and install it into your local Maven repository:

```bash
git clone https://github.com/THartmanOfTheRedwoods/PipelinePattern.git
```

```bash
cd PipelinePattern
```

```bash
mvn install
```

Then add the dependency to your project’s `pom.xml`:

```xml
<dependency>
    <groupId>io.github.thartmanoftheredwoods</groupId>
    <artifactId>pipeline-pattern</artifactId>
    <version>1.0.0</version>
</dependency>
```

Not using Maven? The library has zero dependencies, so you can also just copy the `src/pipes` folder into your project.

Great.. we’re ready to go!

### Assemble your class

We’re going to need a few pieces, so let me first show you how it all goes together, then we’ll look at the individual class requirements. Here’s a new class which we’ll call `RunAllTheThings` — all this class will do is when we call the `doIt` method, we will execute the pipeline and return the result.

```java
import pipes.Pipeline;
import pipes.PipelineInterface;

public class RunAllTheThings {
    public Payload doIt() {
        PipelineInterface<Payload> pipeline = new Pipeline<Payload>()
                .pipe(new DoStage1())
                .pipe(new DoStage2())
                .pipe(new DoStage3());

        // The payload is an object that's passed between stages
        Payload payload = new Payload();

        // Run the pipeline and return the result
        return pipeline.process(payload);
    }
}
```

Simple, eh? The `<Payload>` type argument tells the compiler what flows through the pipe, so every stage is checked to accept and return a `Payload`. Let’s have a look at some of the sub classes.

### Payload

The payload is something I really like to add in here as it keeps the data being passed though the pipe clean and readable. In this example, payload is just a simple entity with a private property and some getters and setters. It gives us a clean way to not only update the results, but an expected format at the end. Because the `doIt` method returns this object, we know that we can do something like `result.getResult()` to get what we need. This payload can be as simple or as complex as you need.

The library itself doesn’t care what your payload is — a `Pipeline<T>` works with any type `T`: a `String`, an `Integer`, a record, your `Order` entity... This is ours:

```java
public class Payload {
    private String result = "";

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
```

### Stage 1

In PHP, stages need to be **callable** — a closure, a callback or a class with an `__invoke` method. The Java equivalent is anything that behaves like a `Function<T, T>`: a class that implements `StageInterface<T>` (whose single method is `apply`), a lambda, a method reference, or even another pipeline. We’re going to use class based stages for this example.

```java
import pipes.stages.StageInterface;

public class DoStage1 implements StageInterface<Payload> {
    @Override
    public Payload apply(Payload payload) {
        payload.setResult(payload.getResult() + "all");
        return payload;
    }
}
```

In this stage, we’re adding the word `all` to the result string. Once we’ve modified the result, you simply return the payload and the next stage will run. A stage can also return a completely different object of the same type — whatever it returns is what the next stage receives.

### Stage 2

You may start to see the pattern emerging here. This stage takes the payload, adds the word `the` and continues.

```java
import pipes.stages.StageInterface;

public class DoStage2 implements StageInterface<Payload> {
    @Override
    public Payload apply(Payload payload) {
        payload.setResult(payload.getResult() + "the");
        return payload;
    }
}
```

### Stage 3

Guess what this stage does? Yep, we add the word `things` to the result.

```java
import pipes.stages.StageInterface;

public class DoStage3 implements StageInterface<Payload> {
    @Override
    public Payload apply(Payload payload) {
        payload.setResult(payload.getResult() + "things");
        return payload;
    }
}
```

### The Result

Right, so let’s run our code.

```java
RunAllTheThings runner = new RunAllTheThings();
Payload result = runner.doIt();
System.out.println(result.getResult());
```

This will print the following string to the screen.

```text
allthethings
```

### Lambdas and method references

Because `StageInterface` is a functional interface, you don’t have to write a class for every little step. Lambdas and method references are stages too, and you can mix them freely with class based stages.

```java
PipelineInterface<Payload> pipeline = new Pipeline<Payload>()
        .pipe(new DoStage1())
        .pipe(payload -> {
            payload.setResult(payload.getResult() + "the");
            return payload;
        })
        .pipe(new DoStage3());
```

And if your payload is a simple value, the whole thing becomes a one-liner per stage:

```java
String result = new Pipeline<String>()
        .pipe(s -> s + "all")
        .pipe(s -> s + "the")
        .pipe(s -> s + "things")
        .pipe(String::toUpperCase)
        .process("");
```

That gives you `ALLTHETHINGS`.

## Immutability

Pipelines are immutable stage chains. When you `pipe` a new stage, a **new** pipeline is created with the added stage, and the original is left untouched. This makes pipelines easy to share and reuse, and minimizes side-effects.

```java
PipelineInterface<Integer> timesTwo = new Pipeline<Integer>()
        .pipe(p -> p * 2);
PipelineInterface<Integer> timesTwoPlusOne = timesTwo
        .pipe(p -> p + 1);

timesTwo.process(10);        // 20 - the original pipeline is unchanged
timesTwoPlusOne.process(10); // 21
```

One thing to watch out for: because `pipe` returns a new pipeline, calling `pipeline.pipe(stage);` on its own line and ignoring the result does nothing. Always chain the calls or keep the returned pipeline. If you need to add stages one at a time, that’s exactly what the `PipelineBuilder` below is for.

## Short-circuiting

Sometimes you simply don’t want to continue processing. If an order is invalid why would you attempt to capture payment? How do we tackle that? The simplest way is with a try/catch. Like League Pipeline, this library is completely transparent with exceptions: it never catches or silences them. So just throw an exception from one of your stages — the library ships with an unchecked `StageException` for exactly this purpose.

```java
import ecomm.Order;
import pipes.stages.StageInterface;
import pipes.stages.exceptions.StageException;

public class ProcessPayment implements StageInterface<Order> {

    @Override
    public Order apply(Order order) {
        System.out.printf("Stage %s: Processing payment for Order %d.%n", ProcessPayment.class.getName(), order.getOrderId());
        // Used to demo short-circuiting a pipeline.
        if (order.getOrderId() == 1002) {
            throw new StageException("Failed to process payment for Order " + order.getOrderId() + ".");
        }
        order.setPaid(true);
        return order;
    }
}
```

Then deal with it at the time the pipeline processes the payload.

```java
try {
    pipeline.process(order);
} catch (StageException e) {
    // Do something else!
}
```

If stopping early is part of the normal flow rather than an error, you can swap in the `InterruptibleProcessor` instead. It checks the payload after every stage and stops as soon as the check returns `false`, without throwing anything.

```java
PipelineInterface<Integer> pipeline = new Pipeline<Integer>(new InterruptibleProcessor<>(p -> p < 100))
        .pipe(p -> p * 2)
        .pipe(p -> p * 2)
        .pipe(p -> p + 1);

pipeline.process(32); // 128: the check fails after the second stage, so the third never runs
```

## Dynamic Pipelines

There’s another useful aspect of the library that I love, and it’s where the Builder Pattern comes in as a supporting pattern: the `PipelineBuilder`. Because pipelines themselves are immutable, the builder collects stages one at a time and lets you add logic as to whether to add a stage to the pipeline. When you’re ready, `build()` assembles the pipeline.

```java
// Instantiate the PipelineBuilder
PipelineBuilderInterface<Order> builder = new PipelineBuilder<Order>()
        .add(new CreateOrder());

// Conditional stage
if (order.getOrigin().equals("New Zealand")) {
    builder.add(new PreBookCarrier());
}

// Continue adding more stages
builder.add(new ProcessPayment())
       .add(new SendInvoice())
       .add(new ExportOrder());

// Assemble the pipeline
PipelineInterface<Order> pipeline = builder.build();

// Process
pipeline.process(order);
```

`build()` uses the default `FingersCrossedProcessor`; pass a processor, e.g. `builder.build(new InterruptibleProcessor<>(Order::isProcessed))`, to change how the stages run. Every call to `build` takes a snapshot of the stages added so far, so adding more stages to the builder later never changes a pipeline you have already built.

## Reusing Pipes

It may be useful sometimes to reuse a pipe inside another pipe! Easy enough: `PipelineInterface<T>` extends `StageInterface<T>`, so a pipeline *is* a stage and can be passed straight to `pipe`.

```java
PipelineInterface<Order> createOrderPipe = new Pipeline<Order>()
        .pipe(new CreateOrder())
        .pipe(new GenerateInvoice());

PipelineInterface<Order> mainPipeline = new Pipeline<Order>()
        .pipe(createOrderPipe) // Pipe re-use!
        .pipe(new ProcessPayment())
        .pipe(new SendInvoice())
        .pipe(new ExportOrder());

mainPipeline.process(order);
```

## Where everything lives

| Class                                                  | Package                     |
|--------------------------------------------------------|-----------------------------|
| `Pipeline`, `PipelineInterface`                        | `pipes`                     |
| `StageInterface`                                       | `pipes.stages`              |
| `StageException`                                       | `pipes.stages.exceptions`   |
| `PipelineBuilder`, `PipelineBuilderInterface`          | `pipes.builder`             |
| `FingersCrossedProcessor`, `InterruptibleProcessor`, `ProcessorInterface` | `pipes.processors` |

The complete `Order` entity and e-commerce stages (`CreateOrder`, `ProcessPayment`, `GenerateInvoice`, `SendInvoice`, `ExportOrder`, `PreBookCarrier`) used in this article are in the repository’s `tests/ecomm` and `tests/stages` folders, and every code example in this article is compiled and run by the project’s test suite.

## Conclusion

While the pipeline pattern isn’t for every occasion, there is a LOT of ugly code out there that could benefit from the simplicity and readability this pattern provides. Next time you find yourself building a multi-stage piece of code try the pattern out and see how it goes.
