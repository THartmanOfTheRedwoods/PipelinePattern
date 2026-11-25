# The Pipeline Pattern — for fun and awesomeness

I wanted to share a little about my favourite design pattern — I literally can not get enough of it.

Think of the ‘Pipeline Pattern’ like a conveyor belt or assembly line that takes an object, modifies it and passes it onto the next class. We do this all the time in engineering. Consider an order transaction in an eCommerce site.

- User places the order
- Payment processor takes the payment
- Invoice is generated and sent to the user
- The order is sent to your ERP system
- The order is packed and shipped
- Customer receives a thank-you email.

While this may be more useful with some kind of state-machine, this clearly shows the concept of a pipe. There is one primary constant in all of these steps — the order. This order Array/Class/Object/TransferObject etc is passed to each phase of the process until completed.

If you’ve created something similar in the past, it’s a pretty good bet that there’s something ugly like this going on in a file called `Order.php`.

```php
if ($order->getStatus() === 'success') {
    $this->getErpAdapter()->sendOrder($order);
}
```

To figure out the whole process, I’d have to go through EVERY line of code to figure out what’s going on! Ain’t no one got time for that!

Here’s one of my favourite quotes from Martin Fowler.

> Any fool can write code that a computer can understand. Good programmers write code that humans can understand.

Now, consider this.

```php
$pipeline = (new Pipeline)
    ->pipe(new createOrder)
    ->pipe(new processPayment)
    ->pipe(new sendInvoice)
    ->pipe(new exportOrder);

$pipeline->process($order);
```

Can someone say ‘readability’, please? This is also a huge win for testability and single responsibility — what this means is that each piece of code only does one or two things that it was designed to do, then passes the torch to the next class. To make testing easier, each of these stages can be easily mocked — or even the whole pipeline!

## Definition of the Pipeline Pattern

If you’re still not sure about the definition, here’s a better description from some great algorithm design documentation.

> Each of the sequence of calculations is performed by having the first stage of the pipeline perform the first step, and then the second stage the second step, and so on. As each stage completes a step of a calculation, it passes the calculation-in-progress to the next stage and begins work on the next calculation.

## Implementation in Java

Since I’m a PHP developer (well, mostly!), I’m doing my examples in that — BUT, you can easily apply this pattern to any language. There are plenty of libraries out there that implement this pattern, but my favourite is **PHP League Pipeline**. It’s super simple and FAST — go and check out their site. If you’re not already a fan of the PHP League, please go and check it out immediately, their packages are first rate. Their packages are rock-solid, backed by incredible code coverage/testing and have awesome documentation. See that? Builds passing, code quality and coverage.. it’s that good.

From the authors:

> “This package provides a plug and play implementation of the Pipeline Pattern. It’s an architectural pattern which encapsulates sequential processes. When used, it allows you to mix and match operation, and pipelines, to create new execution chains. The pipeline pattern is often compared to a production line, where each stage performs a certain operation on a given payload/subject. Stages can act on, manipulate, decorate, or even replace the payload. If you find yourself passing results from one function to another to complete a series of tasks on a given subject, you might want to convert it into a pipeline.”

So.. how do we use it?

### Maven install

