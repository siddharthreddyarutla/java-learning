# Notes: SSH (Secure Shell)

## What is SSH?

**SSH = Secure Shell**
- A **protocol** for connecting to remote computers securely
- Establishes connection over an **encrypted channel**
- All data between your machine and remote server is **protected from eavesdropping and interception**

---

## Primary Uses of SSH

1. **Remote command-line access** - Control servers from anywhere
2. **File transfers** - Securely move files between machines
3. **Tunneling traffic** - Create secure connections for other protocols

### Common Use Cases
- **System administrators**: Server maintenance, monitoring
- **Developers**: Application deployment, troubleshooting
- **DevOps**: Infrastructure management, CI/CD pipelines

---

## SSH Architecture Components

### Two Main Parts

```
┌─────────────────┐         Encrypted         ┌─────────────────┐
│   SSH CLIENT    │◄─────── Connection ──────►│   SSH SERVER    │
│ (Your computer) │                            │ (Remote server) │
└─────────────────┘                            └─────────────────┘
```

**SSH Client:**
- Your local machine
- Initiates the connection
- Sends commands and data

**SSH Server:**
- Remote machine you want to access
- Listens for incoming connections
- Executes commands and returns results

---

## Authentication Methods

### Method 1: Password Authentication

**How it works:**
```
1. Client connects to server
2. Server asks for username and password
3. Client enters credentials
4. Server verifies and grants access
```

**Characteristics:**
- ✅ **Simple** - Easy to understand and use
- ✅ **Quick setup** - No key generation needed
- ❌ **Less secure** - Passwords can be intercepted/guessed
- ❌ **Brute force attacks** - Vulnerable to automated password attempts
- ❌ **Phishing** - Users can give away passwords

**Not recommended for production servers!**

---

### Method 2: SSH Key Authentication (Recommended)

**Uses asymmetric cryptography with public/private key pair**

#### Step-by-Step Process

**Step 1: Key Generation (One-time setup)**
```
Client generates SSH key pair on local machine:
├─ Private Key (id_rsa) - Kept SECRET on client
└─ Public Key (id_rsa.pub) - Shared with server
```

**Important:**
- **Private key** = Like your house key - NEVER share it
- **Public key** = Like a padlock - Safe to share freely

**Step 2: Public Key Distribution**
```
Client → Copies public key → Server
Server stores it in: ~/.ssh/authorized_keys
```

**Step 3: Connection Attempt**
```
1. Client: "Hi, I want to connect"
   └─ Client sends username

2. Server: "Do you have the matching private key?"
   └─ Server checks if client's public key exists
```

**Step 4: Challenge-Response (The Clever Part)**
```
3. Server generates RANDOM STRING (challenge)
   └─ Server encrypts it with client's PUBLIC KEY
   └─ Sends encrypted challenge to client

4. Client receives encrypted challenge
   └─ Uses PRIVATE KEY to decrypt it
   └─ Sends decrypted answer back to server

5. Server receives answer
   └─ Uses PUBLIC KEY to verify the response
   └─ If match → Authentication successful!
```

**Step 5: Secure Session Established**
```
6. Encrypted SSH session is now active
   └─ Client can run commands
   └─ Client can transfer files
   └─ All communication encrypted
```

---

## Why Key Authentication is More Secure

### Mathematical Security

**The Magic of Asymmetric Cryptography:**

1. **One-Way Function:**
    - Public key can ENCRYPT data
    - Only private key can DECRYPT it
    - Reverse engineering is mathematically infeasible

2. **No Password Transmitted:**
    - Password auth: "Here's my password" ❌ (can be intercepted)
    - Key auth: "Here's proof I have the key" ✅ (never sends private key)

3. **Challenge-Response:**
    - Server sends random challenge each time
    - Different every connection
    - Even if intercepted, useless for future connections

### Security Comparison

| Feature | Password Auth | SSH Key Auth |
|---------|---------------|--------------|
| Can be brute-forced | ✅ Yes | ❌ No (2048-bit key = impossible) |
| Can be intercepted | ✅ Yes | ❌ Private key never transmitted |
| Vulnerable to phishing | ✅ Yes | ❌ No password to steal |
| Requires remembering | ✅ Yes | ❌ Key stored on machine |
| Can use 2FA | ✅ Yes | ✅ Yes (even more secure) |

---

## Complete SSH Connection Flow

### With Password Authentication
```
1. Client: ssh user@server.com
2. Server: "Enter password"
3. Client: enters password
4. Server: verifies password
5. Server: grants access (if correct)
6. Encrypted session established
```

### With Key Authentication
```
1. Client: ssh user@server.com
2. Server: "I'll check for your public key"
3. Server: Found public key → Send challenge
4. Client: Decrypt with private key → Send answer
5. Server: Verify answer → Match!
6. Server: Access granted
7. Encrypted session established
```

---

## Real-World Example

### Scenario: Developer deploying to production server

