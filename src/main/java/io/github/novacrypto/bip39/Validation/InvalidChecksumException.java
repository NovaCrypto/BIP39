package io.github.novacrypto.bip39.Validation;

/**
 * Created by aevans on 2017-10-08.
 */
public final class InvalidChecksumException extends Exception {
    public InvalidChecksumException() {
        super("Invalid checksum");
    }
}