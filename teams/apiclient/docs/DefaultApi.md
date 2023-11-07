# HtiBotApi.DefaultApi

All URIs are relative to *http://localhost:9191/rest/v1.0*

Method | HTTP request | Description
------------- | ------------- | -------------
[**continueConversation**](DefaultApi.md#continueConversation) | **PUT** /htibot/continueConversation | Requests further conversation in the ongoing conversation
[**getAnswer**](DefaultApi.md#getAnswer) | **GET** /htibot/getAnswer | Retrieves an answer to a user prompt.
[**hasOpenConversation**](DefaultApi.md#hasOpenConversation) | **GET** /htibot/hasOpenConversation | Checks whether user currently has any open conversation with the Bot Backend.
[**rateConversation**](DefaultApi.md#rateConversation) | **PUT** /htibot/rateConversation | Rates the currently open conversation as positive or negative.
[**updateKnowledgeDB**](DefaultApi.md#updateKnowledgeDB) | **POST** /htibot/updateKnowledgeDB | Updates the vector database with the uploaded zipfile.



## continueConversation

> UpdateKnowledgeDB200Response continueConversation(userId)

Requests further conversation in the ongoing conversation

Requests further conversation in the ongoing conversation enabling to follow-up on messages.

### Example

```javascript
import HtiBotApi from 'hti_bot_api';
let defaultClient = HtiBotApi.ApiClient.instance;
// Configure API key authorization: apiKeyAuth
let apiKeyAuth = defaultClient.authentications['apiKeyAuth'];
apiKeyAuth.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKeyAuth.apiKeyPrefix = 'Token';

let apiInstance = new HtiBotApi.DefaultApi();
let userId = 1; // Number | The user id as determined by the caller
apiInstance.continueConversation(userId, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userId** | **Number**| The user id as determined by the caller | 

### Return type

[**UpdateKnowledgeDB200Response**](UpdateKnowledgeDB200Response.md)

### Authorization

[apiKeyAuth](../README.md#apiKeyAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getAnswer

> GetAnswer200Response getAnswer(prompt, userId, language)

Retrieves an answer to a user prompt.

Retrieves relevant internal documentation and generates an answer using a Large Language Model.

### Example

```javascript
import HtiBotApi from 'hti_bot_api';
let defaultClient = HtiBotApi.ApiClient.instance;
// Configure API key authorization: apiKeyAuth
let apiKeyAuth = defaultClient.authentications['apiKeyAuth'];
apiKeyAuth.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKeyAuth.apiKeyPrefix = 'Token';

let apiInstance = new HtiBotApi.DefaultApi();
let prompt = Tell me what the weather is like tomorrow; // String | The prompt of the user
let userId = 1; // Number | The user id as determined by the caller
let language = new HtiBotApi.LanguageEnum(); // LanguageEnum | The language for the operation.
apiInstance.getAnswer(prompt, userId, language, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **prompt** | **String**| The prompt of the user | 
 **userId** | **Number**| The user id as determined by the caller | 
 **language** | [**LanguageEnum**](.md)| The language for the operation. | 

### Return type

[**GetAnswer200Response**](GetAnswer200Response.md)

### Authorization

[apiKeyAuth](../README.md#apiKeyAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## hasOpenConversation

> HasOpenConversation200Response hasOpenConversation(userId)

Checks whether user currently has any open conversation with the Bot Backend.

A user that has not closed his conversation yet and that has not requested to continue the current conversation has an open conversation.

### Example

```javascript
import HtiBotApi from 'hti_bot_api';
let defaultClient = HtiBotApi.ApiClient.instance;
// Configure API key authorization: apiKeyAuth
let apiKeyAuth = defaultClient.authentications['apiKeyAuth'];
apiKeyAuth.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKeyAuth.apiKeyPrefix = 'Token';

let apiInstance = new HtiBotApi.DefaultApi();
let userId = 1; // Number | The user id as determined by the caller
apiInstance.hasOpenConversation(userId, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userId** | **Number**| The user id as determined by the caller | 

### Return type

[**HasOpenConversation200Response**](HasOpenConversation200Response.md)

### Authorization

[apiKeyAuth](../README.md#apiKeyAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## rateConversation

> UpdateKnowledgeDB200Response rateConversation(userId, rating)

Rates the currently open conversation as positive or negative.

Rates the currently open conversation as positive or negative.

### Example

```javascript
import HtiBotApi from 'hti_bot_api';
let defaultClient = HtiBotApi.ApiClient.instance;
// Configure API key authorization: apiKeyAuth
let apiKeyAuth = defaultClient.authentications['apiKeyAuth'];
apiKeyAuth.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKeyAuth.apiKeyPrefix = 'Token';

let apiInstance = new HtiBotApi.DefaultApi();
let userId = 1; // Number | The user id as determined by the caller
let rating = true; // Boolean | The rating of the conversation
apiInstance.rateConversation(userId, rating, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userId** | **Number**| The user id as determined by the caller | 
 **rating** | **Boolean**| The rating of the conversation | 

### Return type

[**UpdateKnowledgeDB200Response**](UpdateKnowledgeDB200Response.md)

### Authorization

[apiKeyAuth](../README.md#apiKeyAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## updateKnowledgeDB

> UpdateKnowledgeDB200Response updateKnowledgeDB(zipFile, opts)

Updates the vector database with the uploaded zipfile.

Updates the vector database with the uploaded zipfile. Zipfile consists of FAQ in html format and enclosed resources folder with pictures. CleanUp&#x3D;True will result in the old entries being wiped from the database after succesful upload.

### Example

```javascript
import HtiBotApi from 'hti_bot_api';
let defaultClient = HtiBotApi.ApiClient.instance;
// Configure API key authorization: apiKeyAuth
let apiKeyAuth = defaultClient.authentications['apiKeyAuth'];
apiKeyAuth.apiKey = 'YOUR API KEY';
// Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
//apiKeyAuth.apiKeyPrefix = 'Token';

let apiInstance = new HtiBotApi.DefaultApi();
let zipFile = "/path/to/file"; // File | The FAQ Zip File with HTML FAQs and enclosed resources folder
let opts = {
  'cleanUp': false // Boolean | Whether to delete old entries after succesful upload of new entries.
};
apiInstance.updateKnowledgeDB(zipFile, opts, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **zipFile** | **File**| The FAQ Zip File with HTML FAQs and enclosed resources folder | 
 **cleanUp** | **Boolean**| Whether to delete old entries after succesful upload of new entries. | [optional] [default to false]

### Return type

[**UpdateKnowledgeDB200Response**](UpdateKnowledgeDB200Response.md)

### Authorization

[apiKeyAuth](../README.md#apiKeyAuth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

