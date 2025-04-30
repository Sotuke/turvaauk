package com.cs_test.turvaauk.files;


import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    public final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Select a file to upload");
        }

        try {
            fileService.uploadFile(file);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("File uploaded successfully: " + file.getOriginalFilename());
        } catch (DuplicateKeyException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("File already exists");
        }
    }

    @GetMapping
    public List<FileInfoDto> getFiles(@RequestParam(defaultValue = "uploaded_at") String orderBy,
                                      @RequestParam(defaultValue = "ASC") String direction) {
        return fileService.getFiles(orderBy, direction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id) {
        boolean deleted = fileService.deleteFile(id);
        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        FileInfoDto file = fileService.getFileData(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(file.getName()).build()
        );
        headers.setContentType(
                MediaType.parseMediaType(file.getMimeType())
        );

        return new ResponseEntity<>(
                file.getData(),
                headers,
                HttpStatus.OK
        );
    }
}
