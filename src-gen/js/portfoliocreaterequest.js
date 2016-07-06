/**
 * @fileoverview
 * @enhanceable
 * @public
 */
// GENERATED CODE -- DO NOT EDIT!

goog.provide('proto.budget.PortfolioCreateRequest');

goog.require('jspb.Message');
goog.require('proto.budget.PortfolioUI');


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
proto.budget.PortfolioCreateRequest = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.budget.PortfolioCreateRequest, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.budget.PortfolioCreateRequest.displayName = 'proto.budget.PortfolioCreateRequest';
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
proto.budget.PortfolioCreateRequest.prototype.toObject = function(opt_includeInstance) {
  return proto.budget.PortfolioCreateRequest.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.budget.PortfolioCreateRequest} msg The msg instance to transform.
 * @return {!Object}
 */
proto.budget.PortfolioCreateRequest.toObject = function(includeInstance, msg) {
  var f, obj = {
    portfolio: (f = msg.getPortfolio()) && proto.budget.PortfolioUI.toObject(includeInstance, f)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg
  }
  return obj;
};
}


/**
 * Creates a deep clone of this proto. No data is shared with the original.
 * @return {!proto.budget.PortfolioCreateRequest} The clone.
 */
proto.budget.PortfolioCreateRequest.prototype.cloneMessage = function() {
  return /** @type {!proto.budget.PortfolioCreateRequest} */ (jspb.Message.cloneMessage(this));
};


/**
 * optional PortfolioUI portfolio = 1;
 * @return {proto.budget.PortfolioUI}
 */
proto.budget.PortfolioCreateRequest.prototype.getPortfolio = function() {
  return /** @type{proto.budget.PortfolioUI} */ (
    jspb.Message.getWrapperField(this, proto.budget.PortfolioUI, 1));
};


/** @param {proto.budget.PortfolioUI|undefined} value  */
proto.budget.PortfolioCreateRequest.prototype.setPortfolio = function(value) {
  jspb.Message.setWrapperField(this, 1, value);
};


proto.budget.PortfolioCreateRequest.prototype.clearPortfolio = function() {
  this.setPortfolio(undefined);
};


