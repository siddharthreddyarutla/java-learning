## When Using Multiple Wi-Fi Routers (Without WAPs)

### Each Router Creates Its Own Subnet

**Example Setup:**
```
Modem → Main Router (192.168.1.x subnet)
     → Wi-Fi Router 1 (192.168.2.x subnet)
     → Wi-Fi Router 2 (192.168.3.x subnet)
     → Wi-Fi Router 3 (192.168.4.x subnet)
```

### The Problem
- **Subnet 1**: Devices on Router 1 get IPs like 192.168.2.10, 192.168.2.11
- **Subnet 2**: Devices on Router 2 get IPs like 192.168.3.10, 192.168.3.11
- **Subnet 3**: Devices on Router 3 get IPs like 192.168.4.10, 192.168.4.11

**Issues:**
- Devices on different routers are on **different subnets**
- Harder for devices to communicate with each other
- More complex network management
- Each router acts as a separate gateway
- Network administrator must configure each router individually

---

## When Using Wireless Access Points

### All Devices on SAME Subnet

**Example Setup:**
```
Modem → Router (192.168.1.x subnet - ONLY subnet)
     → WAP 1 ┐
     → WAP 2 ├─→ All devices get 192.168.1.x addresses
     → WAP 3 ┘
```

### The Advantage
- **Single Subnet**: All devices (wired and wireless) get IPs like 192.168.1.10, 192.168.1.11, 192.168.1.12
- WAPs are "transparent" - they just relay traffic
- All devices can easily communicate
- Single router handles all DHCP, routing, and management
- Much simpler network architecture

---

## Real-World Example

### Office with Multiple Wi-Fi Routers (Bad):
```
Main Router: 192.168.1.x
├─ Desktop 1: 192.168.1.10 (wired to main router)
├─ Router A: Creates 192.168.2.x network
│  └─ Laptop 1: 192.168.2.50
└─ Router B: Creates 192.168.3.x network
   └─ Laptop 2: 192.168.3.50
```

**Problem**: Laptop 1 (192.168.2.50) trying to print to a printer connected to Desktop 1 (192.168.1.10) = **Complicated routing needed**

### Office with Wireless Access Points (Good):
```
Router: 192.168.1.x (only subnet)
├─ Desktop 1: 192.168.1.10 (wired)
├─ WAP 1 → Laptop 1: 192.168.1.50
└─ WAP 2 → Laptop 2: 192.168.1.51
```

**Solution**: All devices on same subnet = **Easy communication, simple management**

---

## Key Takeaway

- **Multiple Routers** = Multiple subnets = Network complexity
- **Single Router + Multiple WAPs** = Single subnet = Network simplicity

This is why organizations prefer WAPs - they maintain a **unified, flat network** that's much easier to manage!