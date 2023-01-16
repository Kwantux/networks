# Networks Plugin

## About the Project

You host a Minecraft-Server with paper, bukkit or spigot and have issues with large sorting systems? Use this plugin, to give players an oportunity to use a serverfriendly storage system!

This Project is developed by <a href="https://github.com/Quantum625">@Quantum625</a> and <a href="https://github.com/Schwerthecht">@Schwerthecht</a>. The idea came up, as we searched for such a plugin and there was none. Hopefully we can help you with this Project.


### Links
[Modrinth](https://modrinth.com/plugin/networks)

[Bukkit](https://dev.bukkit.org/projects/networks)

Since Curseforge staff takes longer to view source code, updates usually arrive earlier on Modrinth.


## How to use

Most commands and their functionalities can be seen in <code>/networks help</code>
See a more detailed instruction at the [wiki](https://github.com/Quantum625/networks/wiki)


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

To add chests to your network simply just do the following and then right click the block you want to add as a component

`/net component input` <br />
`/net component sorting (item)` <br />
`/net component misc` <br />

(item) is the item, that will be sorted into your item chest

To remove a chest from your network, simply just break it


### List of all Commands

`/net create (name)` - Create a storage network<br />
`/net delete (name)` - Delete a storage network

`/net select (name)` - Select a storage network<br />

`/net list` - List all networks you have permission on
`/net info` - Show information about the selected network

`/net component` - Add components to your network


## Installation

[Download the newest version of the plugin](https://github.com/Quantum625/networks/releases/tag/v1.0.0)

Put it in your plugins folder inside your server files.

Restart the server and the plugin should show a loading message in the console.
