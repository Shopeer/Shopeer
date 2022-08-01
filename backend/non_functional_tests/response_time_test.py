import time
import unittest
import requests

USERNAME = "testUser"
EMAIL_TAG = "@pythontest.com"
NUM_USERS = 20
NUM_TRIALS = 100
SUGG_URL = "http://20.230.148.126:8080/match/suggestions"
SEARCH_URL = "http://20.230.148.126:8080/match/searches"
USER_URL = "http://20.230.148.126:8080/user/registration"

class TestResponseTime(unittest.TestCase):
    def test(self):
        print("Starting Test")
        time_total = 0
        make_users()

        # Test 100 times
        for i in range(NUM_TRIALS):
            if (i%10 == 0):
                print("Tested: {x}...".format(x=i))
            start_time = time.time()
            test_get_search()
            time_total += (time.time() - start_time)
        print("completed {x} trials".format(x=NUM_TRIALS))

        average_time = time_total / NUM_TRIALS

        delete_users()
        print("---------\n")
        print("average time per search:{x:.4f}s".format(x=average_time))
        self.assertTrue(average_time < 3.0, "Response time should be less than 3s")



def make_users():
    print("Making {x} users".format(x=NUM_USERS))
    for i in range(NUM_USERS):
        email = USERNAME + str(i) + EMAIL_TAG
        params = {"name": USERNAME + str(i), "email":email}
        # Makes the user
        requests.post(USER_URL, params=params)
        # Creates Search for user
        post_search(email)

def post_search(email):
    params = {"email":email, "search":"search1"}
    data = { "search":
                {
                    "search_name": "um",
                    "activity": "movies",
                    "location": [49, 49],
                    "max_range": 10,
                    "max_budget":100
                }
            }
    requests.post(SEARCH_URL, params=params, json=data)

def delete_users():
    print("Deleting {x} users".format(x=NUM_USERS))
    for i in range(NUM_USERS):
        email = USERNAME + str(i) + EMAIL_TAG
        params = {"email":email}
        requests.delete(USER_URL, params=params)

def test_get_search():
    email = USERNAME + str(time.time()%20) + EMAIL_TAG
    # Call Api function to get searches
    params = {"email":email}
    res = requests.get(SUGG_URL, params=params)
    print(res)


if __name__ == '__main__':
    unittest.main()
