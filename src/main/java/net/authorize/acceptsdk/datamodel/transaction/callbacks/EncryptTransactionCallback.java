package net.authorize.acceptsdk.datamodel.transaction.callbacks;

import net.authorize.acceptsdk.datamodel.transaction.response.EncryptTransactionResponse;
import net.authorize.acceptsdk.datamodel.transaction.response.ErrorTransactionResponse;

/**
 * Callback Interface for Encrypt Transaction
 * <p>
 * Created by Kiran Bollepalli on 07,July,2016.
 * kbollepa@visa.com
 */
public interface EncryptTransactionCallback {

    /**
     * Called when an error occurred caused by sending/receiving a request. It
     * might be an error returned by the gateway as well as by the gateway
     * driver (e.g connection problems, missing request parameters)
     *
     * @param error {@link ErrorTransactionResponse} error
     */
    void onErrorReceived(ErrorTransactionResponse error);

    /**
     * Called when transaction request completed.
     */
    void onEncryptionFinished(EncryptTransactionResponse response);
}
