package guru.springframework.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Data
@Document(indexName = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private double price;

    @Field(type = FieldType.Nested)
    private Brand brand;

}
