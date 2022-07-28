require('dotenv').config();

var user_collection = require('./config/mongodb_connection')

describe('db.test.js', () => {
    test("This one", async () => {
        var finder = await user_collection.findOne()
        // console.log(finder.email)
        expect(finder.email).toBe("janjan3332001@gmail.com")
    });
});