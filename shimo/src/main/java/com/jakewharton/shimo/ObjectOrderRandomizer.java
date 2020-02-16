package com.jakewharton.shimo;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Random;
import java.util.Set;

public final class ObjectOrderRandomizer {
  public static JsonAdapter.Factory create() {
    return create(new Random());
  }

  public static JsonAdapter.Factory create(Random random) {
    return new Factory(random);
  }

  private static final class Factory implements JsonAdapter.Factory {
    private final Random random;

    Factory(Random random) {
      this.random = random;
    }

    @Override
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
      JsonAdapter<Object> delegate = moshi.nextAdapter(this, type, annotations);
      return new OrderRandomizingAdapter(random, delegate);
    }
  }
}
