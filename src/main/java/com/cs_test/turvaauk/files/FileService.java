package com.cs_test.turvaauk.files;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class FileService {

    private final JdbcTemplate jdbcTemplate;

    public FileService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void uploadFile(MultipartFile file) throws IOException, DuplicateKeyException {
        String sql =
                "INSERT INTO files (name, mime_type, data, uploaded_at, size) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes(),
                new Timestamp(System.currentTimeMillis()),
                file.getSize()
        );
    }

    public List<FileInfoDto> getFiles(String orderBy, String direction) {
        String sql = String.format("""
            SELECT
              id, name, mime_type, uploaded_at, size
            FROM files
            ORDER BY %s %s
            """, orderBy, direction);
        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(FileInfoDto.class)
        );
    }

    public boolean deleteFile(Integer id) {
        String sql = "DELETE FROM files WHERE id = '" + id + "'";
        return jdbcTemplate.update(sql) > 0;
    }

    public FileInfoDto getFileData(Long id) {
        String sql = "SELECT id, name, mime_type, data, uploaded_at, size FROM files WHERE id = " + id;
        RowMapper<FileInfoDto> mapper =
                BeanPropertyRowMapper.newInstance(FileInfoDto.class);

        return jdbcTemplate.queryForObject(sql, mapper);
    }
}
