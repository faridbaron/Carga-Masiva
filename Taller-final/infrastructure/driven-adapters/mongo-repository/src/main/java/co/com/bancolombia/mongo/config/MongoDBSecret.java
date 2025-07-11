package co.com.bancolombia.mongo.config;


public class MongoDBSecret {

    private final String uri;

    private MongoDBSecret(Builder builder) {
        this.uri = builder.uri;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "MongoDBSecret{" +
                "uri='" + uri + '\'' +
                '}';
    }

    // Builder interno
    public static class Builder {
        private String uri;

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public MongoDBSecret build() {
            return new MongoDBSecret(this);
        }
    }
}

