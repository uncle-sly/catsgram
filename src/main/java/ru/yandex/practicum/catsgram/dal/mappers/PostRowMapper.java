package ru.yandex.practicum.catsgram.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class PostRowMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setDescription(resultSet.getString("description"));

        Timestamp postDate = resultSet.getTimestamp("post_date");
        post.setPostDate(postDate.toInstant());

        User author = new User();
        author.setId(resultSet.getLong("author_id"));
        post.setAuthor(author);

        return post;
    }
}