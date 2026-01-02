## 1) Forward vs Reverse proxy — which is nginx here?

This nginx is acting as a **reverse proxy / edge gateway**.

* **Reverse proxy**: it *accepts client requests on behalf of many internal services*, routes them to appropriate internal servers (the `alb.peoplehum.local:PORT` backends or `ph-app3`), and then returns responses to the client.
* It is **not** a forward proxy (which clients explicitly configure to proxy outbound requests).

So nginx is the public face (TLS termination, routing, security, rate limiting, gzip) and it forwards requests to internal application servers.

---

## 2) TLS / SSL — what nginx does and what it **doesn't** do

### What nginx *does* (from your config)

* **Terminates TLS** for inbound client connections on port `443`. Each `server { listen 443 ssl; }` block sets:

    * `ssl_certificate /etc/ssl/bundle.crt;`
    * `ssl_certificate_key /etc/ssl/peoplehum.key;`
    * `ssl_protocols TLSv1.2;` (only TLS 1.2 allowed)
* For any HTTP request on port 80, nginx responds with `301` redirect to `https://...` (enforcing HTTPS).

When a client connects:

1. Client performs TLS handshake with nginx.
2. nginx presents the configured certificate (`/etc/ssl/bundle.crt`) to the client.
3. If the client trusts the certificate chain, TLS is established and encrypted HTTP traffic flows to nginx.

**Conclusion:** nginx performs TLS termination and is responsible for the server certificate presented to clients.

### What nginx does *not* do by default in this config

* **It does not validate upstream (backend) SSL certificates** in locations where `proxy_pass` targets `https://` backends — because there is **no** `proxy_ssl_verify on;` or `proxy_ssl_trusted_certificate` directive present.

    * Example: some `proxy_pass https://cop.peoplehum.com/;` calls appear; nginx sends requests to that upstream over HTTPS if `proxy_pass https://...`, but it won’t verify the upstream cert unless `proxy_ssl_verify on;` is configured.
* Where `proxy_pass` targets `http://...` (the common case to `alb.peoplehum.local:1924` etc.), there is **no upstream TLS** — nginx communicates in plain HTTP to internal services (TLS is only client→nginx). This is typical in internal network setups.

**If you want nginx to validate upstream certs**, add:

```nginx
proxy_ssl_server_name on;
proxy_ssl_verify on;
proxy_ssl_trusted_certificate /path/to/ca-bundle.pem;
```

---

## 3) How routing works — `server` block → `location` → `proxy_pass` (concrete flow)

 Take a real example request to see the flow:

### Example request:

`GET https://api.peoplehum.com/api/users/123?active=true`

**Request flow:**

1. DNS resolves `api.peoplehum.com` to the nginx public IP.
2. Client connects to nginx:443, TLS handshake occurs with the nginx certificate.
3. nginx chooses the `server` block where `server_name api.peoplehum.com` matches.
4. nginx sets `$uuid` from `map $http_x_request_id $uuid { ... }`:

    * If client included `X-Request-Id`, nginx will use it; otherwise `$request_id` is used.
5. Header variables set in server block:

    * `proxy_set_header X-PH-Tracking-ID $uuid;`
    * `proxy_set_header X-PH-Remote-IP $http_x_forwarded_for;` (so backend sees client forwarding chain)
    * `add_header X-PH-Tracking-ID $uuid;` (also returned to client responses)
6. `location ~ ^/api/(.*)` matches. nginx executes location block for `/api/...`.
7. `proxy_pass http://$alb:1924/$1$is_args$args;`

    * `$alb` is set to `alb.peoplehum.local` in that server block, so effectively `http://alb.peoplehum.local:1924/users/123?active=true`
    * nginx rewrites the URL using the `$1` capture group and preserves query string (`$is_args$args`).
8. nginx opens a connection to the upstream `alb.peoplehum.local:1924`, forwards the client request, and streams the upstream response back to the client.

**Notes about `proxy_pass` & URI rewriting:**

