package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Image;

import java.util.List;
import java.util.Optional;

@Repository
public class ImageRepository extends BaseRepository<Image> {

    private static final String INSERT_IMAGE = "INSERT INTO image_storage(original_name, file_path, post_id) " +
            "VALUES (?, ?, ?) returning id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM image_storage WHERE id = ?";
    private static final String FIND_BY_POST_ID_QUERY = "SELECT * FROM image_storage where post_id = ?";
    private static final String UPDATE_IMAGE = "UPDATE image_storage SET original_name = ?, file_path = ?, post_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM image_storage WHERE id = ?";

    public ImageRepository(JdbcTemplate jdbc, RowMapper<Image> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Image> findById(long imageId) {
        return findOne(FIND_BY_ID_QUERY, imageId);
    }
    public List<Image> findByPostId(long postId) {
        return findMany(FIND_BY_POST_ID_QUERY, postId);
    }

    public boolean delete(long imageId) {
        return delete(DELETE_QUERY, imageId);
    }

    public Image save(Image image) {
        long id = insert(
                INSERT_IMAGE,
                image.getOriginalFileName(),
                image.getFilePath(),
                image.getPostId()
        );
        image.setId(id);
        return image;
    }

    public Image update(Image image) {
        update(
                UPDATE_IMAGE,
                image.getOriginalFileName(),
                image.getFilePath(),
                image.getPostId()
        );
        return image;
    }

}
