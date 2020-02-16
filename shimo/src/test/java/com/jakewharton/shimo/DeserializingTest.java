package com.jakewharton.shimo;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class DeserializingTest {
  private final Random random = new Random(1234);

  @Test public void keysReordered() throws IOException {
    final List<String> keysSeen = new ArrayList<String>();
    Moshi moshi = new Moshi.Builder()
        .add(ObjectOrderRandomizer.create(random))
        .add(new Object() {
          @FromJson Simple simple(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
              keysSeen.add(reader.nextName());
              reader.skipValue();
            }
            reader.endObject();
            return null;
          }
        })
        .build();
    JsonAdapter<Simple> adapter = moshi.adapter(Simple.class);
    Simple simple1 = adapter.fromJson("{\"one\":\"one\",\"two\":\"two\"}");
    assertThat(keysSeen).containsExactly("one", "two").inOrder();
    keysSeen.clear();
    Simple simple2 = adapter.fromJson("{\"one\":\"one\",\"two\":\"two\"}");
    assertThat(keysSeen).containsExactly("two", "one").inOrder();
  }

  @Test public void nestedKeysReordered() throws IOException {
    final List<String> keysSeen = new ArrayList<String>();
    Moshi moshi = new Moshi.Builder()
        .add(ObjectOrderRandomizer.create(random))
        .add(new Object() {
          @FromJson Simple simple(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.hasNext()) {
              keysSeen.add(reader.nextName());
              reader.skipValue();
            }
            reader.endObject();
            return null;
          }
        })
        .build();
    JsonAdapter<Nested> adapter = moshi.adapter(Nested.class);
    Nested nested1 = adapter.fromJson("{\"nested\":{\"nested\":{\"simple\":{\"one\":\"one\",\"two\":\"two\"}}}}");
    assertThat(keysSeen).containsExactly("one", "two").inOrder();
    keysSeen.clear();
    Nested nested2 = adapter.fromJson("{\"nested\":{\"nested\":{\"simple\":{\"one\":\"one\",\"two\":\"two\"}}}}");
    assertThat(keysSeen).containsExactly("two", "one").inOrder();
  }


  @Test public void arraysAreNotReordered() throws IOException {
    Moshi moshi = new Moshi.Builder()
        .add(ObjectOrderRandomizer.create(random))
        .build();
    JsonAdapter<HasArray> adapter = moshi.adapter(HasArray.class);
    for (int i = 0; i < 1000; i++) {
      HasArray hasArray = adapter.fromJson("{\"strings\":[\"one\",\"two\"]}");
      assertThat(hasArray.strings).containsExactly("one", "two").inOrder();
    }
  }
}
