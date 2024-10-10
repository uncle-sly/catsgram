package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.service.PostService;
import ru.yandex.practicum.catsgram.service.SortOrder;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostDto> findAll(
            @RequestParam(defaultValue = "asc") String sort,
            @RequestParam (defaultValue = "10") Long size,
            @RequestParam (defaultValue = "0") Long from
    ) {
        if ("null".equals(sort)) {
            throw new ParameterNotValidException("sort", "Получено: " + sort + " Должно быть asc или desc");
        }
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Размер должен быть больше нуля");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from", "Начало выборки должно быть положительным числом");
        }
        String order = SortOrder.from(sort);
        return postService.findAll(size, String.valueOf(order), from);
    }

    @GetMapping("{id}")
    public PostDto getPostById(@PathVariable Long id) {
       return postService.getPostById(id);
    }

    //@RequestBody означает, что значение, которое будет передано в метод в качестве аргумента, нужно взять из тела запроса.
    //При этом объект, который пришёл в теле запроса, например, в виде JSON, будет автоматически десериализован в Java-объект.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto create(@RequestBody NewPostRequest request) {
        return postService.create(request);
    }

    @PutMapping("{postId}")
    public PostDto update(@PathVariable long postId, @RequestBody NewPostRequest request) {
        return postService.update(postId, request);
    }


}
