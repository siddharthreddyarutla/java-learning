```shell

127.0.0.1:6379> LPUSH 5039_list 1,2,3
(integer) 1
127.0.0.1:6379> get 5039_list
(error) WRONGTYPE Operation against a key holding the wrong kind of value
127.0.0.1:6379> LLEN 5039_list
(integer) 1
127.0.0.1:6379> LRANGE 5039_list 0 -1
1) "1,2,3"
127.0.0.1:6379> LINDEX 5039_list 1
(nil)
127.0.0.1:6379> LINDEX 5039_list 0
"1,2,3"
127.0.0.1:6379> TYPE 5039_list
list
127.0.0.1:6379> OBJECT ENCODING 5039_list
"quicklist"
127.0.0.1:6379> LPUSH 5039_list 4
(integer) 2
127.0.0.1:6379> LRANGE 5039_list 0 -1
1) "4"
2) "1,2,3"
127.0.0.1:6379> RPUSH 5039_list 5
(integer) 3
127.0.0.1:6379> LRANGE 5039_list 0 -1
1) "4"
2) "1,2,3"
3) "5"
127.0.0.1:6379> LPOP 5039_list
"4"
127.0.0.1:6379> LRANGE 5039_list 0 -1
1) "1,2,3"
2) "5"
127.0.0.1:6379> RPOP 5039_list 1
1) "5"
127.0.0.1:6379> RPOP 5039_list 1
1) "1,2,3"
127.0.0.1:6379> LRANGE 5039_list 0 -1
(empty array)
127.0.0.1:6379> LLEN 5039_list
(integer) 0
127.0.0.1:6379> 

```