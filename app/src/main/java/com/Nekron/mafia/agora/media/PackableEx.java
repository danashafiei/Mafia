package com.Nekron.mafia.agora.media;

public interface PackableEx extends Packable{
    void unmarshal(ByteBuf in);
}