package io.github.flamehub.commons.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;

public class FlamePrettyPrinter extends DefaultPrettyPrinter {

  @Override
  public DefaultPrettyPrinter createInstance() {
    return new FlamePrettyPrinter();
  }

  @Override
  public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
    g.writeRaw(",\n");
    _arrayIndenter.writeIndentation(g, _nesting);
  }

  @Override
  public void writeStartArray(JsonGenerator g) throws IOException {
    g.writeRaw("[\n");
    _nesting++;
    _arrayIndenter.writeIndentation(g, _nesting);
  }

  @Override
  public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
    if (!_arrayIndenter.isInline()) {
      _nesting--;
    }
    if (nrOfValues > 0) {
      _arrayIndenter.writeIndentation(g, _nesting);
    }
    g.writeRaw("\n");
    _arrayIndenter.writeIndentation(g, _nesting);
    g.writeRaw(']');
  }

  @Override
  public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
    g.writeRaw(",\n");
    _objectIndenter.writeIndentation(g, _nesting);
  }

  @Override
  public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
    g.writeRaw(_objectFieldValueSeparatorWithSpaces);
  }

  @Override
  public void writeStartObject(JsonGenerator g) throws IOException {
    g.writeRaw("{\n");
    _nesting++;
    _objectIndenter.writeIndentation(g, _nesting);
  }

  @Override
  public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
    if (!_objectIndenter.isInline()) {
      _nesting--;
    }
    if (nrOfEntries > 0) {
      _objectIndenter.writeIndentation(g, _nesting);
    }
    g.writeRaw("\n");
    _objectIndenter.writeIndentation(g, _nesting);
    g.writeRaw('}');
  }
}