# 🌐 Subnetting Cheatsheet

## 📌 Basics
- IPv4 address = 32 bits → divided into 4 octets (8 bits each).
- Each octet = 0–255 → 256 total values.
- First IP in a subnet = **Network Address** (not usable).
- Last IP in a subnet = **Broadcast Address** (not usable).
- Usable Hosts = **2^n – 2** (where `n` = number of host bits).

---

## 📊 Common Subnets

| CIDR | Subnet Mask       | Total IPs | Usable Hosts | Example Range                   |
|------|------------------|-----------|--------------|---------------------------------|
| /30  | 255.255.255.252  | 4         | 2            | 192.168.1.0 – 192.168.1.3       |
| /29  | 255.255.255.248  | 8         | 6            | 192.168.1.0 – 192.168.1.7       |
| /28  | 255.255.255.240  | 16        | 14           | 192.168.1.0 – 192.168.1.15      |
| /27  | 255.255.255.224  | 32        | 30           | 192.168.1.0 – 192.168.1.31      |
| /26  | 255.255.255.192  | 64        | 62           | 192.168.1.0 – 192.168.1.63      |
| /25  | 255.255.255.128  | 128       | 126          | 192.168.1.0 – 192.168.1.127     |
| /24  | 255.255.255.0    | 256       | 254          | 192.168.1.0 – 192.168.1.255     |
| /23  | 255.255.254.0    | 512       | 510          | 192.168.0.0 – 192.168.1.255     |
| /22  | 255.255.252.0    | 1024      | 1022         | 192.168.0.0 – 192.168.3.255     |
| /21  | 255.255.248.0    | 2048      | 2046         | 192.168.0.0 – 192.168.7.255     |
| /20  | 255.255.240.0    | 4096      | 4094         | 192.168.0.0 – 192.168.15.255    |
| /16  | 255.255.0.0      | 65,536    | 65,534       | 192.168.0.0 – 192.168.255.255   |

---

## ✅ Quick Tips
- **Usable hosts = Total IPs − 2** (for network & broadcast).
- Smaller subnets (e.g., `/30`) are used for point-to-point links.
- Larger subnets (e.g., `/16`) are for big networks.


# 📌 Why `/24` = 256 Addresses (254 Usable Hosts)

## Step 1: What does `/24` mean?
- IPv4 address = 32 bits.
- `/24` = first **24 bits** are **network bits**, last **8 bits** are **host bits**.
- Subnet mask: `255.255.255.0`
    - Binary: `11111111.11111111.11111111.00000000`

---

## Step 2: Host Bits
- Host bits = `32 − prefix_length`
- For `/24`: `32 − 24 = 8 host bits`

---

## Step 3: Total Addresses
- Each host bit can be `0` or `1`.
- Total = **2^host_bits**
- For `/24`: `2^8 = 256 total addresses`

---

## Step 4: Usable Hosts
- Out of 256:
    - **1** = Network Address (all host bits = 0)
    - **1** = Broadcast Address (all host bits = 1)
- Usable hosts = `256 − 2 = 254`

---

## Example: `192.168.1.0/24`
- **Network Address**: `192.168.1.0`
- **Usable Range**: `192.168.1.1 – 192.168.1.254`
- **Broadcast Address**: `192.168.1.255`

---

✅ **Quick Rule:**
- `/24` = 8 host bits → `2^8 = 256 total IPs`
- Minus 2 (network + broadcast) → **254 usable hosts**
