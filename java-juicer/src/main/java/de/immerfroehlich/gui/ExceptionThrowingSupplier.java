package de.immerfroehlich.gui;

public interface ExceptionThrowingSupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get() throws Exception;
}