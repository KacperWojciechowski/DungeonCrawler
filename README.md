# DungeonCrawler

# Idea

University project for a Master's degree Network Programming class. It's a CLI based client-server game where a player goes through a randomly generated dungeon, collecting loot and fighting enemies. 

# Features

- [ ] Custom randomized map generation using a binary map and a graph adjacency list representation
> NOTE: created this algorithm while working on the very first version of Graph project. It was not uploaded to github at the time, as it was relatively shoddy made university classes project
- [ ] Communication between server and client using sockets and custom-made messages, serialized to JSON format
- [ ] Validation of actions occur on the server side, so the game can be easily hosted online, without a risk of players modding it or cheating (hardcoded IP change required in such a case)
- [ ] A few loot types increasing player's statistics
- [ ] Three example enemies with different health pool and attacks
- [ ] User posesses special ability which can be used in combat consuming mana. Mana is restored by finding a mana potion
