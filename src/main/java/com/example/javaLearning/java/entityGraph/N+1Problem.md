# N + 1 Problem

The N+1 query problem occurs when your application makes 1 query to fetch N entities, and then makes N additional queries to fetch related data for each entity. This leads to poor performance as the number of database calls grows linearly with the number of entities.


```java
@Service
public class AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;
    
    // This method causes N+1 query problem
    public List<Author> getAllAuthorsWithBooks() {
        List<Author> authors = authorRepository.findAll(); // 1 query to get all authors
        
        // For each author, Hibernate makes a separate query to fetch books
        // This is the N+1 problem!
        for (Author author : authors) {
            // This triggers lazy loading for each author
            List<Book> books = author.getBooks(); // N queries (one for each author)
            System.out.println("Author: " + author.getName() + 
                             ", Books: " + books.size());
        }
        
        return authors;
    }
}
```

#### What Happens?

If you have 100 authors:

* 1 query to fetch all authors: SELECT * FROM authors
* 100 additional queries to fetch books for each author: SELECT * FROM books WHERE author_id = ?  

Total: 101 queries instead of just 1 or 2!


## Solutions to Fix N+1 Problem

1. Solution 1: Use JOIN FETCH in Repository
```java
   public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    @Query("SELECT a FROM Author a JOIN FETCH a.books")
    List<Author> findAllWithBooks();
}

// Updated service method
public List<Author> getAllAuthorsWithBooksFixed() {
return authorRepository.findAllWithBooks(); // Single query with JOIN
}
```


2. Solution 2: Use EntityGraph

```java
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    @EntityGraph(attributePaths = "books")
    @Query("SELECT a FROM Author a")
    List<Author> findAllWithBooks();
}
```

3. Solution 3: Use @NamedEntityGraph
```java
@Entity
@Table(name = "authors")
@NamedEntityGraph(
    name = "Author.books",
    attributeNodes = @NamedAttributeNode("books")
)
public class Author {
    // ... same as before
}

public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    @EntityGraph("Author.books")
    List<Author> findAll();
}
```

4. Solution 4: Use DTO Projection

```java
@Entity
@Table(name = "authors")
@NamedEntityGraph(
    name = "Author.books",
    attributeNodes = @NamedAttributeNode("books")
)
public class Author {
    // ... same as before
}

public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    @EntityGraph("Author.books")
    List<Author> findAll();
}
```


