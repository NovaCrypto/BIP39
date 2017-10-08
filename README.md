[![Build Status](https://travis-ci.org/NovaCrypto/BIP39.svg?branch=master)](https://travis-ci.org/NovaCrypto/BIP39)

[![Coverage Status](https://coveralls.io/repos/github/NovaCrypto/BIP39/badge.svg?branch=master)](https://coveralls.io/github/NovaCrypto/BIP39?branch=master)

Read all about how I wrote this and understanding BIP39 [here](https://medium.com/@_west_on/coding-a-bip39-microlibrary-in-java-bb90c1109123).

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

Generate a mnemonic.

```
StringBuffer sb = new StringBuffer();
byte[] entropy = new byte[128 / 8];
new SecureRandom().nextBytes(entropy);
new MnemonicGenerator(English.INSTANCE)
    .createMnemonic(entropy, sb::append);
System.out.println(sb.toString());
```

