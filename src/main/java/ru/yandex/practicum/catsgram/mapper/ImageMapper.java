package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.*;
import ru.yandex.practicum.catsgram.model.Image;

import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImageMapper {
    public static Image mapToImage(long postId, Path filePath, String originalFileName) {
        Image  image = new Image();
        image.setOriginalFileName(originalFileName);
        image.setFilePath(filePath.toString());
        image.setPostId(postId);
        return image;
    }

    public static ImageDto mapToImageDto(Image image, byte[] data) {
        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setPostId(image.getPostId());
        imageDto.setFileName(image.getOriginalFileName());
        imageDto.setData(data);
        return imageDto;
    }

    public static ImageUploadResponse mapToImageUploadResponse(Image image) {
        ImageUploadResponse dto = new ImageUploadResponse();
        dto.setId(image.getId());
        dto.setPostId(image.getPostId());
        dto.setFileName(image.getOriginalFileName());
        dto.setFilePath(image.getFilePath());
        return dto;
    }

}



