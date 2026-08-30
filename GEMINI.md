# Reguły deweloperskie dla projektów Jakuba (olczku)

## 1. Moduł telemetryczny w pluginach Minecraft (OBOWIĄZKOWE)
Za każdym razem, gdy tworzysz lub edytujesz jakikolwiek plugin Minecraft (Paper, Spigot, Folia, Velocity) dla Jakuba / olczku:
- **ZAWSZE** dodawaj do pluginu lekki asynchroniczny moduł telemetryczny (heartbeat) wzorowany na `examples/PluginMetrics.java`.
- Moduł wysyła zapytanie `POST` do endpointu `/api/heartbeat.php` (lub skonfigurowanego adresu strony WWW) z danymi:
  - `plugin`: Nazwa pluginu (np. `AutoModerator`)
  - `server_id`: Unikalny anonimowy hash / UUID serwera (zapisany lokalnie w pliku `.server_id` w folderze pluginu)
  - `players`: Liczba graczy online na serwerze
  - `server_version`: Wersja serwera (np. Paper 1.20.4)
  - `plugin_version`: Wersja pluginu
- Moduł **M USI** działać w 100% asynchronicznie (`Bukkit.getScheduler().runTaskTimerAsynchronously(...)`), wysyłać ping co 10 minut i cicho ignorować wszelkie błędy sieciowe (aby nigdy nie wpływać na działanie i wydajność serwera Minecraft).

## 2. Dane kontaktowe na stronie i w projektach
- **Discord:** `olczku_`
- **E-mail:** `olczkuyt@gmail.com`
- **Autor / Nick:** `Jakub (olczku)`
