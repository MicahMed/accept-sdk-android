
# Authorize.Net Accept Mobile SDK for Android  


This SDK allows mobile developers to accept payments on a customer's mobile device from within their Android applications without having to pass sensitive card data back to their application backend servers.  For more information on including payments in your mobile application see our [In-App Payments Guide](http://developer.authorize.net/api/reference/features/in-app.html)


## Contents

1. [Installation](#installation-one-step)
1. [Getting Started](#getting-started-four-steps)
1. [Sample Application](#sample-application)

## Installation (One Step)
Add the dependency to your app's `build.gradle` file.

```groovy
repositories {
   maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.MicahMed:accept-sdk-android:2.0.0'
}
```

## Getting Started (Four Steps)

### Prerequisites
Android API 14+ is required as the `minSdkVersion` in your build.gradle


### 1. Initialize AcceptSDKApiClient
All SDK API's will be accessed through `AcceptSDKApiClient` Object, which can be created as follows:

```java
// Parameters:
// 1) Context - Activity context
// 2) AcceptSDKApiClient.Environment - AUTHORIZE.NET ENVIRONMENT
apiClient = new AcceptSDKApiClient.Builder (getActivity(),
                                          AcceptSDKApiClient.Environment.SANDBOX) 
                                          .connectionTimeout(5000) // optional connection time out in milliseconds
                                          .build();
```

### 2. Prepare Objects required to call Token API
Fetch token API requires `EncryptTransactionObject`, which can be created as follows:

```java
 EncryptTransactionObject transactionObject = TransactionObject.
         createTransactionObject(TransactionType.SDK_TRANSACTION_ENCRYPTION)// type of transaction object
        .cardData(prepareCardDataFromFields()) // card data to be encrypted
        .merchantAuthentication(prepareMerchantAuthentication()) //Merchant authentication
        .build();
```

`EncryptTransactionObject` requires `cardData` object, which  can be created as follows:

```java
CardData cardData = new CardData.Builder(CARD_NUMBER,
                                               EXPIRATION_MONTH, // MM
                                               EXPIRATION_YEAR) // YYYY
                                               .cvvCode(CARD_CVV) // Optional
                                               .zipCode(ZIP_CODE)// Optional
                                               .cardHolderName(CARD_HOLDER_NAME)// Optional
                                               .build();
```

`EncryptTransactionObject` requires `merchantAuthentication` object, which  can be created as follows:

```java
ClientKeyBasedMerchantAuthentication merchantAuthentication = ClientKeyBasedMerchantAuthentication.
                createMerchantAuthentication(API_LOGIN_ID, CLIENT_KEY);
```

Check out the "Obtaining a Public Client Key" section in [Accept Mobile](http://developer.authorize.net/api/reference/features/in-app.html) 
for more information on getting CLIENT_KEY.

### 3. Calling Token API
When transaction information is ready, you can make the following call to fetch token:

```java
// Parameters: 
// 1. EncryptTransactionObject - The transaction object for current transaction
// 2. transaction response callback.
apiClient.getTokenWithRequest(transactionObject, callback);
```

### 4. Implement  `EncryptTransactionCallback` Interface.
To get a response back, the activity/fragment should implement the `EncryptTransactionCallback` interface. It has thefollowing methods:

> [`onEncryptionFinished()`](#onEncryption-Finished)

> [`onErrorReceived()`](#onError-Received)

#### `onEncryptionFinished()` 
   This method will be called when token is successfully generated.`EncryptTransactionResponse` object has Data Descriptor and Data Value details which will be used to perform the payment transaction.
   
```java
@Override
public void onEncryptionFinished(EncryptTransactionResponse response) 
{ 
  Toast.makeText(getActivity(), 
                 response.getDataDescriptor() + " : " + response.getDataValue(),
                 Toast.LENGTH_LONG)
                 .show();
}
```

#### `onErrorReceived()`
   This method will be called in three scenarios,
   
     > Validation of information is failed.
     > Network related errors.
     > API error response.
     
 `ErrorTransactionResponse` may contain one or more error messages.

```java
@Override
public void onErrorReceived(ErrorTransactionResponse errorResponse) 
{ 
 Message error = errorResponse.getFirstErrorMessage();
  Toast.makeText(getActivity(), 
                 error.getMessageCode() + " : " + error.getMessageText() ,
                 Toast.LENGTH_LONG)
                 .show();
}
```

## Sample Application
We have a sample application which demonstrates the SDK usage:  
   https://github.com/AuthorizeNet/accept-sample-android
  
  
## Google Play In-App Billing API
Google’s developer terms require that purchases related to the app, such as premium features or credits, are made via their native Google Play In-app Billing API.  See https://play.google.com/about/developer-content-policy.html for more details.
