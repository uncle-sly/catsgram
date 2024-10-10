package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository extends BaseRepository<Post> {

    private final static String FIND_ALL_POSTS = "select * from public.posts";
    private static final String FIND_BY_ID_POST = "SELECT * FROM public.posts WHERE id = ?";
    private static final String INSERT_POST = "INSERT INTO public.posts(author_id, description, post_date)" +
            "VALUES (?, ?, ?) returning id";
    private static final String UPDATE_POST = "UPDATE public.posts SET description = ?, post_date = ? WHERE id = ?";
    private static final String DELETE_BY_ID_POST = "delete FROM public.posts WHERE id = ?";

    // Инициализируем репозиторий
    public PostRepository (JdbcTemplate jdbc, RowMapper<Post> mapper) {
        super(jdbc, mapper);
    }

    public List<Post> findAll() {
        return findMany(FIND_ALL_POSTS);
    }

    public Optional<Post> findById (long id) {
        return findOne(FIND_BY_ID_POST, id);
    }
    public boolean deleteById (long id) {
        return delete(DELETE_BY_ID_POST, id);
    }

    public Post save(Post post) {
        long id = insert(
                INSERT_POST,
                post.getAuthor().getId(),
                post.getDescription(),
                Timestamp.from(post.getPostDate())
             //   Timestamp.from(Instant.from(post.getPostDate()))
        );
        post.setId(id);
        return post;
    }

    public Post update(Post post) {
        update(
                UPDATE_POST,
                post.getDescription(),
                Timestamp.from(post.getPostDate()),
                post.getId()
                //post.getAuthor(),
        );
        return post;
    }

}
