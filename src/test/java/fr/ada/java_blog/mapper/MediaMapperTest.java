package fr.ada.java_blog.mapper;

import fr.ada.java_blog.dto.MediaResponse;
import fr.ada.java_blog.model.Media;
import fr.ada.java_blog.model.MediaType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaMapperTest {

    @Test
    void toResponse_copieIdTypeEtUrl() {
        Media media = new Media(5, MediaType.image, "https://cdn.example/img.png");

        MediaResponse response = MediaMapper.toResponse(media);

        assertEquals(5, response.id());
        assertEquals(MediaType.image, response.type());
        assertEquals("https://cdn.example/img.png", response.url());
    }
}
