# Subnet Mask - Complete Video Notes

## **What is an IP Address?**

**Definition**: An identifier for a computer or device on a network

**IPv4 Address Structure:**
- 32-bit numeric address
- Written as four numbers separated by periods (e.g., 192.168.1.0)
- Each group is called an **octet**
- Number range per octet: **0-255**

**Two Parts of an IP Address:**
1. **Network Address (Network ID)**: Identifies the network itself (unique to each network)
2. **Host Address (Host ID)**: Identifies individual devices within that network (computers, servers, tablets, routers, etc.)

---

## **What is a Subnet Mask?**

**Definition**: A number that resembles an IP address and reveals which portion of the IP address is the network part vs the host part

**Purpose**: Masks the network portion of the IP address to distinguish between network and host addresses

---

## **Binary Conversion - How Computers Read IP Addresses**

**Key Concept**: Computers only understand binary (1s and 0s), not decimal format

### **8-Bit Octet Chart:**
```
128 | 64 | 32 | 16 | 8 | 4 | 2 | 1
```

**How it works:**
- Each bit can be either 1 or 0
- If bit = 1, the number counts
- If bit = 0, the number doesn't count
- By manipulating 1s and 0s, you get any number from 0-255

### **Example: Converting 192 to Binary**
```
128 + 64 = 192
Binary: 11000000
```

### **Example: Converting 168 to Binary**
```
128 + 32 + 8 = 168
Binary: 10101000
```

### **Example: Full IP Address Conversion**
```
IP: 192.168.1.0
Binary: 11000000.10101000.00000001.00000000
```

### **Subnet Mask Example (255.255.255.0)**
```
255 = all 1s in octet
Binary: 11111111.11111111.11111111.00000000
```

---

## **How Subnet Masks Identify Network vs Host Portions**

**Rule**:
- **1s in subnet mask** = Network portion of IP address
- **0s in subnet mask** = Host portion of IP address

### **Example 1: 255.255.255.0**
```
IP:     192.168.1.0
Mask:   255.255.255.0
Result: First 3 octets = Network, Last octet = Host
```

### **Example 2: 255.255.0.0**
```
Result: First 2 octets = Network, Last 2 octets = Host
```

### **Example 3: 255.0.0.0**
```
Result: First octet = Network, Last 3 octets = Host
```

### **Example 4: Custom Mask (255.255.224.0)**
```
224 in binary = 11100000 (first 3 bits are 1s)
Result: First 2 octets + first 3 bits of third octet = Network
        Remaining 13 bits = Host
```

---

## **Why Networks Need Network and Host Portions**

**Answer**: **Manageability** - To break large networks into smaller subnetworks (subnetting)

### **The Broadcast Problem:**

**Without Subnetting (One Large Network):**
- When a computer wants to communicate, it sends a **broadcast** to ALL computers
- Every computer receives every broadcast
- Results in:
    - Network chaos
    - Slow performance
    - Potential network halt
    - Difficult troubleshooting

**Solution: Use Routers to Create Subnets**
- Routers physically separate networks
- **Broadcasts don't cross routers** - they stay within a subnet
- Data between subnets goes through the router (default gateway)
- Router intelligently routes data to destination

---

## **Subnetting Example**

### **Scenario**: Small business with 12 computers, 3 departments

**Original Setup:**
- IP: 192.168.1.0
- Subnet Mask: 255.255.255.0
- Result: 1 network with 254 usable hosts (256 - 2 reserved)

**Goal**: Create 3 separate networks (one per department)

### **How Subnetting Works:**
**Borrow bits from the host portion** to create more networks

### **Borrowing Bits Chart:**

| Bits Borrowed | New Subnet Mask | Networks Created | Hosts per Network |
|---------------|-----------------|------------------|-------------------|
| 0 bits | 255.255.255.0 | 1 | 254 |
| 1 bit | 255.255.255.128 | 2 | 126 |
| 2 bits | 255.255.255.192 | 4 | 62 |
| 3 bits | 255.255.255.224 | 8 | 30 |
| 4 bits | 255.255.255.240 | 16 | 14 |
| 5 bits | 255.255.255.248 | 32 | 6 |
| 6 bits | 255.255.255.252 | 64 | 2 |
| 7 bits | 255.255.255.254 | 128 | 0 (unusable) |

**Pattern:**
- More bits borrowed = More networks, Fewer hosts per network
- Networks **double** with each bit
- Hosts per network get **cut in half** with each bit

**Solution for 3 Networks:**
- Borrow 2 bits → Creates 4 subnets (minimum needed for 3 departments)
- New subnet mask: **255.255.255.192**
- Result: 4 networks with 62 hosts each

---

## **IP Address Classes**

### **Commercial Classes (A, B, C):**

| Class | First Octet Range | Default Subnet Mask | Max Hosts | Typical Use |
|-------|-------------------|---------------------|-----------|-------------|
| **A** | 1-126 | 255.0.0.0 (/8) | 16 million | ISPs, very large orgs |
| **B** | 128-191 | 255.255.0.0 (/16) | 65,000 | Medium to large orgs |
| **C** | 192-223 | 255.255.255.0 (/24) | 254 | Small orgs, homes |

**Class D**: 224-239 (Multicast)
**Class E**: 240-255 (Experimental)

### **Why Different Classes?**
Organizations get IP address classes based on the number of hosts they need:
- **Class A**: Internet Service Providers (millions of customers)
- **Class B**: Medium/large organizations
- **Class C**: Small businesses and homes

---

## **CIDR Notation (Slash Notation)**

**CIDR** = Classless Inter-Domain Routing

**Definition**: Shorter way to write subnet masks by counting the number of 1s

### **Examples:**

| CIDR Notation | Subnet Mask | Number of 1s |
|---------------|-------------|--------------|
| /8 | 255.0.0.0 | 8 bits |
| /16 | 255.255.0.0 | 16 bits |
| /24 | 255.255.255.0 | 24 bits |
| /25 | 255.255.255.128 | 25 bits |
| /26 | 255.255.255.192 | 26 bits |

**Example**:
- `192.168.1.0/24` means subnet mask is 255.255.255.0 (24 ones)
- `192.168.1.0/25` means subnet mask is 255.255.255.128 (25 ones)

---

## **Key Takeaways**

1. **Subnet masks separate network and host portions** of IP addresses
2. **Computers only read binary** (1s and 0s), not decimal
3. **1s in subnet mask** = network portion, **0s** = host portion
4. **Subnetting prevents broadcast storms** by dividing large networks
5. **Routers separate subnets** - broadcasts don't cross routers
6. **More subnets = fewer hosts per subnet** (trade-off)
7. **Classes A, B, C** are based on organization size
8. **CIDR notation** is a shorthand for subnet masks

---

## **Quick Reference: Binary Octet Values**
```
128  64  32  16  8  4  2  1
----------------------------
255 = 11111111 (all bits on)
128 = 10000000 (first bit only)
192 = 11000000 (first two bits)
224 = 11100000 (first three bits)
240 = 11110000 (first four bits)
248 = 11111000 (first five bits)
252 = 11111100 (first six bits)
254 = 11111110 (first seven bits)
```