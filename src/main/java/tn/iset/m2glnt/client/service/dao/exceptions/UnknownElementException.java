package tn.iset.m2glnt.client.service.dao.exceptions;

/**
 * Exception that can occur during an update / delete / get
 */
public class UnknownElementException extends Exception {

    /**
     * The unknown element in the database
     */
    private final Object element;

    /**
     * Constructor of the exception
     * @param message the message
     * @param element the involved element
     */
    public UnknownElementException(String message, Object element) {
        super(message);
        this.element = element;
    }

    /**
     * The implied element
     * @return an element
     */
    public Object getElement() {
        return element;
    }
}
