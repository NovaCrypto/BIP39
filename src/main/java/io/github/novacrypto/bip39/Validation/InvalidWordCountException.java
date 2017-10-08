package io.github.novacrypto.bip39.Validation;

/**
 * Created by aevans on 2017-10-08.
 */
public final class InvalidWordCountException extends Exception {
    public InvalidWordCountException() {
        super("Not a correct number of words");
    }
}