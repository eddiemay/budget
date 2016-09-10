/**
 * @fileoverview
 * @enhanceable
 * @public
 */
// GENERATED CODE -- DO NOT EDIT!

goog.provide('proto.budget.BillListRequest');

goog.require('jspb.Message');

goog.forwardDeclare('proto.common.DateRange');

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
proto.budget.BillListRequest = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.budget.BillListRequest, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.budget.BillListRequest.displayName = 'proto.budget.BillListRequest';
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
proto.budget.BillListRequest.prototype.toObject = function(opt_includeInstance) {
  return proto.budget.BillListRequest.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.budget.BillListRequest} msg The msg instance to transform.
 * @return {!Object}
 */
proto.budget.BillListRequest.toObject = function(includeInstance, msg) {
  var f, obj = {
    portfolioId: jspb.Message.getField(msg, 1),
    refDate: jspb.Message.getField(msg, 2),
    dateRange: jspb.Message.getField(msg, 3)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg
  }
  return obj;
};
}


/**
 * Creates a deep clone of this proto. No data is shared with the original.
 * @return {!proto.budget.BillListRequest} The clone.
 */
proto.budget.BillListRequest.prototype.cloneMessage = function() {
  return /** @type {!proto.budget.BillListRequest} */ (jspb.Message.cloneMessage(this));
};


/**
 * optional int32 portfolio_id = 1;
 * @return {number?}
 */
proto.budget.BillListRequest.prototype.getPortfolioId = function() {
  return /** @type {number?} */ (jspb.Message.getField(this, 1));
};


/** @param {number?|undefined} value  */
proto.budget.BillListRequest.prototype.setPortfolioId = function(value) {
  jspb.Message.setField(this, 1, value);
};


proto.budget.BillListRequest.prototype.clearPortfolioId = function() {
  jspb.Message.setField(this, 1, undefined);
};


/**
 * optional int64 ref_date = 2;
 * @return {number?}
 */
proto.budget.BillListRequest.prototype.getRefDate = function() {
  return /** @type {number?} */ (jspb.Message.getField(this, 2));
};


/** @param {number?|undefined} value  */
proto.budget.BillListRequest.prototype.setRefDate = function(value) {
  jspb.Message.setField(this, 2, value);
};


proto.budget.BillListRequest.prototype.clearRefDate = function() {
  jspb.Message.setField(this, 2, undefined);
};


/**
 * optional common.DateRange date_range = 3;
 * @return {proto.common.DateRange}
 */
proto.budget.BillListRequest.prototype.getDateRange = function() {
  return /** @type {proto.common.DateRange} */ (jspb.Message.getField(this, 3));
};


/** @param {proto.common.DateRange|undefined} value  */
proto.budget.BillListRequest.prototype.setDateRange = function(value) {
  jspb.Message.setField(this, 3, value);
};


proto.budget.BillListRequest.prototype.clearDateRange = function() {
  jspb.Message.setField(this, 3, undefined);
};

