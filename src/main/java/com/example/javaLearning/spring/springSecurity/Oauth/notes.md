# 🌐 OAuth2 / SSO — Explained With a REAL Example

Assume:

* You built **MyApp**
* You want **“Login with Google”**
* User has a Google account

---

## 0️⃣ One-time setup (VERY IMPORTANT)

Before any user logs in, **YOU register your app with Google**.

### You tell Google:

* **App name**: MyApp
* **Redirect URL**:

  ```
  https://myapp.com/oauth/callback
  ```
* Google gives YOU:

    * **Client ID**
    * **Client Secret**

👉 This is how **Google knows you are a trusted app**.

Think of it as:

> “Google, this is my website. Only redirect users back to this URL.”

---

## 1️⃣ User clicks “Login with Google”

User is on:

```
https://myapp.com/login
```

They click **Login with Google**.

👉 Your frontend does NOT authenticate.

Instead, it **redirects the browser** to Google:

```
https://accounts.google.com/o/oauth2/v2/auth
  ?client_id=ABC123
  &redirect_uri=https://myapp.com/oauth/callback
  &response_type=code
  &scope=openid email profile
```

### What did you send?

* **client_id** → identifies *your app*
* **redirect_uri** → where Google should come back
* **scope** → what user data you want

❗ No password, no token yet.

---

## 2️⃣ How does Google know this redirect is safe?

Google checks:

* Is this **client_id valid?**
* Does the **redirect_uri EXACTLY match** what you registered?

If mismatch → ❌ blocked
If match → ✅ continue

👉 This is how Google prevents phishing.

---

## 3️⃣ User logs in on Google page

Now user sees:

```
accounts.google.com
```

They:

* Enter Google email & password
* Maybe do MFA

✔️ **Only Google sees credentials**
❌ Your app NEVER sees password

---

## 4️⃣ Google redirects BACK to your app (important)

If login succeeds, Google redirects browser to:

```
https://myapp.com/oauth/callback?code=XYZ987
```

### What is this `code`?

* **Authorization Code**
* Short-lived
* One-time use
* Proves:

  > “User authenticated successfully at Google”

❗ This is NOT an access token yet.

---

## 5️⃣ Your backend now talks to Google (server → server)

Your backend receives the `code`.

Now it calls Google directly:

```
POST https://oauth2.googleapis.com/token
```

With:

* client_id
* client_secret
* authorization_code
* redirect_uri

👉 This proves **YOU are the same app** that initiated login.

---

## 6️⃣ Google responds with TOKENS

Google sends back:

```json
{
  "access_token": "google-access-token",
  "refresh_token": "google-refresh-token",
  "id_token": "google-id-token",
  "expires_in": 3600
}
```

Now let’s break these down 👇

---

## 7️⃣ What each token is for (THIS IS KEY)

### 🔹 Access Token (Google’s)

* Used to call **Google APIs**
* Example:

  ```
  GET https://www.googleapis.com/oauth2/v2/userinfo
  Authorization: Bearer <access_token>
  ```
* Short-lived (1 hour)

👉 This token is **for Google**, not your app.

---

### 🔹 ID Token (JWT)

* This is how **you get user identity**
* Contains:

    * email
    * name
    * Google user id

You decode it and get:

```
email = john@gmail.com
```

👉 THIS is how you know **who the user is**.

---

### 🔹 Refresh Token (Google’s)

* Long-lived
* Stored **securely in your backend**
* Used to get new Google access tokens
* NEVER sent to frontend

---

## 8️⃣ How do you map user in YOUR system?

Now you have:

```
email = john@gmail.com
```

Your backend does:

1. Check DB:

    * Does user with this email exist?
2. If not:

    * Create user
    * Mark auth type = GOOGLE
3. If yes:

    * Use existing user

👉 Email (or provider user id) is the **link**.

---

## 9️⃣ Why your app issues its OWN JWT

After mapping user:

* You generate **YOUR JWT**
* This is what your APIs trust

```
Authorization: Bearer <myapp-jwt>
```

Your services:

* NEVER see Google tokens
* NEVER call Google

Google is only used for **login**.

---

## 🔟 Full token ownership summary

| Token                | Issued by    | Used by       | Purpose            |
| -------------------- | ------------ | ------------- | ------------------ |
| Auth Code            | Google       | Your backend  | Proof of login     |
| Google Access Token  | Google       | Your backend  | Call Google APIs   |
| Google Refresh Token | Google       | Your backend  | Renew Google token |
| Google ID Token      | Google       | Your backend  | Get user identity  |
| **Your JWT**         | **Your app** | Client → APIs | App authorization  |

---

## 1️⃣1️⃣ Where is JWT stored on client?

Best practice:

* **HttpOnly Secure Cookie**
  or
* In-memory (SPA)

Never store:

* Google refresh token on client ❌

---

## 1️⃣2️⃣ Is this “OAuth Code Flow”?

YES ✅
This is **Authorization Code Flow + OpenID Connect**.

* OAuth2 → authorization
* OpenID Connect → identity (`id_token`)

---

## 1️⃣3️⃣ Does Keycloak work the same way?

### ✅ YES — exactly the same

Keycloak:

* Replaces Google
* Acts as **Identity Provider**
* Issues:

    * access token
    * refresh token
    * id token

Your app flow is **identical**.

Difference:

* You control Keycloak
* Google is external

---

## 1️⃣4️⃣ One-line mental model (REMEMBER THIS)

> Google/Keycloak authenticates the user and proves identity, then my application issues its own JWT for internal authorization.

---

## 1️⃣5️⃣ Interview-ready answer (PERFECT)

> In SSO, the client redirects the user to an identity provider with a client ID and redirect URI. After authentication, the provider returns an authorization code, which the backend exchanges for tokens. The ID token provides user identity, which is mapped to an internal user, and the application issues its own JWT for API access.

---

## ✅ Final clarity check

* ✔️ Client ID identifies your app
* ✔️ Redirect URI prevents phishing
* ✔️ Google authenticates user
* ✔️ ID token gives user identity
* ✔️ You map user by email/id
* ✔️ You issue your own JWT
* ✔️ Keycloak works the same way

---

If you want next:

* Draw this as **sequence diagram**
* Map this to **Spring Security OAuth2 Login**
* Compare **SSO vs normal login**
* Explain **OIDC claims**

Just say 👍
