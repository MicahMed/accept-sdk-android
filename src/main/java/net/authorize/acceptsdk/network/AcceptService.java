package net.authorize.acceptsdk.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Xml;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.net.ssl.HttpsURLConnection;
import net.authorize.acceptsdk.datamodel.error.SDKErrorCode;
import net.authorize.acceptsdk.datamodel.transaction.EncryptTransactionObject;
import net.authorize.acceptsdk.datamodel.transaction.response.EncryptTransactionResponse;
import net.authorize.acceptsdk.datamodel.transaction.response.ErrorTransactionResponse;
import net.authorize.acceptsdk.datamodel.transaction.response.TransactionResponse;
import net.authorize.acceptsdk.parser.AcceptSDKParser;
import net.authorize.acceptsdk.util.LogUtil;
import net.authorize.acceptsdk.util.SDKUtils;
import org.json.JSONException;

import static net.authorize.acceptsdk.parser.JSONConstants.ResultCode;
import static net.authorize.acceptsdk.util.LogUtil.LOG_LEVEL;

/**
 * Handling asynchronous task requests in
 * a service on a separate handler thread for Accept
 * <p>
 * Created by Kiran Bollepalli on 07,July,2016.
 * kbollepa@visa.com
 */
public class AcceptService extends IntentService {

    private final static String POST = "POST";

    public static final String ACTION_ENCRYPT = "net.authorize.action.ENCRYPT";

    private static final String EXTRA_PARAM_TRANSACTION_OBJECT =
            "net.authorize.extra.TRANSACTION_OBJECT";
    private static final String EXTRA_PARAM_RESULT_RECEIVER = "net.authorize.extra.RESULT_RECEIVER";

    public static final String SERVICE_RESULT_RESPONSE_KEY = "SERVICE_RESULT_RESPONSE_KEY";
    public static final String SERVICE_RESULT_ERROR_KEY = "SERVICE_RESULT_ERROR_KEY";
    public static final int SERVICE_RESULT_CODE_SDK_RESPONSE = 100;
    public static final int SERVICE_RESULT_CODE_SDK_ERROR = 200;

    /**
     * Starts this service to perform action ENCRYPT with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @param context Activity context
     * @param transactionObject - Envelope that will be send to Gateway
     * @param resultReceiver - result receiver to notify the gateway when the service has a result
     * @see IntentService
     */
    public static void startActionEncrypt(Context context,
                                          final EncryptTransactionObject transactionObject, TransactionResultReceiver resultReceiver) {
        Intent intent = new Intent(context, AcceptService.class);
        intent.setAction(ACTION_ENCRYPT);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_PARAM_TRANSACTION_OBJECT, transactionObject);
        bundle.putParcelable(EXTRA_PARAM_RESULT_RECEIVER, resultReceiver);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    public AcceptService() {
        super("InAppConnectionService");
    }

    @Override protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_ENCRYPT.equals(action)) {
                final EncryptTransactionObject transactionObject =
                        (EncryptTransactionObject) intent.getSerializableExtra(
                                EXTRA_PARAM_TRANSACTION_OBJECT);
                final ResultReceiver resultReceiver =
                        intent.getParcelableExtra(EXTRA_PARAM_RESULT_RECEIVER);
                Object result = handleActionEncrypt(transactionObject);
                onPostHandleAction(result, resultReceiver);
            }
        }
    }

    /**
     * Handles action Encrypt in the provided background thread with the provided
     * parameters.
     */
    private Object handleActionEncrypt(EncryptTransactionObject transactionObject) {
        Object resultObject = null;
        String url = ConnectionData.getActiveEndPointUrl();
        try {

            HttpsURLConnection urlConnection = SDKUtils.getHttpsURLConnection(url, POST, true);
            urlConnection.setRequestProperty(ConnectionData.CONTENT_TYPE_LABEL,
                    ConnectionData.CONTENT_TYPE_APPLICATION_JSON);
            urlConnection.setConnectTimeout(ConnectionData.getConnectionTimeout());

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer =
                    new BufferedWriter(new OutputStreamWriter(os, Xml.Encoding.UTF_8.name()));
            writer.write(
                    AcceptSDKParser.getOrderedJsonFromEncryptTransaction(transactionObject)); //Json data
            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK
                    || responseCode == HttpsURLConnection.HTTP_CREATED) {

                String responseString = SDKUtils.convertStreamToString(urlConnection.getInputStream());
                LogUtil.log(LOG_LEVEL.INFO, " response string :" + responseString);
                TransactionResponse response =
                        AcceptSDKParser.createEncryptionTransactionResponse(responseString);
                /* COMMENT: Check Result code.
                 *   > If it is "Ok" that means transaction is successful.
                 *   > If it is "Error" that means transaction is failed.
                 */
                if (response.getResultCode().equals(ResultCode.OK)) {
                    resultObject = response;
                } else { //Error case
                    resultObject = response;
                }
            } else if (responseCode == HttpsURLConnection.HTTP_INTERNAL_ERROR) {
                resultObject =
                        ErrorTransactionResponse.createErrorResponse(SDKErrorCode.E_WC_02.getErrorCode(),
                                urlConnection.getErrorStream());
            } else {
                //COMMENT: Handles other HTTP errors.
                resultObject = ErrorTransactionResponse.createErrorResponse(SDKErrorCode.E_WC_02);
            }
        } catch (IOException e) {
            resultObject =
                    ErrorTransactionResponse.createErrorResponse(SDKErrorCode.E_WC_02, e.getMessage());
        } catch (JSONException e) {
            resultObject =
                    ErrorTransactionResponse.createErrorResponse(SDKErrorCode.E_WC_14, e.getMessage());
        }
        return resultObject;
    }

    protected void onPostHandleAction(Object result, ResultReceiver resultReceiver) {
        Bundle resultData = new Bundle();
        if (result instanceof EncryptTransactionResponse response) {
            resultData.putParcelable(SERVICE_RESULT_RESPONSE_KEY, response);
            resultReceiver.send(SERVICE_RESULT_CODE_SDK_RESPONSE, resultData);
        } else if (result instanceof ErrorTransactionResponse response) {
            resultData.putParcelable(SERVICE_RESULT_ERROR_KEY, response);
            resultReceiver.send(SERVICE_RESULT_CODE_SDK_ERROR, resultData);
        }
    }
}
