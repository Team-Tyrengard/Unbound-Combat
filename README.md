# Unbound Combat
Ever wanted more complex combat mechanics in Minecraft? Do you want to provide a more RPG-esque combat experience for your server? If yes, then **Unbound Combat is the plugin for you!** Featuring an impressive overhaul of vanilla Minecraft's combat system, complete with stats, attributes, and even support for skills!

This is a project that was started in 2019, and later became the flagship plugin for the Tyrengard server. Despite overriding the combat system, we did our best to make it feel as vanilla as possible, so it's not jarring to play with. We even crafted preset config values to make it feel as vanilla as possible.

**WARNING: Unbound Combat overrides Minecraft's vanilla combat system, so it may not play so well with other combat-focused plugins (unless they support this plugin). You have been warned!**

## Features
1. **Combat stats** - 
   Customize everything from melee and ranged damage, health, base resistances, attack and movement speeds, and more! We even introduce a chance-based critical strike system (no more repetitive jump crits), evasion, and physical and magic damage types! Customize how they work in the plugin's config file.
   
2. **Combat attributes** - 
   We implemented 7 attributes (Strength, Agility, Dexterity, Constitution, Perception, Intellect, and Wisdom) that each give additional combat stats and give players a way to progress and get better in combat. **NOTE: Combat attributes and combat leveling is disabled by default.**
   
3. **Combat logs** - 
   We store a history of combat for each player, and they can brag about it in global chat! See the details of your most recent battles, including how much damage you dodged, and how many times you hit those sweet 150% damage crits!

4. **Party system** -
   Do you find it lonely adventuring alone, but your friends keep taking the best loot and all the experience? Introducing the party system! You can set BOTH items and experience to be shared across the team! No more salty dungeon raids!

5. **GUI-based commands** -
   All our commands are completely GUI-based and intuitive! Get the full MMORPG experience!
   
6. **Support for combat skills** (with MagicksAPI) - 
   If you install [MagicksAPI](https://github.com/Team-Tyrengard/MagicksAPI) and a compatible combat skills plugin (or [write one, it's not that hard](https://github.com/Team-Tyrengard/MagicksAPI/wiki)), you can use MagicksAPI's casting system to spice up PvP and PvE. We even have combat stats (mana, mana regen, spell potency, etc.) and combat attributes (Intellect and Wisdom) that specifically augment this.
   
7. **Combat equipment system** (with Unbound Combat Gear) - 
   With Unbound Combat Gear, you can hook into the combat engine and create items with innate combat stats and skills. Create your dream "max evasion" full armor set!
   
8. **Custom mob support** (with Unbound Combat Mobs) - 
   You can create custom mobs with jacked combat stats in Unbound Combat Mobs, give them OP abilities, and watch your players suffer! Or make fair and engaging mobs. Your choice, really.
   
9. **An API for everything we just mentioned** - 
   Want to write your own custom mob plugin and have compatible with Unbound Combat? Or maybe you already have one and are looking to make use of Unbound Combat's overhauled combat engine? Guess what: you can! Unbound Combat fires custom events for the beginning and end of every combat, and for each instance of damage. You can customize each step of the damage calculation to your heart's content! You can even make your own skills plugin or custom item plugin compatible with Unbound Combat. Hooray for APIs!

## Supported versions
Currently, Unbound Combat only supports Minecraft 1.18 and above. If enough requests for supporting lower versions are given, we may retrofit the code to support that version. You can also submit pull requests if you manage to retrofit the code yourself!

## Installation and configuration
You can get the latest version of the plugin via the latest release link on the right, and place it in your `/plugins` folder. After that, you can customize values in the plugin's config file, or just roll with our recommended presets and enjoy a different yet familiar combat experience!

## Wiki
Click [here](https://github.com/Team-Tyrengard/Unbound-Combat/wiki) to visit the wiki.

## Translations
Translations will be supported soon!

## API and integration
The Unbound Combat API with allows you to:
* Add your own [combat skills](https://github.com/Team-Tyrengard/Unbound-Combat/wiki) (with or without MagicksAPI),
* Imbue your own [custom items](https://github.com/Team-Tyrengard/Unbound-Combat/wiki) with combat stats
* Create your own [combatants](https://github.com/Team-Tyrengard/Unbound-Combat/wiki) for custom mobs and/or players, and
* Listen for [combat events](https://github.com/Team-Tyrengard/Unbound-Combat/wiki) such as combat beginning or ending, damage instances resolving, skills activating, etc.

Simply add the plugin as a dependency via Maven:

```xml
<dependencies>
    ...
    <dependency>
      <groupId>com.tyrengard.unbound</groupId>
      <artifactId>unbound-combat</artifactId>
      <version>1.0</version>
      <scope>provided</scope>
    </dependency>
    ...
</dependencies>
```
To use SNAPSHOT versions, add the Sonatype Snapshots repository:
```xml
<repositories>
    ...
    <repository>
        <id>sonatype-snapshots</id>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    ...
</repositories>
```

## Demo
--- under construction ---

## Community
You can join the [Team Tyrengard Discord server](https://discord.gg/4Zct7WmYUD) to ask us questions, get help, report bugs, or request features! You can also click [here](https://github.com/Team-Tyrengard/Unbound-Combat/issues/new/choose) to submit a new issue.