package pl.olczku.wOOLCODEHELPOPDC;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DiscordWebhook {

    private final String url;
    private String content;
    private String username;
    private String avatarUrl;
    private boolean tts;
    private List<EmbedObject> embeds = new ArrayList<>();

    public DiscordWebhook(String url) {
        this.url = url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setTts(boolean tts) {
        this.tts = tts;
    }

    public void addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
    }

    public void execute() throws IOException {
        if (content == null && embeds.isEmpty()) {
            throw new IllegalArgumentException("Musisz ustawic content lub embed!");
        }

        JsonObject json = new JsonObject();

        if (content != null) {
            json.addProperty("content", content);
        }

        if (username != null) {
            json.addProperty("username", username);
        }

        if (avatarUrl != null) {
            json.addProperty("avatar_url", avatarUrl);
        }

        if (tts) {
            json.addProperty("tts", true);
        }

        if (!embeds.isEmpty()) {
            List<JsonObject> embedObjects = new ArrayList<>();
            for (EmbedObject embed : embeds) {
                JsonObject jsonEmbed = new JsonObject();

                if (embed.getTitle() != null) {
                    jsonEmbed.addProperty("title", embed.getTitle());
                }

                if (embed.getDescription() != null) {
                    jsonEmbed.addProperty("description", embed.getDescription());
                }

                if (embed.getUrl() != null) {
                    jsonEmbed.addProperty("url", embed.getUrl());
                }

                if (embed.getColor() != null) {
                    jsonEmbed.addProperty("color", embed.getColor());
                }

                if (embed.getFields() != null && !embed.getFields().isEmpty()) {
                    List<JsonObject> fieldObjects = new ArrayList<>();
                    for (EmbedObject.Field field : embed.getFields()) {
                        JsonObject jsonField = new JsonObject();
                        jsonField.addProperty("name", field.getName());
                        jsonField.addProperty("value", field.getValue());
                        jsonField.addProperty("inline", field.isInline());
                        fieldObjects.add(jsonField);
                    }
                    jsonEmbed.add("fields", new Gson().toJsonTree(fieldObjects));
                }

                embedObjects.add(jsonEmbed);
            }
            json.add("embeds", new Gson().toJsonTree(embedObjects));
        }

        URL url = new URL(this.url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Java-DiscordWebhook");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(json.toString().getBytes());
            outputStream.flush();
        }

        connection.getInputStream().close();
        connection.disconnect();
    }

    public static class EmbedObject {
        private String title;
        private String description;
        private String url;
        private Integer color;
        private List<Field> fields = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public Integer getColor() {
            return color;
        }

        public EmbedObject setColor(int color) {
            this.color = color;
            return this;
        }

        public List<Field> getFields() {
            return fields;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        public static class Field {
            private final String name;
            private final String value;
            private final boolean inline;

            public Field(String name, String value, boolean inline) {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            public String getName() {
                return name;
            }

            public String getValue() {
                return value;
            }

            public boolean isInline() {
                return inline;
            }
        }
    }
}
