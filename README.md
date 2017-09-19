#feign.cache
Library for caching feign request.

Using proxy pattern for wrap feign interface.
 
Standard feign interface 

```java
    public interface IUserRest {
        @RequestLine("GET /v1/user/{uid}")
        @RestCache(cacheTime = 5000)
        User getUser(@Param("uid") long uid);
        
        @RequestLine("GET /v1/user/full/{uid}")
        User getUserFull(@Param("uid") long uid);
    }
 ```   
Annotation **@RestCache** on method enable caching for this method.
Can set caching time. Default is 5000ms.

For using cache for feign you must wrap REST client.
  
 #### Without cache
 ```java
    IUserRest userCollector = 
                 Feign.builder().target(IUserRest.class, url);
  ```
 #### Without cache
 ```java
    IUserRest userCollector = 
        RestClientCache.wrap(
                Feign.builder().target(IUserRest.class, url);
        );    
 ```
