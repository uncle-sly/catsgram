package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.*;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostMapper {
    public static Post mapToPost(NewPostRequest request, User author) {
        Post post = new Post();
        post.setDescription(request.getDescription());
        post.setAuthor(author);
        post.setPostDate(Instant.now());

        return post;
    }

    public static PostDto mapToPostDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setDescription(post.getDescription());
        postDto.setPostDate(post.getPostDate());

        User author = post.getAuthor();
        postDto.setAuthor(UserMapper.mapToUserDto(author));

        if (!post.getImages().isEmpty()) {
            List<Long> imageIds = post.getImages().stream()
                    .map(Image::getId)
                    .toList();
        postDto.setImages(imageIds);
        }
        return postDto;
    }

    public static Post updatePostFields(Post post, UpdatePostRequest request) {
        if (request.hasDescription()) {
            post.setDescription(request.getDescription());
        }

        return post;
    }

}



