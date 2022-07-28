const mongoose = require('mongoose');
require('dotenv').config();

const { MongoClient } = require("mongodb");  // this is multiple return
const { find } = require('./config/mongodb_connection');
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)
const user_collection = mongoClient.db("shopeer_database").collection("user_collection")
mongoClient.connect()


describe('Customer CRUD', () => {
    let connection;
    let db;
    const customers = mongoose.model("test_" + process.env.COLLECTION, mongoose.Schema({
        name: String,
        email: String
    }));

    beforeAll(async () => {

        console.log(process.env.CUSTOMER_EMAIL)
        connection = mongoose.connect('mongodb://127.0.0.1:27017/test_' + process.env.DATABASE, { useNewUrlParser: true, useUnifiedTopology: true });
        db = mongoose.connection;
        const collection = process.env.COLLECTION;
        db.createCollection(collection);


    });

    afterAll(async () => {

        const collection = "test_" + process.env.COLLECTION;
        await db.dropCollection(collection);
        await db.dropDatabase();
        await db.close();
        // await connection.close();

    });

    test("This one", async () => {
        var finder = await user_collection.findOne()
        console.log(finder)
        expect(finder.email).toBe("timothy@gmail.com")
    });


    test("Add Customer POST /customers", async () => {

        const response = await customers.create({
            name: process.env.CUSTOMER_NAME,
            email: process.env.CUSTOMER_EMAIL
        });
        await response.save();
        expect(response.name).toBe(process.env.CUSTOMER_NAME);

    });

    // test("All Customers GET /customers", async () => {

    //     const response = await customers.find({});
    //     expect(response.length).toBeGreaterThan(0);

    // });

    // test("Update Customer PUT /customers/:id", async () => {

    //     const response = await customers.updateOne({ name: process.env.CUSTOMER_NAME }, { email: process.env.CUSTOMER_EMAIL_ALT });
    //     console.log(response)
    //     expect(response.matchedCount).toBeTruthy();

    // });

    // test("Customer update is correct", async () => {

    //     const responseTwo = await customers.findOne({ name: process.env.CUSTOMER_NAME });
    //     expect(responseTwo.email).toBe(process.env.CUSTOMER_EMAIL_ALT);

    // });

    // test("Delete Customer DELETE /customers/:id", async () => {

    //     const response = await customers.deleteOne({ name: process.env.CUSTOMER_NAME });
    //     expect(response.ok).toBe(1);


    // });

});