var ObjectID = require('bson-objectid');

module.exports = {
  "localhost:27017": {
    "databases": {
      "myproject": {
        "collections": [
          {
            "name": "system.namespaces",
            "documents": [
              {
                "name": "system.indexes"
              }
            ]
          },
          {
            "name": "system.indexes",
            "documents": []
          }
        ]
      },
      "shopeer_database": {
        "collections": [
          {
            "name": "system.namespaces",
            "documents": [
              {
                "name": "system.indexes"
              },
              {
                "name": "user_collection"
              }
            ]
          },
          {
            "name": "system.indexes",
            "documents": [
              {
                "v": 1,
                "key": {
                  "_id": 1
                },
                "ns": "shopeer_database.user_collection",
                "name": "_id_",
                "unique": true
              }
            ]
          },
          {
            "name": "user_collection",
            "documents": [
              {
                "a": 1,
                "_id": ObjectID("62d0a194e1124d600621d44e")
              },
              {
                "a": 2,
                "_id": ObjectID("62d0a194e1124d600621d44f")
              },
              {
                "a": 3,
                "_id": ObjectID("62d0a194e1124d600621d450")
              },
              {
                "b": 1,
                "_id": ObjectID("62d0a194e1124d600621d451")
              },
              {
                "b": 2,
                "_id": ObjectID("62d0a194e1124d600621d452")
              },
              {
                "b": 3,
                "_id": ObjectID("62d0a194e1124d600621d453")
              }
            ]
          }
        ]
      }
    }
  }
}