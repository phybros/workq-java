# workq-java

Java client library for the [workq job server](https://github.com/iamduo/workq). Currently under development and not feature-complete.

# Building

This project uses maven. To build the jar and run tests, use

```
mvn clean package
```
> **Note**: to run the tests you need a workq server running on localhost:9922. To skip the tests altogether and just build the jar, append `-Dmaven.test.skip` to the command line

# Using workq-java

Once you have included the jar in your project, you can create a connection and add a new background job like so:

```java
WorkqClient client = new WorkqClient("localhost", 9922);

BackgroundJob job = new BackgroundJob();
job.setName("JobName");
job.setId(UUID.randomUUID());
job.setTtl(3600000);
job.setPayload("Payload".getBytes());
job.setPriority(100);
job.setTtr(1000);
job.setMaxAttempts(2);

client.add(job);
```

In the future, you will be able to include `workq-java` as a dependency from Maven Central, but not until it is feature complete (1.0).

# License

MIT License

Copyright (c) 2016 Will Warren

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
