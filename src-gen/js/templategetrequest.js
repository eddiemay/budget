/**
 * @fileoverview
 * @enhanceable
 * @public
 */
// GENERATED CODE -- DO NOT EDIT!

goog.provide('proto.budget.TemplateGetRequest');

goog.require('jspb.Message');


/**
 * Generated by JsPbCodeGenerator.
 * @param {Array=} opt_data Optional initial data array, typically from a
 * server response, or constructed directly in Javascript. The array is used
 * in place and becomes part of the constructed object. It is not cloned.
 * If no data is provided, the constructed object will be empty, but still
 * valid.
 * @extends {jspb.Message}
 * @constructor
 */
proto.budget.TemplateGetRequest = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.budget.TemplateGetRequest, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.budget.TemplateGetRequest.displayName = 'proto.budget.TemplateGetRequest';
}


if (jspb.Message.GENERATE_TO_OBJECT) {
/**
 * Creates an object representation of this proto suitable for use in Soy templates.
 * Field names that are reserved in JavaScript and will be renamed to pb_name.
 * To access a reserved field use, foo.pb_<name>, eg, foo.pb_default.
 * For the list of reserved names please see:
 *     com.google.apps.jspb.JsClassTemplate.JS_RESERVED_WORDS.
 * @param {boolean=} opt_includeInstance Whether to include the JSPB instance
 *     for transitional soy proto support: http://goto/soy-param-migration
 * @return {!Object}
 */
proto.budget.TemplateGetRequest.prototype.toObject = function(opt_includeInstance) {
  return proto.budget.TemplateGetRequest.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.budget.TemplateGetRequest} msg The msg instance to transform.
 * @return {!Object}
 */
proto.budget.TemplateGetRequest.toObject = function(includeInstance, msg) {
  var f, obj = {
    templateId: jspb.Message.getField(msg, 1)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg
  }
  return obj;
};
}


/**
 * Creates a deep clone of this proto. No data is shared with the original.
 * @return {!proto.budget.TemplateGetRequest} The clone.
 */
proto.budget.TemplateGetRequest.prototype.cloneMessage = function() {
  return /** @type {!proto.budget.TemplateGetRequest} */ (jspb.Message.cloneMessage(this));
};


/**
 * optional int32 template_id = 1;
 * @return {number?}
 */
proto.budget.TemplateGetRequest.prototype.getTemplateId = function() {
  return /** @type {number?} */ (jspb.Message.getField(this, 1));
};


/** @param {number?|undefined} value  */
proto.budget.TemplateGetRequest.prototype.setTemplateId = function(value) {
  jspb.Message.setField(this, 1, value);
};


proto.budget.TemplateGetRequest.prototype.clearTemplateId = function() {
  jspb.Message.setField(this, 1, undefined);
};


