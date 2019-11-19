# HamsterAPI
**让你更便捷地设计你的GUI**
**只需给出GUI图形**
```
String[] s = {
"*@@@@@@@*",
"*@%%%%%@*",
"*@%###%@*",
"*@%###%@*",
"*@%%%%%@*",
"*@@@@@@@*"} ;
```
**以及你的字符转换器**
```Java
class Swap implements Swapper {
    @Override
    public ItemStack getItemStackByChar(Character character) {
        switch (character) {
            case '*':
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 8);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemStack.setItemMeta(itemMeta);
            return itemStack;
            case '@':
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 9);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§c边栏");
            itemStack.setItemMeta(itemMeta);
            return itemStack;
            case '#':
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 10);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§c护符");
            itemStack.setItemMeta(itemMeta);
            return itemStack;
            case '%':
            ItemStack itemStack = new ItemStack(Material.DIAMOND);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§d&l核心");
            itemStack.setItemMeta(itemMeta);
            return itemStack;
            default:
                return null;
        }
    }
}
```
（写成匿名内部类的形式更好看23333）

**然后你就可以快速获取一个GUI了！**
`Inventory inv = HamsterAPI.getInventory(this, null, "§cGUI测试", new Swap(), s);`
是不是很直观！！！以后你造GUI都不需要再反复进游戏测试了有木有！！！

---
# 匿名内部类写法
```Java
public class Main implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("guitest")) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                if(!p.isOp()) {
                    p.sendMessage("只有OP可以执行这个命令!");
                    return true;
                }
                String[] graphic = {
                        "*@@@@@@@*",
                        "*@%%%%%@*",
                        "*@%###%@*",
                        "*@%###%@*",
                        "*@%%%%%@*",
                        "*@@@@@@@*"} ;
                //字符串图形
                Inventory inv = GUIMakerAPI.getInventory(this, null, "§cGUI测试", new Swapper() {
                    @Override
                    public ItemStack getItemStackByChar(Character character) {
                        if(character == '*') {
                            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE,(short) 8);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName("§c边栏");
                            itemStack.setItemMeta(itemMeta);
                            return itemStack;
                        }
                        if(character == '@') {
                            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE,(short) 9);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName("§c边栏");
                            itemStack.setItemMeta(itemMeta);
                            return itemStack;
                        }
                        if(character == '%') {
                            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE,(short) 10);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName("§c护符");
                            itemStack.setItemMeta(itemMeta);
                            return itemStack;
                        }
                        if(character == '#') {
                            ItemStack itemStack = new ItemStack(Material.DIAMOND);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName("§b§l核心");
                            itemStack.setItemMeta(itemMeta);
                            return itemStack;
                        }
                        return null;
                    }
                },graphic);
                return true;
            }
        }
        return false;
    }
}
```

嘛...至少比一个一个加东西进去要方便2333