# ARP (Address Resolution Protocol) - Complete Video Notes

## **What is ARP?**

**Definition**: Address Resolution Protocol - a protocol used to resolve IP addresses to MAC addresses

**Purpose**: Devices use ARP to acquire the MAC address of another device on a local area network (LAN)

---

## **Why ARP is Needed**

### **The Communication Problem:**

**What devices need to communicate:**
- Devices need **both** IP address AND MAC address
- IP address: Already known (used to locate the device)
- MAC address: Unknown (identifies the actual device)

**Remember:**
- **IP Address** = Used to **locate** a device on the network
- **MAC Address** = **Identifies** the actual device

**Without MAC address, communication cannot happen!**

---

## **How ARP Works - Step by Step**

### **Scenario: Computer A wants to communicate with Computer B**

**Initial State:**
- Computer A knows Computer B's **IP address** ✓
- Computer A doesn't know Computer B's **MAC address** ✗

### **Step 1: Check ARP Cache**

Computer A first checks its internal list called the **ARP cache** to see if it already has Computer B's MAC address stored.

**Check ARP cache in Windows:**
```
Command: arp -a
Result: Empty (no entries)
```

### **Step 2: Send ARP Broadcast**

Since MAC address is not in cache, Computer A sends a **broadcast message** to the entire network:

**Broadcast Message:**
- Sent to: **Every device** on the local network
- Question: "Which computer has IP address X.X.X.X?"
- Request: "Please tell me your MAC address"

### **Step 3: Target Device Responds**

Computer B (the device with the matching IP address) responds:
- Sends back its MAC address to Computer A
- Response is **unicast** (direct reply, not broadcast)

### **Step 4: Communication Begins**

- Computer A now has Computer B's MAC address
- Communication can finally take place!

### **Step 5: Store in ARP Cache**

Computer A stores this information in the ARP cache for future use.

**Check ARP cache again:**
```
Command: arp -a
Result: Shows IP address and matching MAC address
```

---

## **ARP Cache**

### **What is ARP Cache?**

**Definition**: An internal list stored on each device that contains IP address to MAC address associations

**Purpose**: Makes the network more efficient

### **How ARP Cache Improves Efficiency:**

**Without ARP Cache:**
- Every time Computer A wants to talk to Computer B
- Must broadcast to entire network
- Wastes network bandwidth
- Slows down network

**With ARP Cache:**
- Computer A looks in ARP cache first
- Finds MAC address immediately
- No broadcast needed
- Faster communication
- Reduced network traffic

### **ARP Cache Commands (Windows)**

**View ARP cache:**
```
arp -a
```

**Add static entry:**
```
arp -s [IP_ADDRESS] [MAC_ADDRESS]
```

**Example:**
```
arp -s 192.168.1.100 00-1A-2B-3C-4D-5E
```

**Delete entry:**
```
arp -d [IP_ADDRESS]
```

**Clear entire cache:**
```
arp -d *
```

---

## **Types of ARP Entries**

### **1. Dynamic Entries**

**How Created:**
- Created **automatically**
- When device broadcasts ARP request
- When device receives ARP response

**Characteristics:**
- **Not permanent**
- Flushed out periodically
- Prevents cache from filling up with unused entries
- Default behavior for normal network communication

**Lifetime:**
- Windows: Typically 2-10 minutes for unused entries
- Active entries: Refresh when used

**Example:**
```
Internet Address      Physical Address      Type
192.168.1.100        00-1a-2b-3c-4d-5e     dynamic
```

### **2. Static Entries**

**How Created:**
- **Manually entered** by administrator
- Using ARP command-line utility
- Must be explicitly added

**Characteristics:**
- **Permanent** (until manually removed or reboot)
- Don't get flushed automatically
- Survive until deleted or computer restarts

**When to Use:**
- Two devices constantly communicate
- Reduce unnecessary ARP broadcast traffic
- Critical infrastructure (servers, gateways)
- Security (prevent ARP spoofing)

**Example:**
```
Internet Address      Physical Address      Type
192.168.1.1          aa-bb-cc-dd-ee-ff     static
```

---

## **ARP Process Diagram**

```
Computer A (needs to talk to Computer B)
    ↓
1. Check ARP Cache
    ↓
   Found? → YES → Use cached MAC address → Communicate
    ↓
   NO
    ↓
2. Send ARP Broadcast
   "Who has IP 192.168.1.100? Tell me your MAC!"
    ↓
   [Broadcast to ALL devices on network]
    ↓
3. Computer B responds (if it has that IP)
   "I have 192.168.1.100, my MAC is 00-1A-2B-3C-4D-5E"
    ↓
4. Computer A receives MAC address
    ↓
5. Store in ARP Cache
    ↓
6. Communication begins!
```

---

## **ARP Cache Entry States**

| State | Description | Duration |
|-------|-------------|----------|
| **Incomplete** | ARP request sent, waiting for reply | Few seconds |
| **Reachable** | Valid entry, recently confirmed | 2-10 minutes |
| **Stale** | Entry exists but not recently used | Until flushed |
| **Permanent** | Static entry, manually added | Until removed |

---

## **Benefits of ARP Cache**

