package io.github.flamehub.commons.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;

public class FileTypeAdapter extends TypeAdapter<File> {
    @Override
    public void write(JsonWriter out, File file) throws IOException {
        if (file == null) {
            out.nullValue();
        } else {
            out.value(file.getPath());
        }
    }

    @Override
    public File read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            return new File(in.nextString());
        }
    }
}