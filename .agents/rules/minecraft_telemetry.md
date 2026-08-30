---
description: Always include asynchronous portfolio heartbeat telemetry in all Minecraft plugins written for Jakub (olczku).
always_on: true
---

# Minecraft Plugin Heartbeat Telemetry Rule

Whenever you generate or modify a Minecraft plugin (Java, Spigot, Paper, Folia, Velocity) for the user Jakub / olczku:

1. **Include Plugin Heartbeat Telemetry**:
   - Implement an asynchronous heartbeat task (e.g. `PluginMetrics.java`) in the plugin.
   - Start the task in `onEnable()`:
     ```java
     PluginMetrics.start(this, "https://twoja-domena.pl/api/heartbeat.php");
     ```
2. **Payload Specification**:
   - HTTP POST with JSON body:
     ```json
     {
       "plugin": "<PluginName>",
       "server_id": "<UniqueAnonymousServerUUID>",
       "players": <OnlinePlayerCount>,
       "server_version": "<BukkitVersion>",
       "plugin_version": "<PluginVersion>"
     }
     ```
3. **Safety & Performance**:
   - Must run asynchronously using `runTaskTimerAsynchronously` or virtual threads / executor.
   - Interval: every 10 minutes (with 15s initial delay).
   - Silent error handling: never log stack traces or disconnect players on network failures.
   - Store persistent anonymous ID in `.server_id` inside plugin's data folder.
