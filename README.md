McmmoRankUp
===========

## What this Plugin do ? ##
	 Gives you and players the option to automate your permission systems groups to players on your servers.

Optionally, if you just want a Rank-Up based on internal tags, you don't have use the permissions systems groups.
All of this with your McMMO RPG System.

## Players ##
         Can choose which ability they wish to use as their Base Rank Ability. 

From there, as the player progresses in mcMMO, mcmmoRankUp will detect these changes and promote/demote players within the players chosen rank line.
Players can choose which ability they wish to rank up in and can change them on the fly whenever they want.

mcmmoRankUp will inform players when their current skill has been achieved and is not promotable to any further promotions
within that ability. Your permission groups dictate what a player gets at a specific ability level promotion, if you use
the permissions interface of mcmmoRankUp (suggested if you truely want to automate your promotional aspects of your server
for your players). mcmmoRankUp will also demote players if their skill level falls below the current level requirements
of the current abilities group. 

## For example ##

if the player is currently a level 20 in the ability AXES, and something along the way made the player drops a level or more in AXES from mcMMO, upon AutoRankUp or manually invoking mcmmoRankUp's
rank-up option, the player will be demoted back down the respective level of that ability. Demotions work just like Promotions
and will update the player's permissions group(s) if you use the permissions systems interface. Otherwise the tag of the
player will just demote back down. (see setting up below for more detail on skill, abilities and file requirements)

## Permission on 0.6.5+ ##

<table border="1" >
	<tr>
		<td>Permission</td>
		<td>What For ?</td>
	</tr>
	<tr>
		<td>mru.admin.config</td>
		<td>Update config settings and reload.</td>
	</tr>
	<tr>
		<td>mru.buyrankbuks</td>
		<td>Purchase a rank from their current server worlds money</td>
	</tr>
	<tr>
		<td>mru.buyrankxp</td>
		<td>Purchase a rank from XP or XP Levels</td>
	</tr>
	<tr>
		<td>mru.hability</td>
		<td>Option to set base rank line</td>
	</tr>
	<tr>
		<td>mru.playerfeeds</td>
		<td>mcmmoRankup Player feeds</td>
	</tr>
	<tr>
		<td>mru.rankup</td>
		<td>Autorank or invoke ranking up via cmd /mru rank</td>
	</tr>
	<tr>
		<td>mru.stats</td>
		<td>View current ranking stats, both current and previous</td>
	</tr>
	<tr>
		<td>mru.stats.others</td>
		<td>View another players ranking stats, current and previous</td>
	</tr>
	<tr>
		<td>mru.rankup.*</td>
		<td>Permission to All McMMO Skill Rank Up</td>
	</tr>
</table> 

#### McMMO Permission

if you want allow all skill to rankup use mru.rankup.* or choose what skill will rankup

<table border="1" >
	<tr>
		<td>Permission</td>
		<td>Skill For</td>
	</tr>
	<tr>
		<td>mru.rankup.acrobatics</td>
		<td>Acrobatics skill</td>
	</tr>
        <tr>
        	<td>mru.rankup.archery</td>
        	<td>Archery skill</td>
	</tr>
	<tr>
		<td>mru.rankup.axes</td>
		<td>Axes skill</td>
	</tr>
	<tr>
		<td>mru.rankup.excavation</td>
		<td>Excavation skill</td>
	</tr>
	<tr>
		<td>mru.rankup.fishing</td>
		<td>Fishing skill</td>
	</tr>
	
	<tr>
		<td>mru.rankup.herbalism</td>
		<td>Herbalism skill</td>
	</tr>
	
	<tr>
		<td>mru.rankup.mining</td>
		<td>Mining skill</td>
	</tr>
	
	<tr>
		<td>mru.rankup.powerlevel</td>
		<td>Powerlevel skill</td>
	</tr>
	
	<tr>
		<td>mru.rankup.repair</td>
		<td>Repair skill</td>
	</tr>
	
	<tr>
		<td>mru.rankup.smelting</td>
		<td>Smelting skill</td>
	</tr>
	
	<tr>
		<td>mru.rankup.swords</td>
		<td>Swords skill</td>
	</tr>
	
	<tr>
		<td>mru.rankup.taming</td>
		<td>Taming skill</td>
	</tr>
	
	<tr>
		<td>mru.rankup.unarmed</td>
		<td>Unarmed skill</td>
	</tr>
	<tr>
		<td>mru.rankup.woodcutting</td>
		<td>Woodcutting skill</td>
	</tr>
</table>

#### WildCard Permission

If you want to use wildcard permission, do as follow

##### For Admin

  - mru.admin.*
  	- mru.admin.config
	- mru.buyrankbuks 
	- mru.buyrankxp 
	- mru.hability
	- mru.playerfeeds 
	- mru.rankup
	- mru.stats
	- mru.stats.others
	- mru.rankup.*

##### For Player

  - mru.user.*
	- mru.buyrankbuks
	- mru.buyrankxp
	- mru.hability
	- mru.playerfeeds
	- mru.rankup
	- mru.stats
	- mru.stats.others
	- mru.rankup.*

  - mru.user.simple
	- mru.hability
	- mru.rankup
	- mru.stats
	- mru.rankup.*

##### Others

  - mru.exemptdemotions ( bypass demotions )
  - mru.ignore ( player/group )
