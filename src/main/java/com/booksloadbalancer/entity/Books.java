package com.booksloadbalancer.entity;



import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class Books {
public String isbn;
	
	public String title;
	
	public String publishedDate;
	
	public long totalCopies;
	
	public long issuedCopies;
	
	public String author;
}
