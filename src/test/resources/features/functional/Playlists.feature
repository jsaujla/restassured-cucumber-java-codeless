@regression
Feature: Playlists

  @smoke
  Scenario: Verify Playlists. User should be able to create, get and update a playlist
    When With request headers
      | Content-Type     | Authorization  |
      | application/json | {access_token} |
    And With request body: "src/test/resources/test-data/CreatePlaylists.json"
      | name         | description              | public |
      | New Playlist | New playlist description | false  |
    When User makes a POST request to endpoint: "users/{{user_id}}/playlists"
    Then Response status code should be: 201
    And Response body should contains fields
      | id       | name         | description              | public  |
      | NOT_NULL | New Playlist | New playlist description | false   |
      |          | String       | String                   | Boolean |
    And Store response body value in variable
      | variableName                  | variableType | responsePath    |
      | playlist_id_from_post_request | String       | id              |
      | followers_total               | Integer      | followers.total |


    When With request headers
      | Content-Type     | Authorization  |
      | application/json | {access_token} |
    When User makes a GET request to endpoint: "playlists/{playlist_id_from_post_request}"
    Then Response status code should be: 200
    And Response body should contains fields
      | id                              | name         | description              | public  |
      | {playlist_id_from_post_request} | New Playlist | New playlist description | false   |
      | String                          | String       | String                   | Boolean |

    When With request headers
      | Content-Type     | Authorization  |
      | application/json | {access_token} |
    And With request body: "src/test/resources/test-data/CreatePlaylists.json"
      | name             | description                  | public |
      | Updated Playlist | Updated playlist description | false  |
    When User makes a PUT request to endpoint: "playlists/{playlist_id_from_post_request}"
    Then Response status code should be: 200

    When With request headers
      | Content-Type     | Authorization  |
      | application/json | {access_token} |
    When User makes a GET request to endpoint: "playlists/{playlist_id_from_post_request}"
    Then Response status code should be: 200
    And Response body should contains fields
      | id                              | name             | description                  | public  |
      | {playlist_id_from_post_request} | Updated Playlist | Updated playlist description | false   |
      | String                          | String           | String                       | Boolean |


  Scenario Outline: Verify create Playlists with invalid payload
    When With request headers
      | Content-Type     | Authorization  |
      | application/json | {access_token} |
    And With request body: "src/test/resources/test-data/CreatePlaylists.json"
      | name   | description   | public   |
      | <name> | <description> | <public> |
    When User makes a POST request to endpoint: "users/{{user_id}}/playlists"
    Then Response status code should be: <statusCode>
    And Response body should contains fields
      | error.status | error.message   |
      | <statusCode> | <error.message> |
      | Integer      | String          |
    Examples:
      | name | description              | public  | statusCode | error.message                |
      |      | New playlist description | false   | 400        | Missing required field: name |


  Scenario Outline: Verify create Playlists with invalid Access Token
    When With request headers
      | Content-Type     | Authorization   |
      | application/json | <authorization> |
    And With request body: "src/test/resources/test-data/CreatePlaylists.json"
      | name         | description              | public |
      | New playlist | New playlist description | false  |
    When User makes a POST request to endpoint: "users/{{user_id}}/playlists"
    Then Response status code should be: <statusCode>
    And Response body should contains fields
      | error.status | error.message   |
      | <statusCode> | <error.message> |
      | Integer      | String          |
    Examples:
      | authorization                | statusCode | error.message                              |
      | Bearer invalid_access_token  | 401        | Invalid access token                       |
      | invalid_access_token         | 400        | Only valid bearer authentication supported |
      | {{expired.access.token}}     | 401        | The access token expired                   |
