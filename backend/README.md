# Shopeer Backend

## Directory Structure & Test file locations

- Config Folder
    - server.js
    - apps.js
    - mongodb_connection.js

- Match Folder
    - searches.js
    - searches.test.js
    - suggestions_algo.js
    - suggestions_algo.test.js

- User Folder
    - profile.js
    - profile.test.js
    - peers.js
    - peers.test.js

- Chat Folder
    - rooms.js
    - message.js

### How to run backend tests
```
cd /Shopeer/backend
npm test
```

### Location of .yml files that run all your back-end test in GitHub Actions.
```
/Shopeer/.github/workflows/github-actions-db.yml
```

## Designated test modules and use cases
- the two back-end modules specified by TA for your project are: 
    - User
    - Matches
- the three use cases specified by TA for your project are:
    - Manage searches + browse users
    - Modify profile
    - Manage peers
