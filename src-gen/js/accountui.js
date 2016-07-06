/**
 * @fileoverview
 * @enhanceable
 * @public
 */
// GENERATED CODE -- DO NOT EDIT!

goog.provide('proto.budget.AccountUI');

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
proto.budget.AccountUI = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.budget.AccountUI, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.budget.AccountUI.displayName = 'proto.budget.AccountUI';
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
proto.budget.AccountUI.prototype.toObject = function(opt_includeInstance) {
  return proto.budget.AccountUI.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.budget.AccountUI} msg The msg instance to transform.
 * @return {!Object}
 */
proto.budget.AccountUI.toObject = function(includeInstance, msg) {
  var f, obj = {
    id: jspb.Message.getField(msg, 1),
    portfolioId: jspb.Message.getField(msg, 2),
    name: jspb.Message.getField(msg, 3),
    description: jspb.Message.getField(msg, 4),
    paymentAccount: jspb.Message.getField(msg, 5),
    parentAccountId: jspb.Message.getField(msg, 6)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg
  }
  return obj;
};
}


/**
 * Creates a deep clone of this proto. No data is shared with the original.
 * @return {!proto.budget.AccountUI} The clone.
 */
proto.budget.AccountUI.prototype.cloneMessage = function() {
  return /** @type {!proto.budget.AccountUI} */ (jspb.Message.cloneMessage(this));
};


/**
 * optional int32 id = 1;
 * @return {number?}
 */
proto.budget.AccountUI.prototype.getId = function() {
  return /** @type {number?} */ (jspb.Message.getField(this, 1));
};


/** @param {number?|undefined} value  */
proto.budget.AccountUI.prototype.setId = function(value) {
  jspb.Message.setField(this, 1, value);
};


proto.budget.AccountUI.prototype.clearId = function() {
  jspb.Message.setField(this, 1, undefined);
};


/**
 * optional int32 portfolio_id = 2;
 * @return {number?}
 */
proto.budget.AccountUI.prototype.getPortfolioId = function() {
  return /** @type {number?} */ (jspb.Message.getField(this, 2));
};


/** @param {number?|undefined} value  */
proto.budget.AccountUI.prototype.setPortfolioId = function(value) {
  jspb.Message.setField(this, 2, value);
};


proto.budget.AccountUI.prototype.clearPortfolioId = function() {
  jspb.Message.setField(this, 2, undefined);
};


/**
 * optional string name = 3;
 * @return {string?}
 */
proto.budget.AccountUI.prototype.getName = function() {
  return /** @type {string?} */ (jspb.Message.getField(this, 3));
};


/** @param {string?|undefined} value  */
proto.budget.AccountUI.prototype.setName = function(value) {
  jspb.Message.setField(this, 3, value);
};


proto.budget.AccountUI.prototype.clearName = function() {
  jspb.Message.setField(this, 3, undefined);
};


/**
 * optional string description = 4;
 * @return {string?}
 */
proto.budget.AccountUI.prototype.getDescription = function() {
  return /** @type {string?} */ (jspb.Message.getField(this, 4));
};


/** @param {string?|undefined} value  */
proto.budget.AccountUI.prototype.setDescription = function(value) {
  jspb.Message.setField(this, 4, value);
};


proto.budget.AccountUI.prototype.clearDescription = function() {
  jspb.Message.setField(this, 4, undefined);
};


/**
 * optional bool payment_account = 5;
 * Note that Boolean fields may be set to 0/1 when serialized from a Java server.
 * You should avoid comparisons like {@code val === true/false} in those cases.
 * @return {boolean?}
 */
proto.budget.AccountUI.prototype.getPaymentAccount = function() {
  return /** @type {boolean?} */ (jspb.Message.getField(this, 5));
};


/** @param {boolean?|undefined} value  */
proto.budget.AccountUI.prototype.setPaymentAccount = function(value) {
  jspb.Message.setField(this, 5, value);
};


proto.budget.AccountUI.prototype.clearPaymentAccount = function() {
  jspb.Message.setField(this, 5, undefined);
};


/**
 * optional int32 parent_account_id = 6;
 * @return {number?}
 */
proto.budget.AccountUI.prototype.getParentAccountId = function() {
  return /** @type {number?} */ (jspb.Message.getField(this, 6));
};


/** @param {number?|undefined} value  */
proto.budget.AccountUI.prototype.setParentAccountId = function(value) {
  jspb.Message.setField(this, 6, value);
};


proto.budget.AccountUI.prototype.clearParentAccountId = function() {
  jspb.Message.setField(this, 6, undefined);
};


