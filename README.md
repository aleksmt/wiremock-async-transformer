# WireMock Async Transformer
Make asynchronous request after the response. When just the response simply not enough. 

## Intro
The reason why the extension exists: sometimes you need to perform async request after 
the response, e.g. to invoke remote webhook. This transformer allows you to do that by creating a
separate thread, which is waiting for the given interval of time and then performs a request.

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

## Arguments


## Example json mapping with the extension enabled
```json

```