To use the Java replacement library (`PipelinePattern`), you would typically add it as a dependency from its source repository. The library is available at [https://github.com/THartmanOfTheRedwoods/PipelinePattern](https://github.com/THartmanOfTheRedwoods/PipelinePattern). You can clone the repository or add it as a module in your project.

Great.. we’re ready to go!

### Assemble your class

We’re going to need a few pieces, so let me first show you how it all goes together, then we’ll look at the individual class requirements. Here’s a new class which we’ll call `RunAllTheThings` — all this class will do is when we call the `doIt` method, we will execute the pipeline and return the result.

```java
import pipes.Pipeline;
import pipes.PipelineInterface;
import pipes.payloads.Payload;

public class RunAllTheThings {
    public Payload doIt() {
        PipelineInterface pipeline = new Pipeline()
                .pipe(new DoStage1())
                .pipe(new DoStage2())
                .pipe(new DoStage3());

        // The payload is an object that's passed between stages
        Payload payload = new Payload(String.class, "");

        // Run the pipeline
        pipeline.process(payload);

        return payload;
    }
}
```

Simple, eh? Let’s have a look at some of the sub classes.

### Payload

The payload is something I really like to add in here as it keeps the data being passed though the pipe clean and readable. In this example, payload is just a simple entity with a protected property and some getters and setters. It gives us a clean way to not only update the results, but an expected format at the end. Because the `doIt` method returns this object, we know that we can do something like `result.getResult(String.class)` to get what we need. This payload can be as simple of as complex as you need.

The `PipelinePattern` library provides a generic `Payload` class that wraps a result and ensures type safety.

```java
package pipes.payloads;

public class Payload {
    private Object result = null;

    public <T> Payload(Class<T> desiredType, Object result) {
        this.setResult(desiredType, result);
    }

    public <T> T getResult(Class<T> desiredType) {
        if (this.result.getClass().isAssignableFrom(desiredType)) {
            return desiredType.cast(this.result);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public <T> void setResult(Class<T> desiredType, Object result) {
        if (result.getClass().isAssignableFrom(desiredType)) {
            this.result = desiredType.cast(result);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
```

### Stage 1

Stages need to be **callable** — this means that they need to either be a closure, a callback or have an `__invoke` method. In Java with the `PipelinePattern` library, stages implement the `StageInterface`, which requires an `apply(Payload)` method. We’re going to use that for this example.

```java
import pipes.payloads.Payload;
import pipes.stages.StageInterface;
import pipes.stages.exceptions.StageException;

public class DoStage1 implements StageInterface {
    @Override
    public Payload apply(Payload payload) throws StageException {
        String current = payload.getResult(String.class);
        payload.setResult(String.class, current + "all");
        return payload;
    }
}
```

In this stage, we’re adding the word `all` to the result string. Once we’ve modified the result, you simply return the payload and the next stage will run.

### Stage 2

You may start to see the pattern emerging here. This stage takes the payload, adds the word `the` and continues.

```java
import pipes.payloads.Payload;
import pipes.stages.StageInterface;
import pipes.stages.exceptions.StageException;

public class DoStage2 implements StageInterface {
    @Override
    public Payload apply(Payload payload) throws StageException {
        String current = payload.getResult(String.class);
        payload.setResult(String.class, current + "the");
        return payload;
    }
}
```

### Stage 3

Guess what this stage does? Yep, we add the word `things` to the result.

```java
import pipes.payloads.Payload;
import pipes.stages.StageInterface;
import pipes.stages.exceptions.StageException;

public class DoStage3 implements StageInterface {
    @Override
    public Payload apply(Payload payload) throws StageException {
        String current = payload.getResult(String.class);
        payload.setResult(String.class, current + "things");
        return payload;
    }
}
```

### The Result

Right, so let’s run our code.

```java
RunAllTheThings runner = new RunAllTheThings();
Payload result = runner.doIt();
System.out.println(result.getResult(String.class));
```

This will print the following string to the screen.

```
allthethings
```

## Short-circuiting

Sometimes you simply don’t want to continue processing. If an order is invalid why would you attempt to capture payment? How do we tackle that? The simplest way is with a try/catch. Now just throw a `StageException` in one of your stages.

```java
try {
    pipeline.process(payload);
} catch (StageException e) {
    // Do something else!
}
```

The `PipelinePattern` library defines `StageException`, which stages can throw to interrupt the pipeline.

## Dynamic Pipelines

There’s another useful aspect of the `PipelinePattern` library that I love, which is the `PipelineBuilder` — This allows you to add logic as to whether to add a stage to the pipeline.

```java
import pipes.builder.PipelineBuilder;
import pipes.builder.PipelineBuilderInterface;
import pipes.PipelineInterface;
import pipes.processors.FingersCrossedProcessor;

// Instantiate the PipelineBuilder
PipelineBuilderInterface builder = new PipelineBuilder()
        .add(new CreateOrder());

// Conditional stage
if (order.getOrigin().equals("New Zealand")) {
    builder.add(new PreBookCarrier());
}

// Continue adding more stages
builder.add(new ProcessPayment())
       .add(new SendInvoice())
       .add(new ExportOrder());

// Assemble to pipeline
PipelineInterface pipeline = builder.build(new FingersCrossedProcessor());

// Process
pipeline.process(order);
```

The `PipelineBuilder` accumulates stages and then builds a `Pipeline` with a chosen processor.

## Reusing Pipes

It may be useful sometimes to reuse a pipe inside another pipe! Easy enough since the `pipe` method can accept another `StageInterface`, and `Pipeline` itself implements `StageInterface`.

```java
PipelineInterface createOrderPipe = new Pipeline()
        .pipe(new CreateOrder())
        .pipe(new GenerateInvoice());

PipelineInterface mainPipeline = new Pipeline()
        .pipe(createOrderPipe) // Pipe re-use!
        .pipe(new ProcessPayment())
        .pipe(new SendInvoice())
        .pipe(new ExportOrder());

mainPipeline.process(order);
```

## Conclusion

While the pipeline pattern isn’t for every occasion, there is a LOT of ugly code out there that could benefit from the simplicity and readability this pattern provides. Next time you find yourself building a multi-stage piece of code try the pattern out and see how it goes.
