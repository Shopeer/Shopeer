const request = require('supertest');
const app = require('../config/app')
var user_collection = require('../config/mongodb_connection')

const ann_email = 'test_algo_ann@test.com'
const ann_name = 'test_algo_ann'
const bob_email = 'test_algo_bob@test.com'
const bob_name = 'test_algo_bob'
const charlie_email = 'test_algo_charlie@test.com'
const charlie_name = 'test_algo_charlie'
const dobert_email = 'test_algo_dobert@test.com'
const dobert_name = 'test_algo_dobert'
var emails = [ann_email, bob_email, charlie_email, dobert_email]
var names = [ann_name, bob_name, charlie_name, dobert_name]


// Ann
request(app)
    .post('/match/searches')
    .query({
        email: ann_email
    })
    .send({
        "search_name": "Ann search",
        "activity": [
            "groceries",
            "entertainment",
            "bulk buy"
        ],
        "location_name": "North Pole",
        "location_long": 135,
        "location_lati": 90,
        "max_range": 10,
        "max_budget": 100
    })

// Bob
request(app)
    .post('/match/searches')
    .query({
        email: ann_email
    })
    .send({
        "search_name": "Bob search",
        "activity": [
            "groceries",
            "bulk buy"
        ],
        "location_name": "North Pole",
        "location_long": 135.1,
        "location_lati": 90.1,
        "max_range": 10,
        "max_budget": 110
    })

// Charlie
request(app)
    .post('/match/searches')
    .query({
        email: ann_email
    })
    .send({
        "search_name": "Charlie search",
        "activity": [
            "groceries",
        ],
        "location_name": "North Pole",
        "location_long": 134.9,
        "location_lati": 89.9,
        "max_range": 10,
        "max_budget": 90
    })

// Dob
request(app)
    .post('/match/searches')
    .query({
        email: dobert_email
    })
    .send({
        "search_name": "Dob search",
        "activity": [
            "groceries",
            "entertainment",
            "bulk buy"
        ],
        "location_name": "North Pole",
        "location_long": 135,
        "location_lati": 90,
        "max_range": 10,
        "max_budget": 100
    })


async function initializeDatabase() {
    await user_collection.insertMany([{ name: names[0], email: emails[0] }, { name: names[1], email: emails[1] }, { name: names[2], email: emails[2] }, { name: names[3], email: emails[3] }])
}
async function resetDatabase() {
    await user_collection.deleteMany({})
}

beforeAll(() => {
    initializeDatabase();
});

afterAll(() => {
    resetDatabase();
});


describe('Tests for algorithm submodule', function () {

    it.only('Anns match list, expect [Bob]', async function () {
        const response = await request(app)
            .get('/match/suggestions')
            .query({
                email: ann_email
            })
        expect(response.status).toEqual(200);
        expect(JSON.parse(response.text)).toEqual([bob_email])
    })

    it('Bobs match list, expect [Ann, Charlie]', async function () {
        const response = await request(app)
            .get('/match/suggestions')
            .query({
                email: ann_email
            })
        expect(response.status).toEqual(200);
        expect(JSON.parse(response.text)).toEqual([ann_email, charlie_email])
    })

    it('Charlies match list, expect []', async function () {
        const response = await request(app)
            .get('/match/suggestions')
            .query({
                email: ann_email
            })
        expect(response.status).toEqual(200);
        expect(JSON.parse(response.text)).toEqual([])
    })

})