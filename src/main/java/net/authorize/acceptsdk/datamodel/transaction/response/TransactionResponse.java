package net.authorize.acceptsdk.datamodel.transaction.response;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.List;
import net.authorize.acceptsdk.datamodel.common.Message;
import net.authorize.acceptsdk.datamodel.common.ResponseMessages;

/**
 * Base Transaction Response Object.
 * <p>
 * Created by Kiran Bollepalli on 12,July,2016.
 * kbollepa@visa.com
 */
public class TransactionResponse implements Parcelable {

    ResponseMessages responseMessages;

    public TransactionResponse() {
    }

    protected TransactionResponse(Parcel in) {
        readFromParcel(in);
    }

    public TransactionResponse(ResponseMessages responseMessages) {
        this.responseMessages = responseMessages;
    }

    public ResponseMessages getResponseMessages() {
        return responseMessages;
    }

    public void setResponseMessages(ResponseMessages responseMessages) {
        this.responseMessages = responseMessages;
    }

    public String getResultCode() {
        return responseMessages.getResultCode();
    }

    public List<Message> getMessageList() {
        return responseMessages.getMessageList();
    }
    // ---------- Code for Parcelable interface ----------

    public void readFromParcel(Parcel in) {
        responseMessages = in.readParcelable(ResponseMessages.class.getClassLoader());
    }

    @Override public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(responseMessages, flags);
    }

    @Override public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionResponse> CREATOR = new Creator<TransactionResponse>() {
        @Override public TransactionResponse createFromParcel(Parcel in) {
            return new TransactionResponse(in);
        }

        @Override public TransactionResponse[] newArray(int size) {
            return new TransactionResponse[size];
        }
    };
}
