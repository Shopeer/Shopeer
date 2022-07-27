import { isString } from 'underscore';
import MinimongoError from './MinimongoError';

// Make sure field names do not contain Mongo restricted
// characters ('.', '$', '\0').
// https://docs.mongodb.com/manual/reference/limits/#Restrictions-on-Field-Names
var invalidCharMsg = {
  '.': "contain '.'",
  '$': "start with '$'",
  '\0': "contain null bytes"
};
export function assertIsValidFieldName(key) {
  var match = void 0;
  if (isString(key) && (match = key.match(/^\$|\.|\0/))) {
    throw MinimongoError('Key ' + key + ' must not ' + invalidCharMsg[match[0]]);
  }
};

// checks if all field names in an object are valid
export function assertHasValidFieldNames(doc) {
  if (doc && (typeof doc === 'undefined' ? 'undefined' : babelHelpers.typeof(doc)) === "object") {
    JSON.stringify(doc, function (key, value) {
      assertIsValidFieldName(key);
      return value;
    });
  }
};