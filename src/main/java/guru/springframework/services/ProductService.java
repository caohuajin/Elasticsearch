package guru.springframework.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.domain.Product;
import guru.springframework.repositories.ProductRepository;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RestHighLevelClient elasticsearchClient;

    private final String index = "products";

    @Autowired
    private ObjectMapper objectMapper;



    public Page<Product> getAllProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return productRepository.findAll(pageable);
    }
    public Page<Product> searchProductsByBrandName(String brandName, int pageNumber, int pageSize) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("brand.name", brandName));
        sourceBuilder.from(pageNumber * pageSize);
        sourceBuilder.size(pageSize);
        searchRequest.source(sourceBuilder);

        SearchResponse response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Product> products = extractProductsFromSearchResponse(response);

        return new PageImpl<>(products, PageRequest.of(pageNumber, pageSize), response.getHits().getTotalHits().value);
    }

    private List<Product> extractProductsFromSearchResponse(SearchResponse response) {
        return Arrays.stream(response.getHits().getHits())
                .map(hit -> {
                    try {
                        return objectMapper.readValue(hit.getSourceAsString(), Product.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to parse search response", e);
                    }
                })
                .collect(Collectors.toList());
    }

}
