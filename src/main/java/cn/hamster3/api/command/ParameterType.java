package cn.hamster3.api.command;

public enum ParameterType {
    /**
     * 整数
     */
    Integer,
    /**
     * 正整数
     */
    PositiveInteger,
    /**
     * 单精度浮点数
     */
    Float,
    /**
     * 双精度浮点数
     */
    Double,
    /**
     * 正数
     */
    PositiveDouble,
    /**
     * 布尔值
     */
    Boolean,
    /**
     * 玩家，必须在线
     */
    Player,
    /**
     * 玩家，可以为不在线状态
     */
    OfflinePlayer,
}
