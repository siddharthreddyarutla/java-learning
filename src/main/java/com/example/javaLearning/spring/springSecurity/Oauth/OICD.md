OpenID Connect (OIDC) is an identity layer on top of the OAuth 2.0 protocol that enables secure user authentication. It allows applications to verify user identity and obtain profile information via JSON Web Tokens (JWT). It is widely used for Single Sign-On (SSO) and social logins, allowing third-party apps to trust an identity provider like Google or Microsoft.

Key aspects of OIDC include:
* Authentication & Authorization: While OAuth 2.0 handles authorization (what a user can do), OIDC handles authentication (who the user is).
* How it Works: The client application (Relying Party) redirects the user to an OpenID Provider (OP) to log in, which then issues an ID token to the application.
* Key Components:
    * ID Token: A JWT containing user claims.
    * UserInfo Endpoint: A protected resource where the client can retrieve additional user details.
    * Flows: Uses Authorization Code, Implicit, or Hybrid flows to exchange tokens.
* Benefits: It enhances security by reducing password storage, improves user experience through SSO, and is an open, industry-standard protocol
