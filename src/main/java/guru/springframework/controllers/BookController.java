package guru.springframework.controllers;

import guru.springframework.domain.Book;
import guru.springframework.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;
    /**
     * 按关键字分页搜索book
     * RestHighLevelClient 实现方式
     */
    @GetMapping("/search")
    public Page<Book> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookService.searchBooksByKeyword(keyword, page, size);
    }

    /**
     * spring data elasticsearch 实现方式
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search2")
    public Page<Book> searchBooks2(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookService.searchBooksByKeyword2(keyword, page, size);
    }
}
