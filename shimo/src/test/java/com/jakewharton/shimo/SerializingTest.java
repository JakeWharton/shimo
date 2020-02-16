package com.jakewharton.shimo;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class SerializingTest {
  private final Random random = new Random(1234);
  private final Moshi moshi = new Moshi.Builder()
      .add(ObjectOrderRandomizer.create(random))
      .build();

  @Test public void keysReordered() {
    Simple simple = new Simple("one", "two");
    JsonAdapter<Simple> adapter = moshi.adapter(Simple.class);
    String json1 = adapter.toJson(simple);
    assertThat(json1).isEqualTo("{\"one\":\"one\",\"two\":\"two\"}");
    String json2 = adapter.toJson(simple);
    assertThat(json2).isEqualTo("{\"two\":\"two\",\"one\":\"one\"}");
  }

  @Test public void nestedKeysReordered() {
    Nested nested = new Nested(new Nested(new Nested(null, new Simple("one", "two")), null), null);
    JsonAdapter<Nested> adapter = moshi.adapter(Nested.class);
    String json1 = adapter.toJson(nested);
    assertThat(json1).isEqualTo(
        "{\"nested\":{\"nested\":{\"simple\":{\"one\":\"one\",\"two\":\"two\"}}}}");
    String json2 = adapter.toJson(nested);
    assertThat(json2).isEqualTo(
        "{\"nested\":{\"nested\":{\"simple\":{\"two\":\"two\",\"one\":\"one\"}}}}");
  }

  @Test public void mapsReordered() {
    Map<String, Object> nested = new LinkedHashMap<String, Object>();
    nested.put("one", "one");
    nested.put("two", "two");
    Map<String, Object> map = new LinkedHashMap<String, Object>();
    map.put("string", "string");
    map.put("map", nested);
    @SuppressWarnings("rawtypes") // Being lazy.
    JsonAdapter<Map> adapter = moshi.adapter(Map.class);
    String json1 = adapter.toJson(map);
    assertThat(json1).isEqualTo(
        "{\"string\":\"string\",\"map\":{\"two\":\"two\",\"one\":\"one\"}}");
    String json2 = adapter.toJson(map);
    assertThat(json2).isEqualTo(
        "{\"map\":{\"two\":\"two\",\"one\":\"one\"},\"string\":\"string\"}");
  }

  @Test public void arraysAreNotReordered() {
    HasArray hasArray = new HasArray(Arrays.asList("one", "two"));
    JsonAdapter<HasArray> adapter = moshi.adapter(HasArray.class);
    for (int i = 0; i < 1000; i++) {
      String json = adapter.toJson(hasArray);
      assertThat(json).isEqualTo("{\"strings\":[\"one\",\"two\"]}");
    }
  }
}
