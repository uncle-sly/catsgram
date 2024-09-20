package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class PostService {

    private final Map<Long, Post> posts = new HashMap<>();
    UserService userService;

    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(Long size, String sort, Long from) {
        switch (sort) {
            case  "ASCENDING" :
                return posts.values().stream()
                        .sorted(Comparator.comparing(Post::getPostDate))
                        .filter(post1 -> post1.getId() > from)
                        .limit(size)
                        .toList();

            case  "DESCENDING" :
                return posts.values().stream()
                        .sorted(Comparator.comparing(Post::getPostDate).reversed())
                        .filter(post -> post.getId() > from)
                        .limit(size)
                        .toList();
            default:
                return new ArrayList<>(posts.values());
        }

    }

    public Optional<Post> getPostById(Long id) {
        return Optional.ofNullable(posts.get(id));
    }


    public Post create(Post post) {

        if (userService.findUserById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + post.getId() + " не найден");
        }

        // проверяем выполнение необходимых условий
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        // формируем дополнительные данные
        post.setId(getNextId());
        post.setPostDate(LocalDate.ofInstant(Instant.now(), ZoneId.of("UTC+3")));
//        post.setPostDate(post.getPostDate());
        // сохраняем новую публикацию в памяти приложения
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        // проверяем необходимые условия
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
