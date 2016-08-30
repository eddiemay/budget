/**
 * @fileoverview
 * @enhanceable
 * @public
 */
// GENERATED CODE -- DO NOT EDIT!

goog.provide('proto.budget.AccountUI');
goog.provide('proto.budget.AccountUI.AccountSummary');
goog.provide('proto.budget.AccountUI.BalanceUI');

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
  jspb.Message.initialize(this, opt_data, 0, -1, proto.budget.AccountUI.repeatedFields_, null);
};
goog.inherits(proto.budget.AccountUI, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.budget.AccountUI.displayName = 'proto.budget.AccountUI';
}
/**
 * List of repeated fields within this message type.
 * @private {!Array<number>}
 * @const
 */
proto.budget.AccountUI.repeatedFields_ = [7];



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
    paymentAccount: jspb.Message.getField(msg, 4),
    parentAccountId: jspb.Message.getField(msg, 5),
    balance: (f = msg.getBalance()) && proto.budget.AccountUI.BalanceUI.toObject(includeInstance, f),
    summaryList: jspb.Message.toObjectList(msg.getSummaryList(),
    proto.budget.AccountUI.AccountSummary.toObject, includeInstance)
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
 * optional bool payment_account = 4;
 * Note that Boolean fields may be set to 0/1 when serialized from a Java server.
 * You should avoid comparisons like {@code val === true/false} in those cases.
 * @return {boolean?}
 */
proto.budget.AccountUI.prototype.getPaymentAccount = function() {
  return /** @type {boolean?} */ (jspb.Message.getField(this, 4));
};


/** @param {boolean?|undefined} value  */
proto.budget.AccountUI.prototype.setPaymentAccount = function(value) {
  jspb.Message.setField(this, 4, value);
};


proto.budget.AccountUI.prototype.clearPaymentAccount = function() {
  jspb.Message.setField(this, 4, undefined);
};


/**
 * optional int32 parent_account_id = 5;
 * @return {number?}
 */
proto.budget.AccountUI.prototype.getParentAccountId = function() {
  return /** @type {number?} */ (jspb.Message.getField(this, 5));
};


/** @param {number?|undefined} value  */
proto.budget.AccountUI.prototype.setParentAccountId = function(value) {
  jspb.Message.setField(this, 5, value);
};


proto.budget.AccountUI.prototype.clearParentAccountId = function() {
  jspb.Message.setField(this, 5, undefined);
};


/**
 * optional BalanceUI balance = 6;
 * @return {proto.budget.AccountUI.BalanceUI}
 */
proto.budget.AccountUI.prototype.getBalance = function() {
  return /** @type{proto.budget.AccountUI.BalanceUI} */ (
    jspb.Message.getWrapperField(this, proto.budget.AccountUI.BalanceUI, 6));
};


/** @param {proto.budget.AccountUI.BalanceUI|undefined} value  */
proto.budget.AccountUI.prototype.setBalance = function(value) {
  jspb.Message.setWrapperField(this, 6, value);
};


proto.budget.AccountUI.prototype.clearBalance = function() {
  this.setBalance(undefined);
};


/**
 * repeated AccountSummary summary = 7;
 * If you change this array by adding, removing or replacing elements, or if you
 * replace the array itself, then you must call the setter to update it.
 * @return {!Array.<!proto.budget.AccountUI.AccountSummary>}
 */
proto.budget.AccountUI.prototype.getSummaryList = function() {
  return /** @type{!Array.<!proto.budget.AccountUI.AccountSummary>} */ (
    jspb.Message.getRepeatedWrapperField(this, proto.budget.AccountUI.AccountSummary, 7));
};


/** @param {Array.<!proto.budget.AccountUI.AccountSummary>|undefined} value  */
proto.budget.AccountUI.prototype.setSummaryList = function(value) {
  jspb.Message.setRepeatedWrapperField(this, 7, value);
};


proto.budget.AccountUI.prototype.clearSummaryList = function() {
  this.setSummaryList([]);
};



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
proto.budget.AccountUI.BalanceUI = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.budget.AccountUI.BalanceUI, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.budget.AccountUI.BalanceUI.displayName = 'proto.budget.AccountUI.BalanceUI';
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
proto.budget.AccountUI.BalanceUI.prototype.toObject = function(opt_includeInstance) {
  return proto.budget.AccountUI.BalanceUI.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.budget.AccountUI.BalanceUI} msg The msg instance to transform.
 * @return {!Object}
 */
proto.budget.AccountUI.BalanceUI.toObject = function(includeInstance, msg) {
  var f, obj = {
    date: jspb.Message.getField(msg, 1),
    balance: jspb.Message.getField(msg, 2),
    balanceYearToDate: jspb.Message.getField(msg, 3)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg
  }
  return obj;
};
}