* `proxy_pass http://$alb:1924/$1$is_args$args;` uses the captured group from the location regex (`(.*)`), so the path that reaches the upstream is the portion after `/api/`.
* If `proxy_pass` had been `proxy_pass http://$alb:1924;` without a URI, nginx would pass the full original request URI.

---

## 4) Upstream / load balancing & fail parameters

Your config defines an upstream:

```nginx
upstream email-sync-service-callback {
  least_conn;
  server ph-app3.peoplehum.local:8123 max_fails=3 fail_timeout=60 weight=1;
}
```

* `least_conn;` — nginx will choose the backend with the fewest active connections (good for long-lived requests / websockets).
* `server ... max_fails=3 fail_timeout=60` — if an upstream fails 3 consecutive attempts, it is considered failed for 60s (nginx will avoid sending requests to it during fail window).
* `weight=1` — load weight, only matters when multiple servers listed.

So when `proxy_pass http://email-sync-service-callback/...` is used, nginx will balance using least connections and perform simple failure detection.

---

## 5) Headers & tracking

Important headers in config:

* `map $http_x_request_id $uuid { ... }` creates `$uuid` from either the incoming `X-Request-ID` header or nginx’s generated `$request_id`.
* `proxy_set_header X-PH-Tracking-ID $uuid;` forwards the tracking id to upstreams so all services can correlate logs per request id.
* `add_header X-PH-Tracking-ID $uuid;` sends tracking id back to clients (useful for support & tracing).
* `proxy_set_header X-PH-Remote-IP $http_x_forwarded_for;` sets an upstream header showing the original `X-Forwarded-For` value. (Note: if you want the immediate client IP, `$remote_addr` or `$http_x_forwarded_for` processing is necessary — make sure upstream reads the header appropriately.)

---

## 6) Buffering, timeouts, long-polling & websocket support

Your config includes:

* `proxy_read_timeout 3600s;` and `fastcgi_read_timeout 3600s;` — nginx will wait up to 1 hour for upstream responses. Useful for long-poll or long-running requests.
* For socket/long-poll (e.g., Socket.IO), you have explicit handling:

  ```nginx
  proxy_http_version 1.1;
  proxy_set_header Upgrade $http_upgrade;
  proxy_set_header Connection "upgrade";
  ```

  This allows upgrading the connection to WebSocket for long-lived bi-directional connections.
* `proxy_buffer_size`, `proxy_buffers`, `proxy_busy_buffers_size` are tuned (128k, 4×256k, etc.) to handle larger responses without blocking — good for payloads or streaming.

---

## 7) Rate limiting — global IP and per-customer 20 req/s explained

There are two `limit_req_zone` definitions:

```nginx
limit_req_zone $binary_remote_addr zone=mylimit:10m rate=20r/s;
limit_req_zone $customer_id zone=CUSTOMER_ID:10m rate=20r/s;
```

### a) `zone=mylimit` (global per IP)

* `key` = `$binary_remote_addr` (client IP in a compact binary form).
* `rate=20r/s` — allows **20 requests per second per client IP**.
* This zone is used in `devapi` server block:

  ```nginx
  location / {
      limit_req zone=mylimit burst=20 nodelay;
      limit_req_status 429;
      proxy_pass http://$alb:1959;
   }
  ```

    * `burst=20` allows short bursts (queue size) up to 20 extra requests; with `nodelay`, bursts are *not* delayed — requests within burst are processed immediately until the token bucket is exceeded.
    * If exceeded, nginx returns **HTTP 429** (set by `limit_req_status 429;`).
    * So for devapi endpoints, **per IP** limit is enforced.

### b) `zone=CUSTOMER_ID` (per customer extracted from URI)

* `key` = `$customer_id` (variable you set via `if` rules).
* The config sets `$customer_id` like this inside the `devapi` server block:

  ```nginx
  if ($request_uri ~* /customer/([0-9]+)){
       set $customer_id $1;
  }
  if ($request_uri ~* /customerId/([0-9]+)){
       set $customer_id $1;
  }
  ```

    * This extracts a numeric customer id from the URI when it matches `/customer/<id>` or `/customerId/<id>`.
