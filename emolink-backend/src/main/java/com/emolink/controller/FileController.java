package com.emolink.controller;

import com.emolink.common.Result;
import com.emolink.exception.BusinessException;
import com.emolink.vo.UploadVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController extends BaseController {

    private static final long MAX_IMAGE_SIZE = 20 * 1024 * 1024L;
    private static final Set<String> ALLOWED_FOLDERS = Set.of("avatar", "common");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".tif", ".tiff", ".avif"
    );

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UploadVO> uploadImage(@RequestParam("file") MultipartFile file,
                                        @RequestParam(defaultValue = "common") String folder,
                                        HttpServletRequest request) {
        Long userId = currentUserId(request);
        validateImage(file);

        String targetFolder = ALLOWED_FOLDERS.contains(folder) ? folder : "common";
        String extension = resolveExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;

        Path targetDirectory = Paths.get(uploadDir, targetFolder, String.valueOf(userId)).toAbsolutePath().normalize();
        Path targetFile = targetDirectory.resolve(filename).normalize();

        try {
            Files.createDirectories(targetDirectory);
            file.transferTo(targetFile);
        } catch (IOException exception) {
            throw new BusinessException(500, "上传失败，请稍后重试");
        }

        UploadVO vo = new UploadVO();
        vo.setUrl("/uploads/" + targetFolder + "/" + userId + "/" + filename);
        vo.setOriginalName(file.getOriginalFilename());
        vo.setSize(file.getSize());
        return Result.success("上传成功", vo);
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择图片文件");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException(400, "图片大小不能超过 20MB");
        }
        String contentType = StringUtils.defaultString(file.getContentType());
        String extension = resolveExtension(file.getOriginalFilename());
        if (!contentType.startsWith("image/") && !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(400, "仅支持上传图片文件");
        }
    }

    private String resolveExtension(String originalFilename) {
        if (StringUtils.isBlank(originalFilename) || !originalFilename.contains(".")) {
            return ".png";
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        return extension.length() > 10 ? ".png" : extension;
    }
}
