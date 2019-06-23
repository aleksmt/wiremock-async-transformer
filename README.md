# WireMock Async Transformer
Make asynchronous request after the response. When just the response simply not enough. 

## Intro
The reason why the extension exists: sometimes you need to perform async request after 
the response, e.g. to invoke remote webhook. 

Typically, it's not required during the automated tests. But if you want to mock something 
on the pre-production system very "quick and dirty", the solution might be helpful. 

So you don't have to "hold" some additional runtime, async request "from the box" doing
what you want.

This transformer creates separated thread, which waiting for the given interval of time 
and then performs a HTTP request. 

```text
    +-----------+  request           +------------+
    |           | -(1)-------------> |            |
    |  System1  |                    |  WireMock  |
    |           | <------------(2)-- |            |
    +-----------+  mocked response   +------------+
                                            |
                                  [after n sec. delay]
                                            |
    +-----------+                          (3)
    |           |                           |
    |  System2  |  <------------------------+
    |           |       async request
    +-----------+


```
e.g. delay = 5 sec., then the request (3) will be performed after the 5 seconds after response (2)

## Adding to the WireMock
There is many possible ways to add the extension to your WireMock server, e.g.:

- [This article explains everything](http://wiremock.org/docs/extending-wiremock), or...
- ...[How to add an extension to the docker container](https://github.com/rodolpheche/wiremock-docker#use-wiremock-extensions)

## Arguments and transformer name

Transformer name: `async-request`, use this name in "transformers" json section of the mapping

| Argument Key  | Description (Request)                                             |  
|---------------|-------------------------------------------------------------------|  
| url           | URL, starting from "http://", incl. full path                     |  
| body          | String representation of the async request body                   |  
| method        | HTTP method: GET, POST, PUT, DELETE, PATCH, etc.                  |  
| delay         | Wait for the given amount of milliseconds before the request      |  
  


## Example json mapping with the extension enabled
In this example we're going to use the WireMock itself in order to catch the async request 

- Create new mapping with the following json (change localhost if you have different hostname/ip):
```json
{
    "request": { 
        "urlPattern": "^/example/async/initiate.*$",
        "method": "ANY"
    },
    "response": {
        "status": 200,
        "headers": {
            "Content-Type": "application/json"
        },
        "body": "{\"status\": \"This is usual sync response\"}",
        "transformers": [
            "async-request"
        ],
        "transformerParameters": {
            "method": "POST",
            "url": "http://localhost/example/async/request",
            "body": "This is my async-request plugin request",
            "headers": {
                "X-Async-Request": true
            },
            "delay": 5000
        }
    }
}
```

- Send any request to the WireMock endpoint: `example/async/initiate`
- Wait for a 5 seconds
- Check your `GET /__admin/requests` log, you should find two requests (second is made by async-request)