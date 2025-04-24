/**
 * <p> Title: Article Class </p>
 * 
 * <p> Description: Represents an article in the help system with various attributes </p>
 * 
 * <p> Copyright: Copyright (c) 2024 </p>
 * 
 * @author William Sou
 * 
 * @version 1.0    2024-10-15    Initial implementation
 */
package core;

/**
 * This class represents an article in the help system.
 * It stores various attributes of an article such as title, authors, abstract, keywords, body, and references.
 * The class provides methods to access and modify these attributes.
 */
public class Article {
    private int id;
    private char[] title;
    private char[] authors;
    private char[] abstractBody;
    private char[] keywords;
    private char[] body;
    private char[] references;
    private char[] group;

    /**
     * Constructs a new Article with all attributes.
     * 
     * @param title The title of the article
     * @param authors The authors of the article
     * @param abstractBody The abstract of the article
     * @param keywords The keywords of the article
     * @param body The main body of the article
     * @param references The references of the article
     */
    public Article(char[] title, char[] authors, char[] abstractBody, char[] keywords
    		, char[] body, char[] references, char[] group) {
        this.title = title;
        this.authors = authors;
        this.abstractBody = abstractBody;
        this.keywords = keywords;
        this.body = body;
        this.references = references;
        this.group = group;
    }

    /**
     * Constructs a new Article with only title and authors.
     * 
     * @param title The title of the article
     * @param authors The authors of the article
     */
    public Article(char[] title, char[] authors) {
        this.title = title;
        this.authors = authors;
    }
    
    // Getters
    /**
     * @return The ID of the article
     */
    public int getId() {return id;}
    /**
     * @return The title of the article
     */
    public char[] getTitle() { return title; }
    /**
     * @return The authors of the article
     */
    public char[] getAuthors() { return authors; }
    /**
     * @return The abstract of the article
     */
    public char[] getAbstract() { return abstractBody; }
    /**
     * @return The keywords of the article
     */
    public char[] getKeywords() { return keywords; }
    /**
     * @return The main body of the article
     */
    public char[] getBody() { return body; }
    /**
     * @return The references of the article
     */
    public char[] getReferences() { return references; }
    /**
     * @return The group name of the article
     */
    public char[] getGroup() { return group; }

    // Setters
    /**
     * Sets the ID of the article
     * @param id The new ID
     */
    public void setId(int id) {this.id = id;}
    /**
     * Sets the title of the article
     * @param title The new title
     */
    public void setTitle(char[] title) { this.title = title; }
    /**
     * Sets the authors of the article
     * @param authors The new authors
     */
    public void setAuthors(char[] authors) { this.authors = authors; }
    /**
     * Sets the abstract of the article
     * @param abstract_ The new abstract
     */
    public void setAbstract(char[] abstract_) { this.abstractBody = abstract_; }
    /**
     * Sets the keywords of the article
     * @param keywords The new keywords
     */
    public void setKeywords(char[] keywords) { this.keywords = keywords; }
    /**
     * Sets the main body of the article
     * @param body The new body
     */
    public void setBody(char[] body) { this.body = body; }
    /**
     * Sets the references of the article
     * @param references The new references
     */
    public void setReferences(char[] references) { this.references = references; }
    /**
     * Sets the group of the article
     * @param references The new references
     */
    public void setGroup(char[] group) { this.group = group; }

    /**
     * Clears sensitive data by overwriting content with spaces.
     * This method is used to securely remove sensitive information from memory.
     */
    public void clearSensitiveData() {
        // Overwrite body content with spaces
        for (int i = 0; i < body.length; i++) body[i] = ' ';
        // Overwrite abstract content with spaces
        for (int i = 0; i < abstractBody.length; i++) abstractBody[i] = ' ';
        // Clear other fields as needed
    }
}