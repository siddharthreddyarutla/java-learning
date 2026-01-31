```shell
127.0.0.1:6379> get 5039_string_int
"10"
127.0.0.1:6379> type 5039_string_int
string
127.0.0.1:6379> INCR 5039_string_int
(integer) 11
127.0.0.1:6379> OBJECT ENCODING 5039_string_int
"int"
127.0.0.1:6379> DECR 5039_string_int
(integer) 10
127.0.0.1:6379> SET 5039_string_embstr string testing
(error) ERR syntax error
127.0.0.1:6379> SET 5039_string_embstr "string testing"
OK
127.0.0.1:6379> get 5039_string_embstr
"string testing"
127.0.0.1:6379> type 5039_string_embstr
string
127.0.0.1:6379> OBJECT ENCODING 5039_string_embstr
"embstr"
127.0.0.1:6379> set 5039_string_raw "This is a sample string designed to be over 44 bytes."
OK
127.0.0.1:6379> get 5039_string_raw
"This is a sample string designed to be over 44 bytes."
127.0.0.1:6379> type 5039_string_raw
string
127.0.0.1:6379> OBJECT ENCODING 5039_string_raw
"raw"
127.0.0.1:6379> INCRBY 5039_string_int 5
(integer) 15
127.0.0.1:6379> STRLEN 5039_string_embstr
(integer) 14
127.0.0.1:6379> STRLEN 5039_string_raw
(integer) 53
127.0.0.1:6379> APPEND 5039_string_embstr "appended later"
(integer) 28
127.0.0.1:6379> GETRANGE 5039_string_embstr 0 5
"string"
127.0.0.1:6379> GETRANGE 5039_string_embstr 0 10
"string test"
127.0.0.1:6379> SETRANGE 5039_string_embstr 0 updated
(integer) 28
127.0.0.1:6379> get 5039_string_embstr
"updatedtestingappended later"
127.0.0.1:6379> 

```