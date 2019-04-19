package com.sst.nt.lms.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sst.nt.lms.admin.exception.RetrieveException;
import com.sst.nt.lms.admin.exception.TransactionException;
import com.sst.nt.lms.admin.model.Author;
import com.sst.nt.lms.admin.model.Book;
import com.sst.nt.lms.admin.model.Publisher;
import com.sst.nt.lms.admin.service.AdministratorService;

/**
 * Controller for cataloging administrators.
 *
 * <p>FIXME: Limit access to most of these endpoints to authorized users
 *
 * <p>FIXME: Commit after making changes to the database.
 * @author Jonathan Lovelace
 */
@RestController
public final class CatalogController {
	/**
	 * Service class used to handle requests.
	 */
	@Autowired
	private AdministratorService service;

	/**
	 * Get all authors from the database. Spring turns the list into JSON (or XML?).
	 * @return the list of all authors in the database.
	 * @throws TransactionException on internal error.
	 */
	@GetMapping({"/authors", "/authors/"})
	public List<Author> getAuthors() throws TransactionException {
		return service.getAllAuthors();
	}

	/**
	 * Get all books from the database. Spring turns the list into JSON (or XML?).
	 * @return the list of all books in the database
	 * @throws TransactionException on internal error
	 */
	@GetMapping({"/books", "/books/"})
	public List<Book> getBooks() throws TransactionException {
		return service.getAllBooks();
	}

	/**
	 * Get all publishers from the database. Spring turns the list into JSON (or XML?).
	 * @return the list of all publishers in the database
	 * @throws TransactionException on internal error
	 */
	@GetMapping({"/publishers","/publishers/"})
	public List<Publisher> getPublishers() throws TransactionException {
		return service.getAllPublishers();
	}

	/**
	 * Get an author by its ID number.
	 * @param authorId the ID number of the author
	 * @return the author
	 * @throws TransactionException if author not found, or on internal error
	 */
	@GetMapping({"/author/{authorId}", "/author/{authorId}/"})
	public Author getAuthor(@PathVariable("authorId") final int authorId) throws TransactionException {
		final Author author = service.getAuthor(authorId);
		if (author == null) {
			throw new RetrieveException("Author not found");
		} else {
			return author;
		}
	}

	/**
	 * Get a book by its ID number.
	 * @param bookId the ID number of the book
	 * @return the book
	 * @throws TransactionException if book not found, or on internal error
	 */
	@GetMapping({"/book/{bookId}","/book/{bookId}/"})
	public Book getBook(@PathVariable("bookId") final int bookId) throws TransactionException {
		final Book book = service.getBook(bookId);
		if (book == null) {
			throw new RetrieveException("Book not found");
		} else {
			return book;
		}
	}

	/**
	 * Get a publisher by ID number.
	 * @param publisherId the ID number of the publisher
	 * @return the publisher
	 * @throws TransactionException if publisher not found, or on internal error
	 */
	@GetMapping({"/publisher/{publisherId}", "/publisher/{publisherId}/"})
	public Publisher getPublisher(@PathVariable("publisherId") final int publisherId)
			throws TransactionException {
		final Publisher publisher = service.getPublisher(publisherId);
		if (publisher == null) {
			throw new RetrieveException("Publisher not found");
		} else {
			return publisher;
		}
	}

	/**
	 * Update an author by ID number.
	 * @param authorId the ID number of the author to update
	 * @param input the author data to update.
	 * @return the updated author
	 * @throws TransactionException if author not found, or on internal error
	 */
	@PutMapping({ "/author/{authorId}", "/author/{authorId}/" })
	public Author updateAuthor(@PathVariable("authorId") final int authorId,
			@RequestBody final Author input) throws TransactionException {
		final Author author = service.getAuthor(authorId);
		if (author == null) {
			throw new RetrieveException("Author not found");
		} else {
			author.setName(input.getName());
			service.updateAuthor(author);
			return service.getAuthor(authorId);
		}
	}

	/**
	 * Update a publisher by ID number.
	 * @param publisherId the ID number of the publisher to update
	 * @param input the publisher data to update.
	 * @return the updated publisher
	 * @throws TransactionException if publisher not found, or on internal error
	 */
	@PutMapping({ "/publisher/{publisherId}", "/publisher/{publisherId}/" })
	public Publisher updatePublisher(
			@PathVariable("publisherId") final int publisherId,
			@RequestBody final Publisher input) throws TransactionException {
		final Publisher publisher = service.getPublisher(publisherId);
		if (publisher == null) {
			throw new RetrieveException("Publisher not found");
		} else {
			publisher.setName(input.getName());
			publisher.setAddress(input.getAddress());
			publisher.setPhone(input.getPhone());
			service.updatePublisher(publisher);
			return service.getPublisher(publisherId);
		}
	}

