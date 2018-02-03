[![Download](https://api.bintray.com/packages/novacrypto/BIP/BIP39/images/download.svg)](https://bintray.com/novacrypto/BIP/BIP39/_latestVersion) [![Build Status](https://travis-ci.org/NovaCrypto/BIP39.svg?branch=master)](https://travis-ci.org/NovaCrypto/BIP39) [![codecov](https://codecov.io/gh/NovaCrypto/BIP39/branch/master/graph/badge.svg)](https://codecov.io/gh/NovaCrypto/BIP39)

Read all about how I wrote this and understanding BIP39 [here](https://medium.com/@_west_on/coding-a-bip39-microlibrary-in-java-bb90c1109123).

Apart from generating a seed, only English, French, Spanish and Japanese [currently packaged](https://github.com/NovaCrypto/BIP39/issues/1), but as `WordList` is an interface and you can provide your own.

# Install

Use either of these repositories:

```
repositories {
    jcenter()
}
```

Or:

```
repositories {
    maven {
        url 'https://dl.bintray.com/novacrypto/BIP/'
    }
}
```

Add dependency:

```
dependencies {
    compile 'io.github.novacrypto:BIP39:0.1.9'
}

```

# Usage

## Generate a mnemonic

Using a `StringBuilder`:

```
StringBuilder sb = new StringBuilder();
byte[] entropy = new byte[Words.TWELVE.byteLength()];
new SecureRandom().nextBytes(entropy);
new MnemonicGenerator(English.INSTANCE)
    .createMnemonic(entropy, sb::append);
System.out.println(sb.toString());
```

If you're paranoid and/or need higher than normal [memory security](https://medium.com/@_west_on/protecting-strings-in-jvm-memory-84c365f8f01c), consider using a [`SecureCharBuffer`](https://github.com/NovaCrypto/SecureString):

```
try (SecureCharBuffer secure = new SecureCharBuffer()) {
    byte[] entropy = new byte[Words.TWELVE.byteLength()];
    new SecureRandom().nextBytes(entropy);
    new MnemonicGenerator(English.INSTANCE)
        .createMnemonic(entropy, secure::append);
    Arrays.fill(entropy, (byte) 0); //empty entropy
    //do something with your secure mnemonic
}
```

## Validate a mnemonic

```
try {
    MnemonicValidator
        .ofWordList(English.INSTANCE)
        .validate(mnemonic);
} catch (UnexpectedWhiteSpaceException e) {
   ...
} catch (InvalidWordCountException e) {
    ...
} catch (InvalidChecksumException e) {
     ...
} catch (WordNotFoundException e) {
    ...
    //e.getSuggestion1()
    //e.getSuggestion2()
}
```

Or if you have a list of words from a word list:

```
MnemonicValidator
        .ofWordList(English.INSTANCE)
        .validate(mnemonicWordsInAList);
```

## Generate a seed

As does not use a word list, can be used now for any language.

```
byte[] seed = new SeedCalculator().calculateSeed(mnemonic, passphrase);
```

Or if you have a list of words from a word list:

```
byte[] seed = new SeedCalculator()
                     .withWordsFromWordList(English.INSTANCE)
                     .calculateSeed(mnemonicWordsInAList, passphrase);
```

Note: it will work for words off of the word list, but it allows use of secure CharSequences if they match the wordlist, normalized or not (as they are never `toString`ed)

Those examples both use SpongyCastle, if you don't need or want that dependency, you can use `javax.crypto` like so:

```
byte[] seed = new SeedCalculator(JavaxPBKDF2WithHmacSHA256.INSTANCE).calculateSeed(mnemonic, passphrase);
```

That will not work on Android API < 26 https://developer.android.com/reference/javax/crypto/SecretKeyFactory.html and see Issue #17.

