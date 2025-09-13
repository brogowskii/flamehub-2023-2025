package io.github.flamehub.commons.util;

import java.awt.Color;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;

public final class DiscordWebhook {

  private final String url;
  private final List<EmbedObject> embeds = new ArrayList<>();
  private String content;
  private String username;
  private String avatarUrl;
  private boolean tts;

  public DiscordWebhook(final String url) {
    this.url = url;
  }

  public void setContent(final String content) {
    this.content = content;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public void setAvatarUrl(final String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public void setTts(final boolean tts) {
    this.tts = tts;
  }

  public void addEmbed(final EmbedObject embed) {
    embeds.add(embed);
  }

  public void execute() {
    if (content == null && embeds.isEmpty()) {
      throw new IllegalArgumentException("Set content or add at least one EmbedObject");
    }
    try {
      final JSONObject json = new JSONObject();
      json.put("content", content);
      json.put("username", username);
      json.put("avatar_url", avatarUrl);
      json.put("tts", Boolean.valueOf(tts));
      if (!embeds.isEmpty()) {
        final List<JSONObject> embedObjects = new ArrayList<>();
        for (final EmbedObject embed : embeds) {
          final JSONObject jsonEmbed = new JSONObject();
          jsonEmbed.put("title", embed.getTitle());
          jsonEmbed.put("description", embed.getDescription());
          jsonEmbed.put("url", embed.getUrl());
          if (embed.getTimestamp() != null) {
            jsonEmbed.put("timestamp", embed.getTimestamp());
          }
          if (embed.getColor() != null) {
            final Color color = embed.getColor();
            int rgb = color.getRed();
            rgb = (rgb << 8) + color.getGreen();
            rgb = (rgb << 8) + color.getBlue();
            jsonEmbed.put("color", rgb);
          }
          final EmbedObject.Footer footer = embed.getFooter();
          final EmbedObject.Image image = embed.getImage();
          final EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
          final EmbedObject.Author author = embed.getAuthor();
          final List<EmbedObject.Field> fields = embed.getFields();
          if (footer != null) {
            final JSONObject jsonFooter = new JSONObject();
            jsonFooter.put("text", footer.getText());
            jsonFooter.put("icon_url", footer.getIconUrl());
            jsonEmbed.put("footer", jsonFooter);
          }
          if (image != null) {
            final JSONObject jsonImage = new JSONObject();
            jsonImage.put("url", image.getUrl());
            jsonEmbed.put("image", jsonImage);
          }
          if (thumbnail != null) {
            final JSONObject jsonThumbnail = new JSONObject();
            jsonThumbnail.put("url", thumbnail.getUrl());
            jsonEmbed.put("thumbnail", jsonThumbnail);
          }
          if (author != null) {
            final JSONObject jsonAuthor = new JSONObject();
            jsonAuthor.put("name", author.getName());
            jsonAuthor.put("url", author.getUrl());
            jsonAuthor.put("icon_url", author.getIconUrl());
            jsonEmbed.put("author", jsonAuthor);
          }
          final List<JSONObject> jsonFields = new ArrayList<>();
          for (final EmbedObject.Field field : fields) {
            final JSONObject jsonField = new JSONObject();
            jsonField.put("name", field.getName());
            jsonField.put("value", field.getValue());
            jsonField.put("inline", Boolean.valueOf(field.isInline()));
            jsonFields.add(jsonField);
          }
          jsonEmbed.put("fields", jsonFields.toArray());
          embedObjects.add(jsonEmbed);
        }
        json.put("embeds", embedObjects.toArray());
      }
      final URL url = new URL(this.url);
      final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.addRequestProperty("Content-Type", "application/json");
      connection.addRequestProperty("User-Agent", "Java-DiscordWebhook");
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      final OutputStream stream = connection.getOutputStream();
      stream.write(json.toString().getBytes());
      stream.flush();
      stream.close();
      connection.getInputStream().close();
      connection.disconnect();
    } catch (final Exception ex) {
      ex.printStackTrace();
    }
  }

  public static class EmbedObject {

    private final List<Field> fields = new ArrayList<>();
    private String title;
    private String description;
    private String url;
    private String timestamp;
    private Color color;
    private Footer footer;
    private Thumbnail thumbnail;
    private Image image;
    private Author author;

    public String getTitle() {
      return title;
    }

    public EmbedObject setTitle(final String title) {
      this.title = title;
      return this;
    }

    public String getDescription() {
      return description;
    }

    public EmbedObject setDescription(final String description) {
      this.description = description;
      return this;
    }

    public String getUrl() {
      return url;
    }

    public EmbedObject setUrl(final String url) {
      this.url = url;
      return this;
    }

    public Color getColor() {
      return color;
    }

    public EmbedObject setColor(final Color color) {
      this.color = color;
      return this;
    }

    public Footer getFooter() {
      return footer;
    }

    public Thumbnail getThumbnail() {
      return thumbnail;
    }

    public EmbedObject setThumbnail(final String url) {
      thumbnail = new Thumbnail(url);
      return this;
    }

    public Image getImage() {
      return image;
    }

    public EmbedObject setImage(final String url) {
      image = new Image(url);
      return this;
    }

    public Author getAuthor() {
      return author;
    }

    public List<Field> getFields() {
      return fields;
    }

    public String getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(final String timestamp) {
      this.timestamp = timestamp;
    }

    public EmbedObject setFooter(final String text, final String icon) {
      footer = new Footer(text, icon);
      return this;
    }

    public EmbedObject setAuthor(final String name, final String url, final String icon) {
      author = new Author(name, url, icon);
      return this;
    }

    public EmbedObject addField(final String name, final String value, final boolean inline) {
      fields.add(new Field(name, value, inline));
      return this;
    }

    private class Footer {

      private final String text;

      private final String iconUrl;

      private Footer(final String text, final String iconUrl) {
        this.text = text;
        this.iconUrl = iconUrl;
      }

      private String getText() {
        return text;
      }

      private String getIconUrl() {
        return iconUrl;
      }
    }

    private class Thumbnail {

      private final String url;

      private Thumbnail(final String url) {
        this.url = url;
      }

      private String getUrl() {
        return url;
      }
    }

    private class Image {

      private final String url;

      private Image(final String url) {
        this.url = url;
      }

      private String getUrl() {
        return url;
      }
    }

    private class Author {

      private final String name;

      private final String url;

      private final String iconUrl;

      private Author(final String name, final String url, final String iconUrl) {
        this.name = name;
        this.url = url;
        this.iconUrl = iconUrl;
      }

      private String getName() {
        return name;
      }

      private String getUrl() {
        return url;
      }

      private String getIconUrl() {
        return iconUrl;
      }
    }

    private class Field {

      private final String name;

      private final String value;

      private final boolean inline;

      private Field(final String name, final String value, final boolean inline) {
        this.name = name;
        this.value = value;
        this.inline = inline;
      }

      private String getName() {
        return name;
      }

      private String getValue() {
        return value;
      }

      private boolean isInline() {
        return inline;
      }
    }
  }

  private class Footer {

    private final String text;

    private final String iconUrl;

    private Footer(final String text, final String iconUrl) {
      this.text = text;
      this.iconUrl = iconUrl;
    }

    private String getText() {
      return text;
    }

    private String getIconUrl() {
      return iconUrl;
    }
  }

  private class Thumbnail {

    private final String url;

    private Thumbnail(final String url) {
      this.url = url;
    }

    private String getUrl() {
      return url;
    }
  }

  private class Image {

    private final String url;

    private Image(final String url) {
      this.url = url;
    }

    private String getUrl() {
      return url;
    }
  }

  private class Author {

    private final String name;

    private final String url;

    private final String iconUrl;

    private Author(final String name, final String url, final String iconUrl) {
      this.name = name;
      this.url = url;
      this.iconUrl = iconUrl;
    }

    private String getName() {
      return name;
    }

    private String getUrl() {
      return url;
    }

    private String getIconUrl() {
      return iconUrl;
    }
  }

  private class Field {

    private final String name;

    private final String value;

    private final boolean inline;

    private Field(final String name, final String value, final boolean inline) {
      this.name = name;
      this.value = value;
      this.inline = inline;
    }

    private String getName() {
      return name;
    }

    private String getValue() {
      return value;
    }

    private boolean isInline() {
      return inline;
    }
  }

  private class JSONObject {

    private final HashMap<String, Object> map = new HashMap<>();

    private JSONObject() {
    }

    void put(final String key, final Object value) {
      if (value != null) {
        map.put(key, value);
      }
    }

    public String toString() {
      final StringBuilder builder = new StringBuilder();
      final Set<Map.Entry<String, Object>> entrySet = map.entrySet();
      builder.append("{");
      int i = 0;
      for (final Map.Entry<String, Object> entry : entrySet) {
        final Object val = entry.getValue();
        builder.append(quote(entry.getKey())).append(":");
        if (val instanceof String) {
          builder.append(quote(String.valueOf(val)));
        } else if (val instanceof Integer) {
          builder.append(Integer.valueOf(String.valueOf(val)));
        } else if (val instanceof Boolean) {
          builder.append(val);
        } else if (val instanceof JSONObject) {
          builder.append(val);
        } else if (val.getClass().isArray()) {
          builder.append("[");
          final int len = Array.getLength(val);
          for (int j = 0; j < len; j++) {
            builder.append(Array.get(val, j).toString()).append((j != len - 1) ? "," : "");
          }
          builder.append("]");
        }
        builder.append((++i == entrySet.size()) ? "}" : ",");
      }
      return builder.toString();
    }

    private String quote(final String string) {
      return "\"" + string + "\"";
    }
  }
}