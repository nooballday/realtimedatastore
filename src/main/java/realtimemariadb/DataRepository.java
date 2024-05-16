package realtimemariadb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import jakarta.inject.Singleton;

@Singleton
public class DataRepository {

    private final JdbcTemplate jdbcTemplate;
    private final Logger LOG = LoggerFactory.getLogger(DataRepository.class);

    public DataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // return null if eror happened
    List<Map<String, Object>> fetchData(String tableName) {
        try {
            String query = String.format("SELECT * FROM %s", tableName);
            Map<String, Object> params = new HashMap<>();
            params.put("tableName", tableName);
            return jdbcTemplate.queryForList(query);
        } catch (Exception e) {
            LOG.error(String.format("Something happen when querying to : ", tableName), e);
            return null;
        }
    }

}