* Later, in specific locations:

  ```nginx
  location ~ /customer/ {
       limit_req zone=CUSTOMER_ID burst=20 nodelay;
       limit_req_status 429;
       proxy_pass http://$alb:1959;
    }
  ```

    * This means **20 requests per second per customer id** (as extracted from the request path), with `burst=20` and immediate rejection above the burst.
    * So yes — **per-customer 20 req/s** is applied where the `limit_req` line using `CUSTOMER_ID` is present. Only requests where `$customer_id` is set (URI pattern matched) will be rate-limited by customer id; otherwise the key is empty and rate limiting will be based on empty key (not desired). The `if` extraction must run before `limit_req` evaluation; as written it will work in many nginx versions but using `map` to set `$customer_id` is more robust.

### Summary of exact behavior:

* Where `limit_req zone=mylimit` is used: limit 20r/s per client IP.
* Where `limit_req zone=CUSTOMER_ID` is used: limit 20r/s per numeric customer id extracted from URI.
* If both are used in different locations, both limits may apply depending on which location the request hits.

---

## 8) Other functionality in the config

* **HSTS**: `add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;` forces browsers to use HTTPS for 1 year.
* **Swagger / docs blocking**: `location ~ /(swagger-ui.html|api-docs|api/access|api/authAccess)` returns `404` — prevents exposing internal API docs.
* **Static fallback / error page**: `error_page ... /ph_error.html;` with internal file under `/usr/share/nginx/html`.
* **Large uploads**: `client_max_body_size 100m;` allows payloads up to 100MB — useful for file uploads.
* **Gzip compression**: enabled for many content types to reduce payload size.
* **Proxy redirects rewrite**: `proxy_redirect` rules in `portal` server rewrite upstream domain redirects back to the public domain.
* **S3 proxy**: certain static files served from S3 (apple app association) via `proxy_pass https://s3...`.
* **Cookie set for apicop**: `proxy_set_header Cookie "__session=$cookie_token";` passes a specific cookie to upstream `api-cop.peoplehum.com`.

---

## 9) Pitfalls / improvements to consider (practical suggestions)

* **Upstream TLS verification**: add `proxy_ssl_verify on;` and `proxy_ssl_trusted_certificate` if you rely on upstream HTTPS and want nginx to verify their certs.
* **Avoid `if` for variable extraction**: use `map` to set `$customer_id` from `$request_uri` (more reliable and avoids pitfalls of `if` in nginx).
* **Ensure `$http_x_forwarded_for` correctness**: if nginx is at edge, `$remote_addr` is client IP; `$http_x_forwarded_for` could already contain proxies. Consider `real_ip` module if behind a load balancer.
* **Limit key fallback**: make sure `$customer_id` evaluates to something safe when not present to avoid many requests sharing the same empty key.
* **Lockdown sensitive endpoints**: add stricter allow/deny or mTLS for admin or internal endpoints.
* **Observability**: log the `$uuid` and customer id in access logs to correlate rate limiting and tracing.

---

## 10) Quick recap — one-line summary

Your nginx is a **reverse proxy / TLS-terminating edge gateway** that:

* receives HTTPS requests from clients,
* applies HSTS/gzip/security headers,
* maps or redirects traffic via `location` rules to internal backends (`alb.peoplehum.local:PORT` or named upstreams),
* forwards a request-tracking header (`X-PH-Tracking-ID`) and client IP to backends,
* supports websocket upgrades for Socket.IO/long polling,
* enforces rate limits both **per-client IP** and **per-customer (20 req/s)** where configured,
* and returns responses (or 429 errors / internal error page) back to the client.

---

If you want, I can:

* Produce a **step-by-step example** tracing headers & request body for a sample API call (including exact upstream URL constructed).
* Convert your `if` customer extraction into a safer `map` directive and show the exact `limit_req` semantics.
* Add `proxy_ssl_verify` snippets and the recommended `real_ip` + improved access_log line to log `$uuid` and `$customer_id`.

Which of those do you want next?
