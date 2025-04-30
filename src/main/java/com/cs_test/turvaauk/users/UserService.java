package com.cs_test.turvaauk.users;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void registerUser(String username, String password) throws DuplicateKeyException {
        String sql =
                "INSERT INTO users (username, password) VALUES ('"
                + username + "', '"
                + password + "')";
        jdbcTemplate.update(sql);
    }

    public boolean validateCredentials(String username, String password) {
        String sql =
                "SELECT COUNT(*) FROM users WHERE username = '"
                        + username + "' AND password = '"
                        + password + "'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null && count > 0;
    }

    public boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = '" + username + "'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return (count != null && count > 0);
    }
}
