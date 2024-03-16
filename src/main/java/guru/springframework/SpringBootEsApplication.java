package guru.springframework;

import guru.springframework.domain.Book;
import guru.springframework.domain.Brand;
import guru.springframework.domain.Product;
import guru.springframework.repositories.BookRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.core.DocumentOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@SpringBootApplication
public class SpringBootEsApplication {

	@Autowired
	private DocumentOperations documentOperations;

		private final BookRepository bookRepository;

		@Autowired
		private ElasticsearchOperations elasticsearchOperations;

    	public SpringBootEsApplication(BookRepository bookRepository) {
			this.bookRepository = bookRepository;
		}

		public static void main(String[] args) {
			SpringApplication.run(SpringBootEsApplication.class, args);
		}

		@PostConstruct
		private void init() {
			// 创建文档
			Book book1 = new Book("1", "Java Programming Smith", "John Doe");
			Book book2 = new Book("2", "Spring Boot in Action", "Jane Smith");
			Book book3 = new Book("3", "Spring Boot es", "cao Smith");
			Book book4 = new Book("4", "Spring Boot mongodb Smith", "lili Smith");

			bookRepository.save(book1);
			bookRepository.save(book2);
			bookRepository.save(book3);
			bookRepository.save(book4);


			// 创建 Brand 对象
			Brand brand = new Brand();
			brand.setId("1");
			brand.setName("华为");
			brand.setCountry("中国");

			// 创建 Product 对象并设置嵌套的 Brand 对象
			Product product = new Product();
			product.setId("1");
			product.setName("手机");
			product.setPrice(1001.0);
			product.setBrand(brand);

			// 保存 Product 对象到 Elasticsearch 中
			elasticsearchOperations.save(product);
		}
}
