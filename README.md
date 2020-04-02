# Reply Guy

Twitter bot that responds to mentions.

To execute locally:

```shell script
$ sbt run
```

Reply Guy requires the following configuration parameters:

| Environment Variable                  | Description 
| ------------------------------------- | -----------
| REPLY_GUY_TWITTER_API_KEY             | Twitter developer API key.
| REPLY_GUY_TWITTER_SECRET_KEY          | Twitter developer secret key.
| REPLY_GUY_TWITTER_ACCESS_TOKEN        | Twitter developer access token.
| REPLY_GUY_TWITTER_ACCESS_TOKEN_SECRET | Twitter developer access token secret.
| REPLY_GUY_DECKARD_URI                 | URI for Deckard server

## Deploy

To setup Heroku execute:

```shell script
$ heroku create
$ heroku config:set REPLY_GUY_TWITTER_API_KEY=<...>
$ heroku config:set REPLY_GUY_TWITTER_SECRET_KEY=<...>
$ heroku config:set REPLY_GUY_TWITTER_ACCESS_TOKEN=<...>
$ heroku config:set REPLY_GUY_TWITTER_ACCESS_TOKEN_SECRET=<...>
```

To deploy to Heroku execute:

```shell script
$ git push heroku
$ heroku ps:scale worker=1
```
