<h1><img width=80 src="https://github.com/MrQuackDuck/TicTacToe/assets/61251075/144f1da0-3e0f-4e7d-ac27-cdb1f1539b7c" /> <div>TicTacToe</div></h1>

<p>
  <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Java-gray" /></a>
  <a href="https://hub.spigotmc.org/javadocs/spigot/"><img src="https://img.shields.io/badge/SpigotAPI-orange" /></a>
  <a href="https://github.com/vshymanskyy/StandWithUkraine"><img src="https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/badges/StandWithUkraine.svg"></a>
</p>

 **TicTacToe** is a **Spigot** plugin that allows you to play **Tic Tac Toe** with your friends in inventory-based GUI.

## Screenshots
<img height=300 src="https://github.com/MrQuackDuck/TicTacToe/assets/61251075/cdbdc3c0-2aff-46ab-888b-43e91f86c754" />
<img height=300 src="https://github.com/MrQuackDuck/TicTacToe/assets/61251075/e90e3e7e-52db-49e4-a5b7-a582fef5d130" />

## Commands
- `/ttt info` — shows info about commands.
- `/ttt invite <player>` — invite a player to play.
- `/ttt accept` — accept the invite to play.
- `/ttt reload` — reload the config.

## Permissions

- `tictactoe.admin` - Allows to reload the plugin (with `/ttc reload`)

> [!NOTE]
> Wait for extended permissions in upcoming versions!
</p>

## Default config
```yml
items:
  # See full list on https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html
  "first": "BLUE_DYE"
  "second": "RED_DYE"

messages:
  "prefix": "&#F0F8FF[TicTacToe] &r"
  "info": "&#318CE7Info:\n
  &#318CE7/ttc invite <Player>&#F0F8FF - invite a player to play tic tac toe\n
  &#318CE7/ttc accept&#F0F8FF - accept play request"
  "only-players": "&#CF1020Only players can use this command."
  "command-not-found": "&#CF1020Command wasn't found!"
  "provide-a-name": "&#CF1020Provide the name of a player to invite!"
  "player-wasnt-found": "&#CF1020Player wasn't found!"
  "cant-send-to-yourself": "&#CF1020You can't send a request to yourself!"
  "previous-request-was-deleted": "&#FECB00Your previous request was deleted!"
  "request-sent-successfully": "&#318CE7The request was sent &nsuccessfully!"
  "received-a-request": "&#318CE7You have received a request to play!\nType &n/ttc accept&r&#318CE7 to accept!"
  "request-to-play-wasnt-found": "&#CF1020The request wasn't found!"
  "request-was-accepted": "&#8EE53FYour play request was accepted!"
  "you-accepted-request": "&#8EE53FYou've accepted the request from %s."
  "you-won": "&#8EE53FYou won the game against &n%s&#8EE53F!"
  "you-lost": "&#CF1020You lost against &n%s&#CF1020!"
  "game-ended-draw": "&#FECB00Game ended draw!"
  "opponent-aborted-game": "&#318CE7Your opponent aborted the game! &#8EE53F&nYou are the winner!"
  "you-aborted-game": "&#CF1020You've aborted the game! You lost."

  # Gui messages (that are shown in game inventory)
  "gui-prefix": "[TTC] "
  "gui-player's-turn": "%s's turn!"
  "gui-draw": "Draw!"
  "gui-player-won": "%s won!"

  # Admin messages
  "plugin-reloaded": "&#318CE7Plugin was reloaded!"
  "failed-to-reload": "&#CF1020Plugin was failed to reload."
```

## Getting started

> [!IMPORTANT]
> Before getting started, make sure that the plugin's version is **compatible** with your server version.

1. Download the plugin from <a href="https://github.com/MrQuackDuck/TicTacToe/releases">Releases</a> tab or from <a href="https://www.spigotmc.org/resources/tictactoe.114959/">Spigot</a> page.
1. Put downloaded `.jar` into `/plugins` folder of your server.
1. Restart your server or enter `reload` command.
