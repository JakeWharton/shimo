package com.jakewharton.shimo;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT;

final class OrderRandomizingAdapter extends JsonAdapter<Object> {
  private final Random random;
  private final JsonAdapter<Object> delegate;

  public OrderRandomizingAdapter(Random random, JsonAdapter<Object> delegate) {
    this.random = random;
    this.delegate = delegate;
  }

  private Map<String, Object> shuffleMap(Map<String, ?> ordered) {
    ArrayList<String> keys = new ArrayList<>(ordered.keySet());
    Collections.shuffle(keys, random);

    Map<String, Object> shuffled = new LinkedHashMap<>(ordered.size());
    for (String key : keys) {
      shuffled.put(key, ordered.get(key));
    }
    return shuffled;
  }

  @Override public Object fromJson(JsonReader reader) throws IOException {
    if (reader.peek() != BEGIN_OBJECT) {
      return delegate.fromJson(reader);
    }
    Map<String, ?> ordered = (Map<String, ?>) reader.readJsonValue();
    Map<String, ?> shuffled = shuffleMap(ordered);
    return delegate.fromJsonValue(shuffled);
  }

  @Override public void toJson(JsonWriter writer, Object value) throws IOException {
    Object jsonValue = delegate.toJsonValue(value);
    if (jsonValue instanceof Map) {
      Map<String, ?> ordered = ((Map<String, ?>) jsonValue);
      jsonValue = shuffleMap(ordered);
    }
    // TODO requires Moshi 1.10 or newer:
    //  writer.jsonValue(jsonValue);
    writeJsonValue(writer, jsonValue);
  }

  private static void writeJsonValue(JsonWriter writer, Object value) throws IOException {
    if (value instanceof Map<?, ?>) {
      writer.beginObject();
      for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
        Object key = entry.getKey();
        if (!(key instanceof String)) {
          throw new IllegalArgumentException(key == null
              ? "Map keys must be non-null"
              : "Map keys must be of type String: " + key.getClass().getName());
        }
        writer.name(((String) key));
        writeJsonValue(writer, entry.getValue());
      }
      writer.endObject();

    } else if (value instanceof List<?>) {
      writer.beginArray();
      for (Object element : ((List<?>) value)) {
        writeJsonValue(writer, element);
      }
      writer.endArray();

    } else if (value instanceof String) {
      writer.value(((String) value));

    } else if (value instanceof Boolean) {
      writer.value(((Boolean) value).booleanValue());

    } else if (value instanceof Double) {
      writer.value(((Double) value).doubleValue());

    } else if (value instanceof Long) {
      writer.value(((Long) value).longValue());

    } else if (value instanceof Number) {
      writer.value(((Number) value));

    } else if (value == null) {
      writer.nullValue();

    } else {
      throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
    }
  }
}
