package guru.springframework.repositories;

import guru.springframework.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookRepository extends ElasticsearchRepository<Book, String> {
    Page<Book> findByTitleContainingOrAuthorContaining(String title, String author, Pageable pageable);
    @Query("{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"author\": \"?0\"}}]}}")
    Page<Book> findByTitleOrAuthorCustomQuery(String keyword, Pageable pageable);
}
