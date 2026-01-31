```shell
127.0.0.1:6379> SADD 5039_set "string1" "string2" "string3"
(integer) 3
127.0.0.1:6379> SISMEMBER 5039_set string2
(integer) 1
127.0.0.1:6379> SISMEMBER 5039_set string1
(integer) 1
127.0.0.1:6379> SMEMBERS 5039_set
1) "string2"
2) "string3"
3) "string1"
127.0.0.1:6379> SRANDMEMBER 5039_set 0
(empty array)
127.0.0.1:6379> SREM 5039_set string3
(integer) 1
127.0.0.1:6379> SMEMBERS 5039_set
1) "string2"
2) "string1"
127.0.0.1:6379> type 5039_set
set
127.0.0.1:6379> OBJECT ENCODING 5039_set
"hashtable"
127.0.0.1:6379> SCAN 0 "string"
(error) ERR syntax error
127.0.0.1:6379> SSCAN 127.0.0.1:6379> SMEMBERS 5039_set
(error) ERR invalid cursor
127.0.0.1:6379> SCAN 5039_set 0
(error) ERR invalid cursor
127.0.0.1:6379> SSCAN 5039_set 0
1) "0"
2) 1) "string2"
   2) "string1"
127.0.0.1:6379> SSCAN 5039_set 1
1) "0"
2) 1) "string1"
127.0.0.1:6379> SMEMBERS 5039_set
1) "string2"
2) "string1"
127.0.0.1:6379> 


```