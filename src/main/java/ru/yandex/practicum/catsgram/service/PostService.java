package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.mapper.UserMapper;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PostService {

//    private final Map<Long, Post> posts = new HashMap<>();
    PostRepository postRepository;
    UserRepository userRepository;
    ImageRepository imageRepository;

    UserService userService;


//    public PostService(UserService userService) {
//        this.userService = userService;
//    }

    public List<PostDto> findAll(Long size, String sort, Long from) {
        switch (sort) {
            case  "ASCENDING" :
                return postRepository.findAll().stream()
                        .sorted(Comparator.comparing(Post::getPostDate))
                        .filter(post1 -> post1.getId() > from)
                        .limit(size)
                        .map(PostMapper::mapToPostDto)
                        .toList();

            case  "DESCENDING" :
                return postRepository.findAll().stream()
                        .sorted(Comparator.comparing(Post::getPostDate).reversed())
                        .filter(post -> post.getId() > from)
                        .limit(size)
                        .map(PostMapper::mapToPostDto)
                        .toList();
            default:
                return new ArrayList<>(postRepository.findAll().stream()
                        .map(PostMapper::mapToPostDto)
                        .toList());
        }

    }

    public PostDto getPostById(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow( () -> new NotFoundException("Пост не найден с ID: " + postId));

        User author = userRepository.findById(post.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("Автор поста не найден"));

        //List<Image> images = imageRepository.findByPostId(postId);

        post.setAuthor(author);
        //post.setImages(images);

        return PostMapper.mapToPostDto(post);

    }


    public PostDto create(NewPostRequest request) {

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ConditionsNotMetException("Автор с id = " + request.getAuthorId() + " не найден"));

        // проверяем выполнение необходимых условий
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        // формируем дополнительные данные
        Post post = PostMapper.mapToPost(request, author);

        // сохраняем новую публикацию в памяти приложения
        postRepository.save(post);

        return PostMapper.mapToPostDto(post);
    }

    public PostDto update(long postId, NewPostRequest request) {
        // проверяем необходимые условия
//        if (newPost.getId() == null) {
//            throw new ConditionsNotMetException("Id должен быть указан");
//        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("Пост с id = " + postId + " не найден")
        );

        post.setDescription(request.getDescription());
        post.setPostDate(Instant.now());

        postRepository.update(post);

        return PostMapper.mapToPostDto(post);
    }

    // вспомогательный метод для генерации идентификатора нового поста
   /* private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }*/
}
