package io.contentos.sdk.keystore;

import io.contentos.sdk.crypto.Key;
import io.contentos.sdk.encoding.WIF;
import io.contentos.sdk.prototype.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryKeyStore implements KeystoreAPI {
    private final HashMap<String, String> keys = new HashMap<>();

    @Override
    public synchronized String getKey(String account) {
        return keys.get(account);
    }

    @Override
    public synchronized void addKey(String account, String wifPrivateKey) {
        keys.put(account, wifPrivateKey);
    }

    @Override
    public synchronized void addKeyByMnemonic(String account, String mnemonic) {
        Type.private_key_type privateKey = Key.generateFromMnemonic(mnemonic);
        String wifPrivateKey = WIF.fromPrivateKey(privateKey);
        keys.put(account, wifPrivateKey);
    }

    @Override
    public synchronized void removeKey(String account) {
        keys.remove(account);
    }

    @Override
    public synchronized List<String> getAccounts() {
        return new ArrayList<>(keys.keySet());
    }
}
