package guru.springframework.services;

import guru.springframework.domain.Book;
import guru.springframework.repositories.BookRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BookService {
    @Autowired
    private RestHighLevelClient elasticsearchClient;
    @Autowired
    private BookRepository bookRepository;
    private final String index = "library";

    public Page<Book> searchBooksByKeyword(String keyword, int page, int size) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title", keyword))
                .should(QueryBuilders.matchQuery("author", keyword));
        sourceBuilder.query(boolQuery);
        sourceBuilder.from(page * size);
        sourceBuilder.size(size);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
            List<Book> books = extractBooksFromSearchResponse(response);
            long totalHits = response.getHits().getTotalHits().value;
            return new PageImpl<>(books, PageRequest.of(page, size), totalHits);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Book> extractBooksFromSearchResponse(SearchResponse response) {
        List<Book> books = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String title = (String) sourceAsMap.get("title");
            String author = (String) sourceAsMap.get("author");
            // 构造Book对象
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            books.add(book);
        }
        return books;
    }

    public Page<Book> searchBooksByKeyword2(String keyword, int page, int size) {
       // return bookRepository.findByTitleContainingOrAuthorContaining(keyword,keyword,PageRequest.of(page, size));
        return bookRepository.findByTitleOrAuthorCustomQuery(keyword,PageRequest.of(page, size));
    }
    public GetIndexResponse getIndex() throws IOException {
        // 定义索引名称
        GetIndexRequest request = new GetIndexRequest(index);
        // 发送请求到ES
        GetIndexResponse response = elasticsearchClient.indices().get(request, RequestOptions.DEFAULT);
        // 处理响应结果
        System.out.println("aliases：" + response.getAliases());
        System.out.println("mappings：" + response.getMappings());
        System.out.println("settings：" + response.getSettings());
        return response;
    }
}
