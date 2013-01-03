Description

McMMO rank up auto add user to a permission group Base on PowerLevel of mcMMO plugin, once a PowerLevel is reach this plugin change the Group of your permission plugin automatic , you can set individual group / level. You have ability to rank down too, if player drop powerlevel... you choose !

Ex: PowerLevel 100 or more setup for Guest , PowerLevel 200 or more setup for Member or PowerLevel 100 or more setup for Aprendice , PowerLevel 200 or more setup for Novice

Features
Easy to setup
Auto add and remove base on mcMMO powerlevel
Promote and Remove others groups ( for Rank Down )
TODO
Automatic Rank UP/Down Done on 0.3
Ignore a Group Done on 0.3
Add Rank Up/Down For Choose hability ( swords, etc... ) or powerlevel
Add Eco Support ( promote if player has x money - take or not the money )
Config
Message:
  RankUp: Player %player% promote to %group%
  Sucess: Promote Sucess
  Fail: Promote Fail
Config:
  PromoteOnJoin: true
  AutoUpdate: true
  # Work with m for minute and h for hours ( Ex : 1h , 30m )
  AutoUpdateTime: 1m
PlayerToIgnore: Stutiguias2,Player2
GroupToIgnore: Admin2,Moderator2
PowerLevelRankUp:
  '0': default
  '90': 2
  '300': 3
UseAlternativeBroadCast: true
UseThisBroadcast:
  default: rank1
  '2': rank2
  '3': rank3
GroupName are Case Sensitive !

Comands
/mru show ( displays player promotional ladder only ) - v0.6.1+
/mru rank ( checks if they can be promoted to the next rank ) - v0.6.1+
/mru reload ( reload the plugin )

Depends :

Vault 1.2.17 for CB 1.3.1-R1.0 , mcMMO 1.3.11 for CB 1.3.1-R1.0
