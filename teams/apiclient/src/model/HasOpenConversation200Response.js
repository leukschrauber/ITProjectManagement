/**
 * HTI Bot API
 * Interact with the HTI Bot Backend
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 *
 */

import ApiClient from '../ApiClient';

/**
 * The HasOpenConversation200Response model module.
 * @module model/HasOpenConversation200Response
 * @version 1.0.0
 */
class HasOpenConversation200Response {
    /**
     * Constructs a new <code>HasOpenConversation200Response</code>.
     * @alias module:model/HasOpenConversation200Response
     */
    constructor() { 
        
        HasOpenConversation200Response.initialize(this);
    }

    /**
     * Initializes the fields of this object.
     * This method is used by the constructors of any subclasses, in order to implement multiple inheritance (mix-ins).
     * Only for internal use.
     */
    static initialize(obj) { 
    }

    /**
     * Constructs a <code>HasOpenConversation200Response</code> from a plain JavaScript object, optionally creating a new instance.
     * Copies all relevant properties from <code>data</code> to <code>obj</code> if supplied or a new instance if not.
     * @param {Object} data The plain JavaScript object bearing properties of interest.
     * @param {module:model/HasOpenConversation200Response} obj Optional instance to populate.
     * @return {module:model/HasOpenConversation200Response} The populated <code>HasOpenConversation200Response</code> instance.
     */
    static constructFromObject(data, obj) {
        if (data) {
            obj = obj || new HasOpenConversation200Response();

            if (data.hasOwnProperty('resultCode')) {
                obj['resultCode'] = ApiClient.convertToType(data['resultCode'], 'Number');
            }
            if (data.hasOwnProperty('hasOpenConversation')) {
                obj['hasOpenConversation'] = ApiClient.convertToType(data['hasOpenConversation'], 'Boolean');
            }
        }
        return obj;
    }

    /**
     * Validates the JSON data with respect to <code>HasOpenConversation200Response</code>.
     * @param {Object} data The plain JavaScript object bearing properties of interest.
     * @return {boolean} to indicate whether the JSON data is valid with respect to <code>HasOpenConversation200Response</code>.
     */
    static validateJSON(data) {

        return true;
    }


}



/**
 * The result code
 * @member {Number} resultCode
 */
HasOpenConversation200Response.prototype['resultCode'] = undefined;

/**
 * Whether the user has an unclosed conversation and did not request further conversation.
 * @member {Boolean} hasOpenConversation
 */
HasOpenConversation200Response.prototype['hasOpenConversation'] = undefined;






export default HasOpenConversation200Response;

