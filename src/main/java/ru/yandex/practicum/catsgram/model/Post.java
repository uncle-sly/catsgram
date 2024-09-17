package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
public class Post {

    private Long id;
    private long authorId;
    private String description;
    private LocalDate postDate;

}
