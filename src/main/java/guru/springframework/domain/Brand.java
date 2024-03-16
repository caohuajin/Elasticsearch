package guru.springframework.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "brands")
public class Brand {
    @Id
    private String id;
    private String name;
    private String country;
}