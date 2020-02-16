package com.jakewharton.shimo;

import org.junit.Test;

import static org.junit.Assert.fail;

public final class ObjectOrderRandomizerTest {
  @Test public void createNoNulls() {
    try {
      ObjectOrderRandomizer.create(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }
}
