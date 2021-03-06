package io.contentos.sdk;

import java.io.File;
import java.util.List;

import io.contentos.sdk.keystore.InMemoryKeyStore;
import io.contentos.sdk.keystore.KeyStore;
import io.contentos.sdk.keystore.KeystoreAPI;
import io.contentos.sdk.grpc.ApiServiceGrpc;
import io.contentos.sdk.rpc.RpcClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public final class Wallet extends RpcClient implements KeystoreAPI {

    private final ManagedChannel channel;
    private KeystoreAPI keyStore = new InMemoryKeyStore();

    /**
     * Wallet constructor.
     * @param serverHost    server host
     * @param serverPort    server port
     * @param secure        use TLS or not
     */
    public Wallet(String serverHost, int serverPort, String chainName, boolean secure) {
        super(ApiServiceGrpc.newBlockingStub(channelBuilder(serverHost, serverPort, secure).build()), chainName);
        channel = (ManagedChannel) service.getChannel();
    }

    /**
     * Wallet constructor.
     * @param serverHost    server host
     * @param serverPort    server port
     */
    public Wallet(String serverHost, int serverPort, String chainName) {
        this(serverHost, serverPort, chainName, false);
    }

    private static ManagedChannelBuilder<?> channelBuilder(String serverHost, int serverPort, boolean secure) {
        ManagedChannelBuilder<?> cb = ManagedChannelBuilder.forAddress(serverHost, serverPort)
                .userAgent("");
        if (!secure) {
            cb = cb.usePlaintext();
        }
        return cb;
    }

    /**
     * Close the wallet.
     */
    public void close() {
        try {
            channel.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load accounts and their keys from specific keystore file.
     * @param file      keystore file
     * @param password  password for keystore encryption/decryption
     */
    public synchronized void openKeyStore(File file, String password) {
        keyStore = KeyStore.openOrCreate(file, password);
    }

    /**
     * Create an RPC client in behalf of specific account, i.e. using account's private key for transaction signatures.
     * @param name name of account
     * @return RPC client.
     */
    public RpcClient account(String name) {
        return new RpcClient(service, getKey(name), chainName);
    }

    //
    // KeyStoreAPI implementation
    //

    public synchronized String getKey(String account) {
        if (keyStore != null) {
            return keyStore.getKey(account);
        }
        return null;
    }

    public synchronized void addKey(String account, String wifPrivateKey) {
        if (keyStore == null) {
            throw new RuntimeException("no open keystore");
        }
        keyStore.addKey(account, wifPrivateKey);
    }

    public synchronized void addKeyByMnemonic(String account, String mnemonic) {
        if (keyStore == null) {
            throw new RuntimeException("no open keystore");
        }
        keyStore.addKeyByMnemonic(account, mnemonic);
    }

    public synchronized void removeKey(String account) {
        if (keyStore == null) {
            throw new RuntimeException("no open keystore");
        }
        keyStore.removeKey(account);
    }

    public synchronized List<String> getAccounts() {
        if (keyStore == null) {
            throw new RuntimeException("no open keystore");
        }
        return keyStore.getAccounts();
    }
}
