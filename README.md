Shimo
=====

Shimo is a `JsonAdapter.Factory` for [Moshi][moshi] which randomizes the order of keys when
serializing objects to JSON and when deserializing objects from JSON.

 [moshi]: https://github.com/square/moshi

For example, given the following type:
```java
final class Simple {
  final String one;
  final String two;

  Simple(String one, String two) {
    this.one = one;
    this.two = two;
  }
}
```
When serializing an instance, `new Simple("one", "two")`, the JSON might be serialized normally
```json
{"one":"one","two":"two"}
``` 
or the keys might be reversed
```json
{"two":"two","one":"one"}
```

Similarly, when deserializing `{"one":"one","two":"two"}`, the adapter for `Simple` might see
"one" and then "two" or it might see "two" and then "one".

By randomizing the keys in both directions, you ensure that your servers do not rely on order and
that your adapters do not rely on order.

Shimo should only be installed on non-release builds because it dramatically reduces the performance
of both serialization and deserialization.

Install the Shimo randomizer first before any custom adapters.

```java
Moshi moshi = new Moshi.Builder()
    .add(ObjectOrderRandomizer.create())
    // other adapters here
    .build();
```

If you want deterministic randomness, you can pass in a `Random` instance.


Download
--------

Download [the latest JAR][dl] or depend via Gradle:

```kotlin
implementation("com.jakewharton.moshi:shimo:0.1.1")
```
or Maven:
```xml
<dependency>
  <groupId>com.jakewharton.moshi</groupId>
  <artifactId>shimo</artifactId>
  <version>0.1.1</version>
</dependency>
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

 [dl]: https://search.maven.org/classic/remote_content?g=com.jakewharton.moshi&a=shimo&v=LATEST
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/


License
=======

    Copyright 2020 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
