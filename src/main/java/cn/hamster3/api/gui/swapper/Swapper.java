package cn.hamster3.api.gui.swapper;

import org.bukkit.inventory.ItemStack;

public interface Swapper {
    /**
     * 需要将何种字符转换成何种ItemStack
     *
     * @param c 字符
     * @return ItemStack 返回的ItemStack
     */
    ItemStack getItemStackByChar(char c);
}
