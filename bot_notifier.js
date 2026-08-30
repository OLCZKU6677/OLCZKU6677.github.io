/**
 * Discord Bot Notifier — Jakub (olczku)
 * Bot do automatycznego wysyłania powiadomień o odwiedzinach portfolio na Twój serwer Discord.
 *
 * Token bota: MTU0MzYyNTMxMzE1Njg2MjA2NA.G5UMc-.KqmVMNngqWRMvMDqKXUO86P-frFUg2OSe357Ik
 */

const BOT_TOKEN = "MTU0MzYyNTMxMzE1Njg2MjA2NA.G5UMc-.KqmVMNngqWRMvMDqKXUO86P-frFUg2OSe357Ik";

// Podaj ID kanału na swoim serwerze Discord, gdzie bot ma wysyłać powiadomienia:
// (Włącz tryb dewelopera w Discordzie, kliknij prawym na kanał -> Kopiuj ID kanału)
const NOTIFICATION_CHANNEL_ID = process.env.DISCORD_CHANNEL_ID || "TUTAJ_WKLEJ_ID_KANALU";

process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

async function sendDiscordNotification(channelId, visitorInfo) {
  if (!channelId || channelId === "TUTAJ_WKLEJ_ID_KANALU") {
    console.log("[Bot] Ustaw DISCORD_CHANNEL_ID w pliku bot_notifier.js lub zmiennych środowiskowych.");
    return;
  }

  const now = new Date().toLocaleString('pl-PL', { timeZone: 'Europe/Warsaw' });
  const embed = {
    title: "👀 Ktoś właśnie wszedł na Twoje portfolio!",
    url: "https://olczku6677.github.io",
    color: 0x00f5a0,
    fields: [
      { name: "📍 Lokalizacja", value: `🏙️ **${visitorInfo.city || 'Nieznane'}**, ${visitorInfo.country || 'Polska'}`, inline: true },
      { name: "🌐 Adres IP", value: `\`${visitorInfo.ip || 'Nieznane'}\``, inline: true },
      { name: "🏢 Dostawca (ISP)", value: visitorInfo.org || visitorInfo.isp || "Brak danych", inline: true },
      { name: "💻 Urządzenie / System", value: `🖥️ **${visitorInfo.os || 'Windows'}** (${visitorInfo.screen || '1920x1080'})`, inline: true },
      { name: "🧭 Przeglądarka", value: `🔍 **${visitorInfo.browser || 'Chrome'}**`, inline: true },
      { name: "🔗 Źródło", value: `\`${visitorInfo.referrer || 'Bezpośrednie'}\``, inline: true },
      { name: "🕒 Czas wejścia", value: `⏱️ ${now}`, inline: false }
    ],
    footer: {
      text: "System powiadomień portfolio • Jakub (olczku)"
    }
  };

  try {
    const res = await fetch(`https://discord.com/api/v10/channels/${channelId}/messages`, {
      method: "POST",
      headers: {
        "Authorization": `Bot ${BOT_TOKEN}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ embeds: [embed] })
    });

    if (res.ok) {
      console.log(`[Bot] Powiadomienie wysłane pomyślnie na kanał ${channelId}!`);
    } else {
      const err = await res.json();
      console.error(`[Bot] Błąd wysyłania:`, err);
    }
  } catch (e) {
    console.error(`[Bot] Błąd połączenia z Discordem:`, e.message);
  }
}

console.log("=== DISCORD BOT NOTIFIER URUCHOMIONY ===");
console.log("Link do zaproszenia bota na Twój serwer Discord:");
console.log("👉 https://discord.com/oauth2/authorize?client_id=1543625313156862064&permissions=2048&scope=bot\n");

module.exports = { sendDiscordNotification };
