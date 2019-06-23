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

## Arguments



## Example json mapping with the extension enabled
```json

```