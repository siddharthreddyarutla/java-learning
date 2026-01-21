# VPN & VPC — Short Notes

## 1️⃣ What is a VPC (Virtual Private Cloud)?

* A **VPC** is a **logically isolated private network** inside a cloud provider.
* Used to host **servers, databases, internal services**.
* Uses **private IP ranges**:

    * `10.0.0.0/8`
    * `172.16.0.0/12`
    * `192.168.0.0/16`
* Not accessible from the public internet by default.

---

## 2️⃣ Why Companies Use VPN

* To **securely access private servers** inside a VPC.
* To avoid exposing servers to the public internet.
* To allow **employees, developers, admins** to access internal systems.
* To protect sensitive data using **encryption**.

---

## 3️⃣ What is a VPN?

* **VPN (Virtual Private Network)** creates a **secure, encrypted tunnel** over the internet.
* It connects:

    * User device ↔ Company private network (VPC)
* Makes the user device act as if it is **inside the private network**.

---

## 4️⃣ How VPN Works (Internal Flow)

1. User starts VPN client.
2. User is authenticated (password / certificate / MFA).
3. Encrypted tunnel is created.
4. Device gets a **virtual private IP**.
5. Routing tables are updated.
6. Traffic to private IPs goes through VPN tunnel.

---

## 5️⃣ VPN Traffic Flow

```
Laptop
 → VPN Client (Encrypt)
 → Internet
 → VPN Gateway
 → Decrypt
 → VPC Internal Network
 → Server
```

---

## 6️⃣ Why VPN is Needed for Private Servers

* Private IPs **cannot be routed on the internet**.
* Without VPN:

    * ❌ Request fails
* With VPN:

    * ✅ Request reaches internal server

---

## 7️⃣ Types of VPN

### a) Client-to-Site VPN

* Laptop → VPC
* Used by developers & admins
* Most common type

### b) Site-to-Site VPN

* Office Network → VPC
* Used for hybrid cloud setups

---

## 8️⃣ Why Not Expose Servers Publicly?

**Problems:**

* Port scanning
* Brute-force attacks
* DDoS attacks
* Exploits

**VPN Advantage:**

* Servers are invisible to the internet
* Network-level security
* Access only for authenticated users

---

## 9️⃣ VPN vs Public Access (Comparison)

| Feature             | Public Server | VPN Access |
| ------------------- | ------------- | ---------- |
| Internet Accessible | Yes           | No         |
| Encryption          | Optional      | Mandatory  |
| Security Level      | Medium        | High       |
| Attack Surface      | Large         | Very Small |

---

## 🔟 Simple Analogy

* **VPC** → Private office building
* **VPN** → Secure entry pass
* **Server** → Internal room
* **Internet** → Public road

---

## 🔑 Key Takeaways

* VPC = private cloud network
* VPN = secure tunnel to VPC
* Private IPs are unreachable without VPN
* VPN protects internal services
* Used heavily in enterprises

---
