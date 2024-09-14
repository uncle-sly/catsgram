package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {
    PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping
    public Collection<Post> findAll() {
        return postService.findAll();
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
