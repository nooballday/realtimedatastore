package realtimemariadb;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("datasources.default")
public class DatasourceConfig {

    private String host;

    private String schema;
    
    private String username;

    private String password;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    

}