/**
 * Creates a deep clone of this proto. No data is shared with the original.
 * @return {!proto.budget.AccountUI.BalanceUI} The clone.
 */
proto.budget.AccountUI.BalanceUI.prototype.cloneMessage = function() {
  return /** @type {!proto.budget.AccountUI.BalanceUI} */ (jspb.Message.cloneMessage(this));
};


/**
 * optional string date = 1;
 * @return {string?}
 */
proto.budget.AccountUI.BalanceUI.prototype.getDate = function() {
  return /** @type {string?} */ (jspb.Message.getField(this, 1));
};


/** @param {string?|undefined} value  */
proto.budget.AccountUI.BalanceUI.prototype.setDate = function(value) {
  jspb.Message.setField(this, 1, value);
};


proto.budget.AccountUI.BalanceUI.prototype.clearDate = function() {
  jspb.Message.setField(this, 1, undefined);
};


/**
 * optional double balance = 2;
 * @return {number?}
 */
proto.budget.AccountUI.BalanceUI.prototype.getBalance = function() {
  return /** @type {number?} */ (jspb.Message.getField(this, 2));
};


/** @param {number?|undefined} value  */
proto.budget.AccountUI.BalanceUI.prototype.setBalance = function(value) {
  jspb.Message.setField(this, 2, value);
};


proto.budget.AccountUI.BalanceUI.prototype.clearBalance = function() {
  jspb.Message.setField(this, 2, undefined);
};


/**
 * optional double balance_year_to_date = 3;
 * @return {number?}
 */
proto.budget.AccountUI.BalanceUI.prototype.getBalanceYearToDate = function() {
  return /** @type {number?} */ (jspb.Message.getField(this, 3));
};


/** @param {number?|undefined} value  */
proto.budget.AccountUI.BalanceUI.prototype.setBalanceYearToDate = function(value) {
  jspb.Message.setField(this, 3, value);
};


proto.budget.AccountUI.BalanceUI.prototype.clearBalanceYearToDate = function() {
  jspb.Message.setField(this, 3, undefined);
};



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
proto.budget.AccountUI.AccountSummary = function(opt_data) {
  jspb.Message.initialize(this, opt_data, 0, -1, null, null);
};
goog.inherits(proto.budget.AccountUI.AccountSummary, jspb.Message);
if (goog.DEBUG && !COMPILED) {
  proto.budget.AccountUI.AccountSummary.displayName = 'proto.budget.AccountUI.AccountSummary';
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
proto.budget.AccountUI.AccountSummary.prototype.toObject = function(opt_includeInstance) {
  return proto.budget.AccountUI.AccountSummary.toObject(opt_includeInstance, this);
};


/**
 * Static version of the {@see toObject} method.
 * @param {boolean|undefined} includeInstance Whether to include the JSPB
 *     instance for transitional soy proto support:
 *     http://goto/soy-param-migration
 * @param {!proto.budget.AccountUI.AccountSummary} msg The msg instance to transform.
 * @return {!Object}
 */
proto.budget.AccountUI.AccountSummary.toObject = function(includeInstance, msg) {
  var f, obj = {
    month: jspb.Message.getField(msg, 1),
    total: jspb.Message.getField(msg, 2)
  };

  if (includeInstance) {
    obj.$jspbMessageInstance = msg
  }
  return obj;
};
}


/**
 * Creates a deep clone of this proto. No data is shared with the original.
 * @return {!proto.budget.AccountUI.AccountSummary} The clone.
 */
proto.budget.AccountUI.AccountSummary.prototype.cloneMessage = function() {
  return /** @type {!proto.budget.AccountUI.AccountSummary} */ (jspb.Message.cloneMessage(this));
};


/**
 * optional string month = 1;
 * @return {string?}
 */
proto.budget.AccountUI.AccountSummary.prototype.getMonth = function() {
  return /** @type {string?} */ (jspb.Message.getField(this, 1));
};


/** @param {string?|undefined} value  */
proto.budget.AccountUI.AccountSummary.prototype.setMonth = function(value) {
  jspb.Message.setField(this, 1, value);
};


proto.budget.AccountUI.AccountSummary.prototype.clearMonth = function() {
  jspb.Message.setField(this, 1, undefined);
};


/**
 * optional double total = 2;
 * @return {number?}
 */
proto.budget.AccountUI.AccountSummary.prototype.getTotal = function() {
  return /** @type {number?} */ (jspb.Message.getField(this, 2));
};


/** @param {number?|undefined} value  */
proto.budget.AccountUI.AccountSummary.prototype.setTotal = function(value) {
  jspb.Message.setField(this, 2, value);
};


proto.budget.AccountUI.AccountSummary.prototype.clearTotal = function() {
  jspb.Message.setField(this, 2, undefined);
};


