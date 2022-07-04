# Autosort

## _Importaint Note:<br />This plugin is not finished yet!<br />The first release will come out in around a month_

## About the Project

You host a Minecraft-Server with paper, bukkit or spigot and have issues with large sorting systems? Use this mod, to give players an oportunity to use a serverfriendly storage system!

This Project is developed by <a href="https://github.com/Quantum625">@Quantum625</a> and <a href="https://github.com/Schwerthecht">@Schwerthecht</a>. The idea came up, as we searched for such a plugin and there was none. Hopefully we can help you with this Project.



## How to use

Most commands and their functionalities can be seen in <code>/autosort help</code>


### What is a network?

A network is a group of chests that are linked together

The chests can be one of 3 different types:<br />
- Input Chest - Items put in here will be transfered to a fitting Item chest or others chests<br />
- Item Chest - Items will automatically get sorted into this chests, if they match the chest's item filter<br />
- Others Chest - If there are no free Item Chests for a specific item, that item will instead be transfered to a others chest

You can create networks by doing
<br /><code>/as create (name)</code>

The (name) is the ID of your network<br />
You need it to edit or delete your network.

By default, only the creator and admins have editing permissions


### How do I add chests to my network?

To add chests to your network simply just do the following

<code>/as chest input (xyz)</code><br />
<code>/as chest item (xyz) (item)</code><br />
<code>/as chest others (xyz)</code><br />

(xyz) is the position of the chest<br />
(item) is the item, that will be sorted into your item chest

To remove a chest from your network just do:

<code>/as chest remove (xyz)</code>

### List of all Commands

<code>/as create (name)</code> - Create a storage network<br />
<code>/as delete (name)</code> - Delete a storage network

<code>/as select (name)</code> - Select a storage network<br />

<code>/as info</code> - Show information about the selected network

## Installation

<a href="https://github.com/Schwerthecht/autosort/release/stable/Autosort-1.0.0.jar">Download the newest version of the plugin</a>

Put it in your plugins folder inside your server files.

Restart the server and the plugin should show a loading message in the console.