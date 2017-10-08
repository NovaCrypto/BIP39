[![Build Status](https://travis-ci.org/NovaCrypto/BIP39.svg?branch=master)](https://travis-ci.org/NovaCrypto/BIP39)

[![codecov](https://codecov.io/gh/NovaCrypto/BIP39/branch/master/graph/badge.svg)](https://codecov.io/gh/NovaCrypto/BIP39)

Read all about how I wrote this and understanding BIP39 [here](https://medium.com/@_west_on/coding-a-bip39-microlibrary-in-java-bb90c1109123).

Apart from generating a seed, only English and Japanese [currently supported](https://github.com/NovaCrypto/BIP39/issues/1).

# Install

```
repositories {
    maven {
        url 'https://dl.bintray.com/novacrypto/BIP/'
    }
}
```

`jcenter` coming soon.

Add dependency:

```
dependencies {
    compile 'io.github.novacrypto:BIP39:0.1.0@jar'
}

```

# Usage

## Generate a mnemonic

```
StringBuffer sb = new StringBuffer();
byte[] entropy = new byte[128 / 8];
new SecureRandom().nextBytes(entropy);
new MnemonicGenerator(English.INSTANCE)
    .createMnemonic(entropy, sb::append);
System.out.println(sb.toString());
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
