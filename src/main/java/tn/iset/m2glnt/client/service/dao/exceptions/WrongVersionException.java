package tn.iset.m2glnt.client.service.dao.exceptions;

/**
 * Concurrency issue on the version number
 */
public class WrongVersionException extends Exception {

    /**
     * Constructor
     * @param message the error message
     */
    public WrongVersionException(String message) {
        super(message);
    }

}
