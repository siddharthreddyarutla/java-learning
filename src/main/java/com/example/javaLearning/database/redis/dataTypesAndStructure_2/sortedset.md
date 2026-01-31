```shell





127.0.0.1:6379> ZADD 5039_sortedset "string1" "string2" "string3"
(error) ERR syntax error
127.0.0.1:6379> ZADD 5039_sortedset ["string1" "string2"]
Invalid argument(s)
127.0.0.1:6379> ZADD 5039_sortedset 10 "string1" 20 "string2"
(integer) 2
127.0.0.1:6379> ZRANDMEMBER 5039_sortedset 0 -1 WITHSCORES
(error) ERR syntax error
127.0.0.1:6379> ZRANGE 5039_sortedset 0 -1 WITHSCORES
1) "string1"
2) "10"
3) "string2"
4) "20"
127.0.0.1:6379> ZRANGE 5039_sortedset 0 -1
1) "string1"
2) "string2"
127.0.0.1:6379> ZREM 5039_sortedset string1
(integer) 1
127.0.0.1:6379> ZREM 5039_sortedset string2
(integer) 1
127.0.0.1:6379> ZSCORE 5039_sortedset string1
(nil)
127.0.0.1:6379> ZSCORE 5039_sortedset string3
(nil)
127.0.0.1:6379> ZSCORE 5039_sortedset 1
(nil)
127.0.0.1:6379> ZRANGE 5039_sortedset 0 -1 WITHSCORES
(empty array)
127.0.0.1:6379> ZADD 5039_sortedset 10 "string1" 20 "string2"
(integer) 2
127.0.0.1:6379> ZSCORE 5039_sortedset string2
"20"
127.0.0.1:6379> ZRANK 5039_sortedset string1
(integer) 0
127.0.0.1:6379> ZRANK 5039_sortedset string2
(integer) 1
127.0.0.1:6379> type 5039_sortedset
zset
127.0.0.1:6379> OBJECT ENCODING 5039_sortedset
"listpack"
127.0.0.1:6379> 

```