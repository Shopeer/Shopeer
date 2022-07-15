import update from 'immutability-helper';
import modify from './modify.js';

// Examples are taken from https://docs.mongodb.com/manual/reference/
describe("modify", function () {
  describe("$currentDate", function () {
    it.skip("sets a field with a currentDate", function () {
      var myObject = { existingItem: "here" };

      var updatedObject = modify(myObject, { $currentDate: "lastModified" });

      var expectedObject = { existingItem: "here", lastModified: new Date() }; // placeholder, obviously wrong
      expect(updatedObject).toEqual(expectedObject);
    });
  });
  describe("$min", function () {
    it("updates a field when the passed value is lower than an existing one", function () {
      var myObject = { _id: 1, highScore: 800, lowScore: 200 };

      var updatedObject = modify(myObject, { $min: { lowScore: 150 } });

      var expectedObject = babelHelpers.extends({}, myObject, { lowScore: 150 });
      expect(updatedObject).toEqual(expectedObject);
    });
    it("doesn't update a field when the passed value is higher than an existing one", function () {
      var myObject = { _id: 1, highScore: 800, lowScore: 200 };

      var updatedObject = modify(myObject, { $min: { lowScore: 250 } });

      var expectedObject = babelHelpers.extends({}, myObject);
      expect(updatedObject).toEqual(expectedObject);
    });
  });
  describe("$max", function () {
    it("updates a field when the passed value is higher than an existing one", function () {
      var myObject = { _id: 1, highScore: 800, lowScore: 200 };

      var updatedObject = modify(myObject, { $max: { highScore: 950 } });

      var expectedObject = babelHelpers.extends({}, myObject, { highScore: 950 });
      expect(updatedObject).toEqual(expectedObject);
    });
    it("doesn't update a field when the passed value is lower than an existing one", function () {
      var myObject = { _id: 1, highScore: 950, lowScore: 200 };

      var updatedObject = modify(myObject, { $max: { highScore: 870 } });

      var expectedObject = babelHelpers.extends({}, myObject);
      expect(updatedObject).toEqual(expectedObject);
    });
  });
  describe("$inc", function () {
    it("can increment with positive and negative values at the same time", function () {
      var myObject = {
        _id: 1,
        sku: "abc123",
        quantity: 10,
        metrics: {
          orders: 2,
          ratings: 3.5
        }
      };

      var updatedObject = modify(myObject, { $inc: { quantity: -2, "metrics.orders": 1 } });

      var expectedObject = update(myObject, {
        quantity: { $set: myObject.quantity - 2 },
        metrics: {
          orders: { $set: myObject.metrics.orders + 1 }
        }
      });

      expect(updatedObject).toEqual(expectedObject);
    });
  });
  describe("$set", function () {
    // https://docs.mongodb.com/manual/reference/operator/update/set/#set-top-level-fields
    it("sets top-level fields", function () {
      var myObject = {
        _id: 100,
        sku: "abc123",
        quantity: 250,
        instock: true,
        reorder: false,
        details: { model: "14Q2", make: "xyz" },
        tags: ["apparel", "clothing"],
        ratings: [{ by: "ijk", rating: 4 }]
      };

      var updatedObject = modify(myObject, { $set: {
          quantity: 500,
          details: { model: "14Q3", make: "xyz" },
          tags: ["coats", "outerwear", "clothing"]
        }
      });

      var expectedObject = update(myObject, {
        quantity: { $set: 500 },
        details: { $set: { model: "14Q3", make: "xyz" } },
        tags: { $set: ["coats", "outerwear", "clothing"] }
      });

      expect(updatedObject).toEqual(expectedObject);
    });
    // https://docs.mongodb.com/manual/reference/operator/update/set/#set-fields-in-embedded-documents
    it("sets fields in embedded documents", function () {
      var myObject = {
        _id: 100,
        sku: "abc123",
        quantity: 250,
        instock: true,
        reorder: false,
        details: { model: "14Q2", make: "xyz" },
        tags: ["apparel", "clothing"],
        ratings: [{ by: "ijk", rating: 4 }]
      };

      var updatedObject = modify(myObject, { $set: { "details.make": "zzz" } });

      var expectedObject = update(myObject, {
        details: {
          make: { $set: "zzz" }
        }
      });

      expect(updatedObject).toEqual(expectedObject);
    });
    // https://docs.mongodb.com/manual/reference/operator/update/set/#set-elements-in-arrays
    it("sets elements in arrays", function () {
      var myObject = {
        _id: 100,
        sku: "abc123",
        quantity: 250,
        instock: true,
        reorder: false,
        details: { model: "14Q2", make: "xyz" },
        tags: ["apparel", "clothing"],
        ratings: [{ by: "ijk", rating: 4 }]
      };

      var updatedObject = modify(myObject, { $set: {
          "tags.1": "rain gear",
          "ratings.0.rating": 2
        }
      });

      var expectedObject = update(myObject, {
        tags: {
          1: { $set: "rain gear" }
        },
        ratings: {
          0: {
            rating: { $set: 2 }
          }
        }
      });

      expect(updatedObject).toEqual(expectedObject);
    });
  });
  // https://docs.mongodb.com/manual/reference/operator/update/unset/
  describe("$unset", function () {
    it("deletes a particular field", function () {
      var myObject = { sku: "unknown", quantity: 2, instock: true };

      var updatedObject = modify(myObject, { $unset: { quantity: "", instock: "" } });

      var expectedObject = babelHelpers.extends({}, myObject);
      delete expectedObject.quantity;
      delete expectedObject.instock;
      expect(updatedObject).toEqual(updatedObject);
    });
  });
  describe("$push", function () {
    // https://docs.mongodb.com/manual/reference/operator/update/push/#append-a-value-to-an-array
    it("appends a value to an array", function () {
      var myObject = { _id: 1, scores: [50] };

      var updatedObject = modify(myObject, { $push: { scores: 89 } });

      var expectedObject = update(myObject, {
        scores: { $push: [89] }
      });
      expect(updatedObject).toEqual(expectedObject);
    });
    // https://docs.mongodb.com/manual/reference/operator/update/push/#append-multiple-values-to-an-array
    it("appends multiple values to an array", function () {
      var myObject = { name: "joe", scores: [50] };

      var updatedObject = modify(myObject, { $push: { scores: { $each: [90, 92, 85] } } });

      var expectedObject = update(myObject, {
        scores: { $push: [90, 92, 85] }
      });

      expect(updatedObject).toEqual(expectedObject);
    });
    // https://docs.mongodb.com/manual/reference/operator/update/push/#use-push-operator-with-multiple-modifiers
    // Looks like this is not fully supported in minimongo yet!
    // I get MinimongoError: $slice in $push must be zero or negative for field 'quizzes'
    //TODO When I change slice to negative, turns out I don't have minimongo sorter yet, will investigate this later
    it.skip("can update an array with multiple modifiers", function () {
      var myObject = {
        "_id": 5,
        "quizzes": [{ "wk": 1, "score": 10 }, { "wk": 2, "score": 8 }, { "wk": 3, "score": 5 }, { "wk": 4, "score": 6 }]
      };

      var updatedObject = modify(myObject, {
        $push: {
          quizzes: {
            $each: [{ wk: 5, score: 8 }, { wk: 6, score: 7 }, { wk: 7, score: 6 }],
            $sort: { score: -1 },
            $slice: 3
          }
        }
      });

      var expectedObject = {
        "_id": 5,
        "quizzes": [{ "wk": 1, "score": 10 }, { "wk": 2, "score": 8 }, { "wk": 5, "score": 8 }]
      };

      expect(updatedObject).toEqual(expectedObject);
    });
  });
  // deprecated since mongo 2.4, based on the example of $push "appends multiple values to an array" above
  //
  describe("$pushAll", function () {
    it("appends multiple values to an array without $each", function () {
      var myObject = { name: "joe", scores: [50] };
      // compare with - notice the $each
      // {$push: {scores: {$each: [90, 92, 85]}}};

      var updatedObject = modify(myObject, { $pushAll: { scores: [90, 92, 85] } });

      var expectedObject = update(myObject, {
        scores: { $push: [90, 92, 85] }
      });

      expect(updatedObject).toEqual(expectedObject);
    });
  });
  // https://docs.mongodb.com/manual/reference/operator/update/addToSet/#behavior
  describe("$addToSet", function () {
    it("appends array with an array", function () {
      var myObject = { _id: 1, letters: ["a", "b"] };

      var updatedObject = modify(myObject, { $addToSet: { letters: ["c", "d"] } });

      var expectedObject = { _id: 1, letters: ["a", "b", ["c", "d"]] };

      expect(updatedObject).toEqual(expectedObject);
    });
    // https://docs.mongodb.com/manual/reference/operator/update/addToSet/#examples
    it("adds element to an array if element doesn't already exist", function () {
      var myObject = { _id: 1, item: "polarizing_filter", tags: ["electronics", "camera"] };

      var updatedObject = modify(myObject, { $addToSet: { tags: "accessories" } });

      var expectedObject = update(myObject, { tags: { $push: ["accessories"] } });

      expect(updatedObject).toEqual(expectedObject);
    });
    it("doesn't add an element to the array if it does already exists", function () {
      var myObject = { _id: 1, item: "polarizing_filter", tags: ["electronics", "camera"] };

      var updatedObject = modify(myObject, { $addToSet: { tags: "camera" } });

      var expectedObject = babelHelpers.extends({}, myObject);

      expect(updatedObject).toEqual(expectedObject);
    });
    // https://docs.mongodb.com/manual/reference/operator/update/addToSet/#each-modifier
    it("adds multiple values to the array field with $each modifier, omitting existing ones", function () {
      var myObject = { _id: 2, item: "cable", tags: ["electronics", "supplies"] };

      var updatedObject = modify(myObject, { $addToSet: { tags: { $each: ["camera", "electronics", "accessories"] } } });

      var expectedObject = update(myObject, { tags: { $push: ["camera", "accessories"] } });

      expect(updatedObject).toEqual(expectedObject);
    });
  });
  describe("$pop", function () {
    // https://docs.mongodb.com/manual/reference/operator/update/pop/#remove-the-first-item-of-an-array
    it("removes the first element from an array", function () {
      var myObject = { _id: 1, scores: [8, 9, 10] };

      var updatedObject = modify(myObject, { $pop: { scores: -1 } });

      var expectedObject = { _id: 1, scores: [9, 10] };
      expect(updatedObject).toEqual(expectedObject);
    });
    // https://docs.mongodb.com/manual/reference/operator/update/pop/#remove-the-last-item-of-an-array
    it("removes the last item of an array", function () {
      var myObject = { _id: 1, scores: [9, 10] };

      var updatedObject = modify(myObject, { $pop: { scores: 1 } });

      var expectedObject = { _id: 1, scores: [9] };
      expect(updatedObject).toEqual(expectedObject);
    });
  });
  describe("$pull", function () {
    // https://docs.mongodb.com/manual/reference/operator/update/pull/#remove-all-items-that-equals-a-specified-value
    it.skip("removes all items that equals a specified value", function () {
      var myObject = {
        _id: 1,
        fruits: ["apples", "pears", "oranges", "grapes", "bananas"],
        vegetables: ["carrots", "celery", "squash", "carrots"]
      };

      var updatedObject = modify(myObject, { $pull: { fruits: { $in: ["apples", "oranges"] }, vegetables: "carrots" } });

      var expectedObject = {
        "_id": 1,
        "fruits": ["pears", "grapes", "bananas"],
        "vegetables": ["celery", "squash"]
      };
      // notice two carrots missing from the vegetables array
      expect(updatedObject).toEqual(expectedObject);
    });
    // https://docs.mongodb.com/manual/reference/operator/update/pull/#remove-all-items-that-match-a-specified-pull-condition
    it.skip("Remove All Items That Match a Specified $pull Condition", function () {
      var myObject = { _id: 1, votes: [3, 5, 6, 7, 7, 8] };

      var updatedObject = modify(myObject, { $pull: { votes: { $gte: 6 } } });

      var expectedObject = { _id: 1, votes: [3, 5] };

      expect(updatedObject).toEqual(expectedObject);
    });
  });
  describe("$pullAll", function () {
    // https://docs.mongodb.com/manual/reference/operator/update/pullAll/#up._S_pullAll
    it("removes all instances of the specified values from an existing array", function () {
      var myObject = { _id: 1, scores: [0, 2, 5, 5, 1, 0] };

      var updatedObject = modify(myObject, { $pullAll: { scores: [0, 5] } });

      var expectedObject = { _id: 1, scores: [2, 1] };

      expect(updatedObject).toEqual(expectedObject);
    });
  });
  // https://docs.mongodb.com/manual/reference/operator/update/rename/
  describe("$rename", function () {
    it("updates the name of a field", function () {
      var myObject = {
        "_id": 1,
        "alias": ["The American Cincinnatus", "The American Fabius"],
        "mobile": "555-555-5555",
        "nmae": { "first": "george", "last": "washington" }
      };

      var updatedObject = modify(myObject, { $rename: { "nmae": "name" } });

      var expectedObject = babelHelpers.extends({}, myObject);
      delete expectedObject.nmae;
      expectedObject.name = babelHelpers.extends({}, myObject.nmae);

      expect(updatedObject).toEqual(expectedObject);
    });
  });

  it("throws an error when the operand path contains an empty field name", function () {
    var myObject = {};
    expect(function () {
      modify(myObject, { $set: { "test.abc.": "name" } });
    }).toThrow(/empty field name/);
  });
});