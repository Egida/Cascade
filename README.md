# Cascade AntiBot (Beta)

Cascade is an experimental anti‑bot system designed for modern Minecraft proxy environments.  
It is currently **in beta** and **not recommended for production servers**.

## How It Works

Cascade inserts itself into the player's connection path before they reach your real server.  
The flow looks like this:

1. Client connects to the proxy
2. Cascade intercepts the connection and redirects the player, while disabling Mojang authentication, to a lightweight limbo server
3. The Cascade server performs a series of bot detection checks
4. If the client passes, Cascade issues a transfer packet sending them to the real server
5. If the client fails, the connection never reaches your backend

This approach blocks bots before they can join your lobby or overload your backend or your proxy with authentication requests.

## Installation

### 1. Install the Proxy Plugin
Place the Cascade plugin JAR into your proxy’s `plugins/` folder.

### 2. Configure the Lobby (Optional)
If you want to run the Cascade verification server separately, set:

```bash
cascade = "127.0.0.1:<port>"
```

### 3. Start the Cascade Server (Standalone Mode)
If running separately:

```bash
java -jar server.jar <port>
```
