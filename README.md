# Cascade

A lightweight proxy architecture that provides secure authentication, encrypted tunneling, and
Minecraft-native Layer 7 DDoS protection — all without requiring mods or offline mode on the backend.

## Features
- Cookie-based authentication flow

- Mojang account verification

- Encrypted tunneling to backend

- No backend mods required

- Works with a single proxy node

- Minecraft-specific Layer 7 DDoS protection

- Identity-first filtering (not heuristics)


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

## Running via Command Line (Recommended)

Cascade can be launched directly from the command line.  
This makes it easy to run immediately after downloading the built JAR.

```bash
java -jar cascade.jar --proxyPort 25564 --targetHost localhost --targetPort 25565 --jwtSecret CHANGE_ME_IMMEDIATELY_THIS_IS_NOT_SECURE
```

It is recommended to change the JWT secret immediately when going into production.  
If multiple nodes are connected through an Anycast network, all nodes must use the same JWT secret to ensure proper functionality.

## License
MIT
