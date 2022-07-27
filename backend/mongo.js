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
      "undefined": {
        "collections": [
          {
            "name": "system.namespaces",
            "documents": [
              {
                "name": "system.indexes"
              },
              {
                "name": "documents"
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
                "ns": "undefined.documents",
                "name": "_id_",
                "unique": true
              }
            ]
          },
          {
            "name": "documents",
            "documents": [
              {
                "email": "jimothy@gmail.com",
                "_id": ObjectID("62e0fdea3e75243635361764")
              },
              {
                "email": "timothy@gmail.com",
                "_id": ObjectID("62e0fdea3e75243635361765")
              }
            ]
          }
        ]
      }
    }
  }
}