package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.dto.ImageDto;
import ru.yandex.practicum.catsgram.dto.ImageUploadResponse;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.service.ImageService;

import java.util.List;

@RestController
//@RequestMapping("/posts")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/posts/{postId}/images")
    public List<ImageDto> getPostImages(@PathVariable("postId") long postId) {
        return imageService.getPostImages(postId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts/{postId}/images")
    public List<ImageUploadResponse> addPostImages(@PathVariable("postId") long postId,
                                                   @RequestParam ("image") List<MultipartFile> files) {
        return imageService.saveImages(postId, files);
    }

    @GetMapping (value = "/images/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> dowloadImage(@PathVariable("imageId") long imageId) {

        ImageData imageData = imageService.getImageData(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment()
                                        .filename(imageData.getName())
                                        .build());
        return new ResponseEntity<>(imageData.getData(),headers, HttpStatus.OK);
    }

}
