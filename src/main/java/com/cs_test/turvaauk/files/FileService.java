package com.cs_test.turvaauk.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileService {

    private final JdbcTemplate jdbcTemplate;
    private final Path fileStorageLocation;

    public FileService(JdbcTemplate jdbcTemplate, @Value("${file.storage.location}") String storageLocation) throws IOException {
        this.jdbcTemplate = jdbcTemplate;
        this.fileStorageLocation = Paths.get(storageLocation)
                .toAbsolutePath()
                .normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

    public void uploadFile(MultipartFile file) throws IOException, DuplicateKeyException {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        if (filename.toLowerCase().endsWith(".zip")) {
            try (ZipInputStream zipIn = new ZipInputStream(file.getInputStream())) {
                ZipEntry entry;
                while ((entry = zipIn.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        zipIn.closeEntry();
                        continue;
                    }

                    Path entryPath = this.fileStorageLocation.resolve(entry.getName());

                    Files.createDirectories(entryPath.getParent());

                    try (OutputStream out = Files.newOutputStream(entryPath,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING)) {
                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = zipIn.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    }

                    String mime = Files.probeContentType(entryPath);
                    long size = Files.size(entryPath);


                    jdbcTemplate.update(
                            "INSERT INTO files (name, mime_type, path, uploaded_at, size) VALUES (?, ?, ?, ?, ?)",
                            entry.getName(),
                            mime,
                            entryPath.toString(),
                            new Timestamp(System.currentTimeMillis()),
                            size
                    );

                    zipIn.closeEntry();
                }
            }
        }
        else {
            Path fileLocation = fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
            String sql = "INSERT INTO files (name, mime_type, path, uploaded_at, size) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    filename,
                    file.getContentType(),
                    fileLocation.toString(),
                    new Timestamp(System.currentTimeMillis()),
                    file.getSize()
            );
        }
    }

    public List<FileInfoDto> getFiles(String orderBy, String direction) {
        String sql = String.format("""
            SELECT
              id, name, mime_type, size, uploaded_at, path
            FROM files
            ORDER BY %s %s
            """, orderBy, direction);
        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(FileInfoDto.class)
        );
    }

    public boolean deleteFile(Integer id) throws IOException {
        String select_sql = "SELECT path FROM files WHERE id = " + id;
        String filename = jdbcTemplate.queryForObject(select_sql, String.class);

        Path filePath = fileStorageLocation.resolve(filename).normalize();
        Files.deleteIfExists(filePath);

        String sql = "DELETE FROM files WHERE id = " + id;
        return jdbcTemplate.update(sql) > 0;
    }

    public FileInfoDto getFileData(Long id) {
        String sql = "SELECT id, name, mime_type, path, uploaded_at, size FROM files WHERE id = " + id;
        RowMapper<FileInfoDto> mapper =
                BeanPropertyRowMapper.newInstance(FileInfoDto.class);

        return jdbcTemplate.queryForObject(sql, mapper);
    }

    public Resource loadFileAsResource(Long id) throws MalformedURLException {
        FileInfoDto info = getFileData(id);
        Path filePath = fileStorageLocation.resolve(info.getPath()).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read file: " + info.getPath());
        }
    }
}
