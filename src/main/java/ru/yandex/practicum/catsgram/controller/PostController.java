package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;
import ru.yandex.practicum.catsgram.service.SortOrder;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {
    PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(defaultValue = "asc") String sort,
            @RequestParam (defaultValue = "10") Long size,
            @RequestParam (defaultValue = "0") Long from
    ) {
        String order = SortOrder.from(sort);

        return postService.findAll(size, String.valueOf(order), from);
    }

    @GetMapping("{id}")
    public Post getPostById(@PathVariable Long id) {
       return postService.getPostById(id);
    }

    //@RequestBody означает, что значение, которое будет передано в метод в качестве аргумента, нужно взять из тела запроса.
    //При этом объект, который пришёл в теле запроса, например, в виде JSON, будет автоматически десериализован в Java-объект.
    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }



}
