# HamsterAPI

## 快速设计GUI
```yaml
title: "&c自定义界面"

graphic:
  - "####1234#"
  - "####5678#"
  - "#~#qwert#"
  - "###asdfg#"
  - "###zxcvb#"

items:
  '#':
    ==: org.bukkit.inventory.ItemStack
    type: STAINED_GLASS_PANE
    damage: 4
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: "§c屏障"
  '1':
    ==: org.bukkit.inventory.ItemStack
    type: PAPER
    amount: 1
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: "§c1级保护符"
      lore:
        - "§c为你的装备添加3星淬炼保护"
  '2':
    ==: org.bukkit.inventory.ItemStack
    type: PAPER
    amount: 2
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: "§c2级保护符"
      lore:
        - "§c为你的装备添加6星淬炼保护"
  '3':
    ==: org.bukkit.inventory.ItemStack
    type: PAPER
    amount: 3
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: "§c3级保护符"
      lore:
        - "§c为你的装备添加9星淬炼保护"
  '4':
    ==: org.bukkit.inventory.ItemStack
    type: PAPER
    amount: 4
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: "§c4级保护符"
      lore:
        - "§c为你的装备添加12星淬炼保护"
  '5':
    ==: org.bukkit.inventory.ItemStack
    type: COAL
    amount: 1
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: "§c1级淬炼"
      lore:
        - "§c成功率20%"
  '6':
    ==: org.bukkit.inventory.ItemStack
    type: COAL
    amount: 2
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: "§c2级淬炼"
      lore:
        - "§c成功率30%"
  '7':
    ==: org.bukkit.inventory.ItemStack
    type: COAL
    amount: 3
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: "§c3级淬炼"
      lore:
        - "§c成功率40%"
  '8':
    ==: org.bukkit.inventory.ItemStack
    type: COAL
    amount: 4
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: "§c4级淬炼"
      lore:
        - "§c成功率50%"
  'q':
    ==: org.bukkit.inventory.ItemStack
    type: INK_SACK
    damage: 4
    amount: 1
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d1级青灵石'
      lore:
        - "§d为装备安装1级青灵石"
  'w':
    ==: org.bukkit.inventory.ItemStack
    type: INK_SACK
    damage: 4
    amount: 2
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d2级青灵石'
      lore:
        - "§d为装备安装2级青灵石"
  'e':
    ==: org.bukkit.inventory.ItemStack
    type: INK_SACK
    damage: 4
    amount: 3
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d3级青灵石'
      lore:
        - "§d为装备安装3级青灵石"
  'r':
    ==: org.bukkit.inventory.ItemStack
    type: INK_SACK
    damage: 4
    amount: 4
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d4级青灵石'
      lore:
        - "§d为装备安装4级青灵石"
  't':
    ==: org.bukkit.inventory.ItemStack
    type: INK_SACK
    damage: 4
    amount: 5
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d5级青灵石'
      lore:
        - "§d为装备安装5级青灵石"
  'a':
    ==: org.bukkit.inventory.ItemStack
    type: INK_SACK
    damage: 15
    amount: 1
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d1级烟灵玉'
      lore:
        - "§d为装备安装1级烟灵玉"
  's':
    ==: org.bukkit.inventory.ItemStack
    type: INK_SACK
    damage: 15
    amount: 2
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d2级烟灵玉'
      lore:
        - "§d为装备安装2级烟灵玉"
  'd':
    ==: org.bukkit.inventory.ItemStack
    type: INK_SACK
    damage: 15
    amount: 3
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d33级烟灵玉'
      lore:
        - "§d为装备安装3级烟灵玉"
  'f':
    ==: org.bukkit.inventory.ItemStack
    type: INK_SACK
    damage: 15
    amount: 4
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d4级烟灵玉'
      lore:
        - "§d为装备安装4级烟灵玉"
  'g':
    ==: org.bukkit.inventory.ItemStack
    type: INK_SACK
    damage: 15
    amount: 5
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d5级烟灵玉'
      lore:
        - "§d为装备安装5级烟灵玉"
  'z':
    ==: org.bukkit.inventory.ItemStack
    type: GHAST_TEAR
    amount: 1
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d1级水汐石'
      lore:
        - "§d为装备安装1级水汐石"
  'x':
    ==: org.bukkit.inventory.ItemStack
    type: GHAST_TEAR
    amount: 2
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d2级水汐石'
      lore:
        - "§d为装备安装2级水汐石"
  'c':
    ==: org.bukkit.inventory.ItemStack
    type: GHAST_TEAR
    amount: 3
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d3级水汐石'
      lore:
        - "§d为装备安装3级水汐石"
  'v':
    ==: org.bukkit.inventory.ItemStack
    type: GHAST_TEAR
    amount: 4
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d4级水汐石'
      lore:
        - "§d为装备安装4级水汐石"
  'b':
    ==: org.bukkit.inventory.ItemStack
    type: GHAST_TEAR
    amount: 5
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '§d5级水汐石'
      lore:
        - "§d为装备安装5级水汐石"
```

## 以及其他更多集成方法
请参考[HamsterAPI.java](https://github.com/ViosinDeng/HamsterAPI/blob/master/src/main/java/cn/hamster3/api/HamsterAPI.java)
