# Networks Plugin

## About the Project

You host a Minecraft-Server with paper, bukkit or spigot and have issues with large sorting systems? Use this plugin, to give players an oportunity to use a serverfriendly storage system!

This Project is developed by <a href="https://github.com/Quantum625">@Quantum625</a> and <a href="https://github.com/Schwerthecht">@Schwerthecht</a>. The idea came up, as we searched for such a plugin and there was none. Hopefully we can help you with this Project.


### Links
[Discord](https://discord.gg/wQXKdtVPMd)

[Modrinth](https://modrinth.com/plugin/networks)

[Hangar](https://hangar.papermc.io/NanoFlux/Networks)

[Bukkit](https://dev.bukkit.org/projects/networks)




### What is a network?

A network is a group of chests that are linked together

The containers can be one of 3 different types:<br />
- Input Container - Items put in here will be transfered to a fitting item container or misc container<br />
- Item Container - Items will automatically get sorted into these chests, if they match the chest's item filter<br />
- Miscellaneous Container - If there are no free item containers for a specific item, that item will instead be transfered to one of these

You can create networks by doing
<br /><code>/net create (name)</code>

The (name) is the ID of your network<br />
You need it to edit or delete your network.

By default, only the creator and admins have editing permissions, but you can grant other players permission using
`/net user add (name)`
and remove their permission using `/net user remove (name)`


### How do I add chests to my network?

To add chests to your network, first select your network using <code>/net select (name)</code>, then you need to craft the wanted chest type and simply place it

To add a filter item to an item chest, you can 

(item) is the item, that will be sorted into your item chest

To remove a chest from your network, simply just break it


## More information

Most commands and their functionalities can be seen in <code>/networks help</code>
See a more detailed instruction at the [wiki](https://github.com/nanoflux/networks/wiki)


## Installation

[Download the newest version of the plugin](https://modrinth.com/plugin/networks/versions)

Put it in your plugins folder inside your server files.

Restart the server and the plugin should show a loading message in the console.
