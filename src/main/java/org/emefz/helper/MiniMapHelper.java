package org.emefz.helper;

import org.spongepowered.api.text.Text;

public abstract class MiniMapHelper {
    public static Text disableMinimap(){
        return Text.of(sendToMinimap(0));
    }
    public static Text enableMinimap(){
        return Text.of(sendToMinimap(1));
    }
    public static Text enableEntityPlayerRadar(){
        return Text.of(sendToMinimap(1025));
    }
    private static String sendToMinimap(Integer code){
        StringBuilder toReturn = new StringBuilder("\u00a7c \u00a7r\u00a75 \u00a7r\u00a71 \u00a7r\u00a7f");
        String strCode = String.valueOf(code);
        for(int i = 0; i < strCode.length(); ++i){
            toReturn.append(" ").append("\u00a7r\u00a7").append(strCode.charAt(i));
        }
        return toReturn + " ";
    }
}
