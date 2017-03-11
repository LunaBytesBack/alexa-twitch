# Twitch Alexa Streamer Status
## About this project
This is just a sample project for creating an Amazon Alexa skill for asking whether your favourite Twitch streamer is currently live. Feel free to add more functionality to this skill if you want to. This skill was created for hosting on Amazon Lambda. You can find more information on how to deploy an Alexa skill on Amazon Lamda [here](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/deploying-a-sample-skill-to-aws-lambda).

## How to get this skill to run
You need to provide three piece of information in order to run this skill:

* A client id obtained from twich (see [here](https://blog.twitch.tv/client-id-required-for-kraken-api-calls-afbb8e95f843))
* The channel id of channel you want to retrieve live status of
* Application id of Alexa skill

You can obtain the channel id by the following command:
```
curl -H 'Accept: application/vnd.twitchtv.v5+json' -H 'Client-ID: _your_client_id_' -X GET https://api.twitch.tv/kraken/users?login=_streamer_name_
```

## License
>Copyright 2017 Fabian Krone
>
>Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
>
>The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
>
>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