	/**
	 * Update a book by ID number. If author or publisher is null in the supplied
	 * data, the existing author or publisher is left alone. If the author or
	 * publisher has an ID that is not found in the database, an exception is
	 * thrown; otherwise, the existing author and publisher are used (changes to
	 * that data in the input are ignored).
	 *
	 * @param bookId the ID number of the book to update
	 * @param input  the book data to update.
	 * @return the updated book
	 * @throws TransactionException if the book is not found, or on internal error.
	 */
	@PutMapping({"/book/{bookId}", "/book/{bookId}/"})
	public Book updateBook(@PathVariable("bookId") final int bookId,
			@RequestBody final Book input) throws TransactionException {
		final Book book = service.getBook(bookId);
		if (book == null) {
			throw new RetrieveException("Book not found");
		} else {
			final Author author = input.getAuthor();
			if (author != null) {
				final Author dbAuthor = service.getAuthor(author.getId());
				if (dbAuthor == null) {
					throw new RetrieveException("Author not found");
				} else {
					book.setAuthor(dbAuthor);
				}
			}
			final Publisher publisher = input.getPublisher();
			if (publisher != null) {
				final Publisher dbPublisher = service.getPublisher(publisher.getId());
				if (dbPublisher == null) {
					throw new RetrieveException("Publisher not found");
				} else {
					book.setPublisher(dbPublisher);
				}
			}
			book.setTitle(input.getTitle());
			service.updateBook(book);
			return service.getBook(bookId);
		}
	}

	/**
	 * Create an author with the given name.
	 * @param name the name to give the author
	 * @return the created author
	 * @throws TransactionException on internal error
	 */
	@PostMapping({ "/author", "/author/" })
	public Author createAuthor(@RequestParam("name") final String name)
			throws TransactionException {
		return service.createAuthor(name);
	}
	/**
	 * Create a publisher with the specified parameters.
	 * @param name the name to give the publisher
	 * @param address the address to give the publisher
	 * @param phone the phone number to give the publisher
	 * @throws TransactionException on internal error
	 */
	@PostMapping({"/publisher", "/publisher/"})
	public Publisher createPublisher(@RequestParam("name") final String name,
			@RequestParam(name = "address", defaultValue = "") final String address,
			@RequestParam(name = "phone", defaultValue = "") final String phone)
			throws TransactionException {
		return service.createPublisher(name, address, phone);
	}

	/**
	 * Create a book with the specified parameters. If an author or publisher with
	 * the specified IDs do not exist, they are created, but differences in author or publisher state are otherwise not applied.
	 * @param title the title to give the book
	 * @param author the author to assign the book to
	 * @param publisher the publisher to assign the book to
	 */
	@PostMapping({"/book", "/book/"})
	public Book createBook(@RequestParam("title") final String title,
			@RequestParam(name = "author", required = false) final Author author,
			@RequestParam(name = "publisher", required = false) final Publisher publisher)
			throws TransactionException {
		Author actualAuthor;
		if (author == null) {
			actualAuthor = null;
		} else {
			final Author dbAuthor = service.getAuthor(author.getId());
			if (dbAuthor == null) {
				actualAuthor = service.createAuthor(author.getName());
			} else {
				actualAuthor = dbAuthor;
			}
		}
		Publisher actualPublisher;
		if (publisher == null) {
			actualPublisher = null;
		} else {
			final Publisher dbPublisher = service.getPublisher(publisher.getId());
			if (dbPublisher == null) {
				actualPublisher = service.createPublisher(publisher.getName(),
						publisher.getAddress(), publisher.getPhone());
			} else {
				actualPublisher = dbPublisher;
			}
		}
		return service.createBook(title, actualAuthor, actualPublisher);
	}

	/**
	 * Delete the author with the given ID.
	 * @param authorId the ID of the author to delete.
	 * @throws TransactionException on internal error
	 */
	@DeleteMapping({"/author/{authorId}", "/author/{authorId}/"})
	public void deleteAuthor(@PathVariable("authorId") final int authorId) throws TransactionException {
		final Author author = service.getAuthor(authorId);
		if (author != null) {
			service.deleteAuthor(author);
		}
	}

	/**
	 * Delete the publisher with the given ID.
	 * @param publisherId the ID of the publisher to delete
	 * @throws TransactionException on internal error
	 */
	@DeleteMapping({ "/publisher/{publisherId}", "/publisher/{publisherId}/" })
	public void deletePublisher(@PathVariable("publisherId") final int publisherId)
			throws TransactionException {
		final Publisher publisher = service.getPublisher(publisherId);
		if (publisher != null) {
			service.deletePublisher(publisher);
		}
	}

	/**
	 * Delete the book with the given ID.
	 * @param bookId the ID of the book to delete
	 * @throws TransactionException on internal error
	 */
	@DeleteMapping({ "/book/{bookId}", "/book/{bookId}/" })
	public void deleteBook(@PathVariable("bookId") final int bookId)
			throws TransactionException {
		final Book book = service.getBook(bookId);
		if (book != null) {
			service.deleteBook(book);
		}
	}
}