### **1. Network Efficiency**
- Reduces broadcast traffic
- Faster communication (no waiting for ARP response)
- Lower network congestion

### **2. Performance**
- Immediate MAC address lookup
- No delay for ARP resolution
- Better application response times

### **3. Bandwidth Conservation**
- Fewer broadcasts = less network overhead
- More bandwidth for actual data

---

## **When ARP is Used**

### **Local Network Communication:**
✓ Computer to computer on same subnet
✓ Computer to printer
✓ Computer to router (default gateway)
✓ Any device-to-device on same LAN

### **NOT Used For:**
✗ Communication across routers (different networks)
✗ Internet communication (uses routing)

**Note**: ARP broadcasts don't cross routers - they stay within the local network only

---

## **ARP Broadcast vs Response**

| Aspect | ARP Request (Broadcast) | ARP Response (Unicast) |
|--------|------------------------|------------------------|
| **Destination** | All devices on network | Specific requesting device |
| **Type** | Broadcast | Unicast |
| **Purpose** | Find MAC address | Provide MAC address |
| **Sender** | Device needing MAC | Device with that IP |
| **Destination MAC** | FF-FF-FF-FF-FF-FF | Requester's MAC |

---

## **Real-World Example**

### **Scenario: Accessing a Network Printer**

**Step-by-step:**

1. **User sends print job**
    - Computer knows printer IP: 192.168.1.50
    - Doesn't know printer MAC address

2. **Check ARP cache**
   ```
   arp -a
   # No entry for 192.168.1.50
   ```

3. **ARP broadcast**
    - "Who has 192.168.1.50? Tell 192.168.1.10 (my IP)"
    - Sent to all devices on network

4. **Printer responds**
    - "I'm 192.168.1.50, my MAC is AA-BB-CC-DD-EE-FF"

5. **Computer stores and uses**
   ```
   arp -a
   # Now shows: 192.168.1.50  aa-bb-cc-dd-ee-ff  dynamic
   ```

6. **Print job sent using MAC address**

7. **Future print jobs**
    - Computer checks cache first
    - Finds MAC immediately
    - No broadcast needed!

---

## **Static ARP Entry Use Cases**

### **1. Frequently Communicating Devices**
```
Example: Workstation → File Server
Static entry eliminates repeated ARP broadcasts
```

### **2. Network Infrastructure**
```
Example: All computers → Default Gateway
Static entry for router reduces ARP traffic
```

### **3. Critical Services**
```
Example: Servers → Database Server
Ensures consistent, fast communication
```

### **4. Security**
```
Prevent ARP spoofing attacks
Lock down MAC addresses for critical devices
```

---

## **ARP Cache Maintenance**

### **Why Cache Entries are Flushed:**

**Problems with permanent entries:**
- Cache fills up with too many entries
- Unused entries waste memory
- Devices may change (replaced, moved)
- MAC addresses could change (rare but possible)

**Solution: Dynamic entries expire**
- Inactive entries removed after timeout
- Active entries refreshed automatically
- Keeps cache clean and relevant

### **Typical Timeout Values:**

| OS | Unused Entry | Active Entry |
|-----|--------------|--------------|
| **Windows** | 2 minutes | 10 minutes |
| **Linux** | 60 seconds | 20 minutes |
| **Cisco** | 4 hours | 4 hours |

---

## **Administrator Benefits of Static Entries**

### **1. Reduced ARP Traffic**
- No broadcasts for known devices
- Less network congestion
- Better performance

### **2. Predictable Behavior**
- Known MAC-IP associations
- Easier troubleshooting
- Consistent communication

### **3. Security Enhancement**
- Prevent ARP poisoning
- Lock critical device associations
- Detect unauthorized changes

---

## **Key Takeaways**

1. **ARP resolves IP addresses to MAC addresses** on local networks

2. **ARP cache stores IP-to-MAC mappings** for efficiency

3. **Check cache first** before broadcasting

4. **ARP broadcasts** go to all devices on local network

5. **Only matching device responds** with its MAC address

6. **Two entry types:**
    - Dynamic: Automatic, temporary
    - Static: Manual, permanent

7. **Static entries reduce broadcast traffic** for frequently communicating devices

8. **Cache entries expire** to prevent memory issues

9. **ARP only works on local networks** (doesn't cross routers)

10. **Use `arp -a` command** to view ARP cache

---

## **Quick Command Reference**

```bash
# View ARP cache
arp -a

# View specific entry
arp -a [IP_ADDRESS]

# Add static entry
arp -s [IP_ADDRESS] [MAC_ADDRESS]

# Delete entry
arp -d [IP_ADDRESS]

# Delete all entries
arp -d *
```

---

## **Summary Flow**

```
Need to communicate with device
    ↓
Know IP address? → NO → Use DNS first
    ↓ YES
Check ARP cache
    ↓
MAC found in cache? → YES → Use it, communicate
    ↓ NO
Send ARP broadcast
    ↓
Receive MAC address
    ↓
Store in ARP cache (dynamic entry)
    ↓
Communication begins
    ↓
Entry expires after timeout (unless static)
```

This protocol is fundamental to all local network communication and works transparently behind the scenes every time devices talk to each other!