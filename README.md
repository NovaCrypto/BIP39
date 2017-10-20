[![Download](https://api.bintray.com/packages/novacrypto/BIP/BIP39/images/download.svg)](https://bintray.com/novacrypto/BIP/BIP39/_latestVersion) [![Build Status](https://travis-ci.org/NovaCrypto/BIP39.svg?branch=master)](https://travis-ci.org/NovaCrypto/BIP39) [![codecov](https://codecov.io/gh/NovaCrypto/BIP39/branch/master/graph/badge.svg)](https://codecov.io/gh/NovaCrypto/BIP39)

Read all about how I wrote this and understanding BIP39 [here](https://medium.com/@_west_on/coding-a-bip39-microlibrary-in-java-bb90c1109123).

Apart from generating a seed, only English, French and Japanese [currently packaged](https://github.com/NovaCrypto/BIP39/issues/1), but as `WordList` is an interface and you can provide your own.

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
    compile 'io.github.novacrypto:BIP39:0.1.2@jar'
}

```

# Usage

## Generate a mnemonic

Using a `StringBuilder`:

```
StringBuilder sb = new StringBuilder();
byte[] entropy = new byte[128 / 8];
new SecureRandom().nextBytes(entropy);
new MnemonicGenerator(English.INSTANCE)
    .createMnemonic(entropy, sb::append);
System.out.println(sb.toString());
```

If you're paranoid and/or need higher than normal [memory security](https://medium.com/@_west_on/protecting-strings-in-jvm-memory-84c365f8f01c), consider using a [`SecureCharBuffer`](https://github.com/NovaCrypto/SecureString):

```
try (SecureCharBuffer secure = new SecureCharBuffer()) {
    final byte[] entropy = new byte[128 / 8];
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
} catch (InvalidChecksumException e) {
   ...
} catch (InvalidWordCountException e) {
    ...
} catch (WordNotFoundException e) {
    ...
    //e.getSuggestion1()
    //e.getSuggestion2()
}
```

## Generate a seed

As does not use a word list, can be used now for any language.

```
byte[] seed = new SeedCalculator().calculateSeed(mnemonic, passphrase);
```