**Setup (Once):**
```bash
# On local machine (client)
ssh-keygen -t rsa -b 4096
# Generates: id_rsa (private) and id_rsa.pub (public)

# Copy public key to server
ssh-copy-id user@production-server.com
# Or manually: cat ~/.ssh/id_rsa.pub → paste into server's authorized_keys
```

**Daily Use:**
```bash
# Connect to server (no password needed!)
ssh user@production-server.com

# Run commands
ls -la
cd /var/www/app
git pull origin main
sudo systemctl restart nginx

# Transfer files
scp local-file.txt user@server:/remote/path/

# Exit
exit
```

---

## SSH Session Encryption

### What Gets Encrypted?
- ✅ All commands you type
- ✅ All output from server
- ✅ File transfers
- ✅ Authentication data

### Encryption Algorithms Used
- **Key exchange**: Diffie-Hellman
- **Encryption**: AES, ChaCha20
- **MAC (Message Authentication)**: HMAC-SHA2

```
Plain text command: "cat /etc/passwd"
       ↓
Encrypted: "aq34b$#@kj3h4..."
       ↓
Sent over network (safe from eavesdropping)
       ↓
Server decrypts: "cat /etc/passwd"
       ↓
Executes command
       ↓
Output encrypted: "zx92m$@#..."
       ↓
Client decrypts and displays
```

---

## Additional SSH Features

### 1. Port Forwarding (Tunneling)
```bash
# Local port forwarding
ssh -L 8080:localhost:80 user@server

# Access remote service through encrypted tunnel
```

### 2. Remote Port Forwarding
```bash
# Allow remote server to access your local service
ssh -R 9090:localhost:3000 user@server
```

### 3. SOCKS Proxy
```bash
# Route all traffic through SSH tunnel
ssh -D 1080 user@server
```

### 4. X11 Forwarding
```bash
# Run GUI applications remotely
ssh -X user@server
firefox  # Opens on your local display
```

---

## SSH Configuration Files

### Client Configuration (`~/.ssh/config`)
```
Host myserver
    HostName production-server.com
    User deployer
    Port 22
    IdentityFile ~/.ssh/id_rsa
    
# Now just use: ssh myserver
```

### Server Configuration (`/etc/ssh/sshd_config`)
```
Port 22
PermitRootLogin no
PasswordAuthentication no  # Force key auth only
PubkeyAuthentication yes
```

---

## Security Best Practices

### Client Side
1. ✅ **Use SSH keys** instead of passwords
2. ✅ **Protect private key** with passphrase
3. ✅ **Use strong key** (RSA 4096-bit or ED25519)
4. ✅ **Keep private key secure** (chmod 600)
5. ✅ **Use different keys** for different servers

### Server Side
1. ✅ **Disable password authentication**
2. ✅ **Disable root login**
3. ✅ **Change default port** (22 → something else)
4. ✅ **Use fail2ban** (blocks brute force attempts)
5. ✅ **Keep SSH updated**
6. ✅ **Limit user access** (only authorized users)
7. ✅ **Enable 2FA** for extra security

---

## Common SSH Commands

```bash
# Basic connection
ssh user@hostname

# Specify port
ssh -p 2222 user@hostname

# Copy files (SCP)
scp file.txt user@server:/path/
scp user@server:/path/file.txt ./

# Copy directories
scp -r folder/ user@server:/path/

# Generate new key pair
ssh-keygen -t ed25519 -C "your_email@example.com"

# Copy public key to server
ssh-copy-id user@server

# Check SSH connection (verbose)
ssh -v user@server
```

---

## Why SSH is Essential

### Before SSH (Telnet era)
```
Client ──► "password123" ──► Server
         (Sent in PLAIN TEXT!)
         ❌ Anyone monitoring network can see it
```

### With SSH
```
Client ──► "j#$k2@9x..." ──► Server
         (Encrypted gibberish)
         ✅ Useless to eavesdroppers
```

### Real Impact
- **Secure remote administration** - Manage servers from anywhere safely
- **Protected file transfers** - No more FTP vulnerabilities
- **Encrypted tunneling** - Secure other protocols through SSH
- **Industry standard** - Used everywhere in IT/DevOps

---

## Summary

**SSH = Secure way to access remote computers**

**Key Concepts:**
1. **Client-Server model** - Your machine connects to remote server
2. **Encrypted channel** - All data protected from interception
3. **Two auth methods**:
    - Password (simple but less secure)
    - SSH keys (recommended, highly secure)
4. **Public-private key cryptography**:
    - Public key on server (safe to share)
    - Private key on client (keep secret)
    - Challenge-response prevents key transmission
5. **Use cases**: Remote access, file transfers, tunneling

**Why it matters:**
- Foundation of modern DevOps
- Enables secure remote work
- Protects sensitive data in transit
- Essential for cloud infrastructure management

**Bottom line**: SSH turns the dangerous act of remote server access into a secure, encrypted, and authenticated connection! 🔒