package io.github.novacrypto.bip39;

/**
 * Created by aevans on 2017-10-08.
 */
public final class WordNotFoundException extends RuntimeException {
    public WordNotFoundException(CharSequence word, CharSequence closest1, CharSequence closest2) {
        super(String.format(
                "Word not found in word list \"%s\", suggestions \"%s\", \"%s\"",
                word,
                closest1,
                closest2));
    }
}
