MinimongoError = function MinimongoError(message) {
  var options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};

  if (typeof message === "string" && options.field) {
    message += " for field '" + options.field + "'";
  }

  var e = new Error(message);
  e.name = "MinimongoError";
  return e;
};

export default MinimongoError;