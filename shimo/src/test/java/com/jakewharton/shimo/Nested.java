package com.jakewharton.shimo;

final class Nested {
  final Nested nested;
  final Simple simple;

  Nested(Nested nested, Simple simple) {
    this.nested = nested;
    this.simple = simple;
  }
}
