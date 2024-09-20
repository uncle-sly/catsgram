package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.model.Post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Map<Long, Image> images = new HashMap<>();
    private final PostService postService;

    // Укажите директорию для хранения изображений
    @Value("${catsgram.image-directory}")
    private String imageDirectory;

    //получение данных об изображениях указанного поста
    public List<Image> getPostImages(long postId) {
        return images.values().stream()
                .filter(image -> image.getPostId() == postId)
                .toList();
    }

    // сохранение списка изображений, связанных с указанным постом
    public List<Image> saveImages(long postId, List<MultipartFile> files) {
        return files.stream().map(file -> saveImage(postId, file)).toList();
    }

    // сохранение отдельного изображения, связанного с указанным постом
    private Image saveImage(long postId, MultipartFile file) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new ConditionsNotMetException("Указанный пост не найден"));

        // сохраняем изображение на диск и возвращаем путь к файлу
        Path filePath = saveFile(file, post);

        // создаём объект для хранения данных изображения
        long imageId = getNextId();
        // создание объекта изображения и заполнение его данными
        Image image = new Image();
        image.setId(imageId);
        image.setPostId(postId);
        image.setFilePath(filePath.toString());

        // запоминаем название файла, которое было при его передаче
        image.setOriginalFileName(file.getOriginalFilename());
        images.put(imageId, image);
        return image;
    }

    private long getNextId() {
        long currentMaxId = images.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // сохранение файла изображения
    private Path saveFile(MultipartFile file, Post post) {
        try {
            // формирование уникального названия файла на основе текущего времени и расширения оригинального файла
            String uniqFileName = String.format("%d.%s", Instant.now().toEpochMilli(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()));

            // формирование пути для сохранения файла с учётом идентификаторов автора и поста
            Path uploadPath = Paths.get(imageDirectory, String.valueOf(post.getAuthorId()), post.getId().toString());
            System.out.println(uploadPath);
            Path filePath = uploadPath.resolve(uniqFileName);
            System.out.println(filePath);

            // создаём директории, если они ещё не созданы
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // сохраняем файл по сформированному пути
            file.transferTo(filePath);
            return filePath;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // загружаем данные указанного изображения с диска
    public ImageData getImageData(long imageId) {

        if (!images.keySet().contains(imageId)) {
            throw new NotFoundException("Изображение с id = " + imageId + " не найдено");
        }
        Image image = images.get(imageId);

        // загрузка файла с диска
        byte[] data = loadFile(image);
        return new ImageData(data, image.getOriginalFileName());
    }

    private byte[] loadFile(Image image) {
        Path path = Paths.get(image.getFilePath());
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new ImageFileException("Ошибка чтения файла.  Id: " + image.getId()
                        + ", name: " + image.getOriginalFileName());
            }
        } else {
            throw new ImageFileException("Файл не найден. Id: " + image.getId() + ", name: " + image.getOriginalFileName());
        }
    }

}
