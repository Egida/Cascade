# Cascade

A lightweight proxy architecture that provides secure authentication, encrypted tunneling, and
Minecraft-native Layer 7 DDoS protection — all without requiring mods or offline mode on the backend.

![startup](https://github.com/Steinimfluss/Cascade/blob/main/images/status.png)
> **Important Requirement**  
> Your backend server **must support Minecraft transfer packets and cookie-based authentication**.  
> This means Cascade only works with **Minecraft versions that include the official transfer system**  
> (1.20.5+ and newer).  
> Older versions **will not work**, because they do not support the required packet flow.  
> You must create a firewall rule to only accept request from the Cascade node on your backend!

## Features
- Handles status requests

- Cookie-based authentication flow

- Mojang account verification

- Encrypted tunneling to backend

- No backend mods required

- Works with a single proxy node

- Minecraft-specific Layer 7 DDoS protection

- Escalating punishments for invalid packets and improper logins

- Identity filtering


## How It Works
1. Client connects to the proxy.
2. If the cookie token is invalid:
   
   - Proxy authenticates the player with Mojang.
   - Issues a valid cookie token.
     
   - Client reconnects.
     
4. If the cookie token is valid:
   
   - Proxy tunnels the encrypted connection to the backend.
     
   - Backend sees a normal, signed Minecraft client.

## Why This Beats Traditional Layer 7 DDoS Protection

- No packets ever reach the real backend until the client has authenticated with Mojang servers
- Impossible to spoof or fake tokens
  
- Backend doesn't require any mods or plugins
  
- Works with offline mode
  
- Works even with a single node

It is recommended to change the JWT secret immediately when going into production.  
If multiple nodes are connected through an Anycast network, all nodes must use the same JWT secret to ensure proper functionality.

## License
MIT
